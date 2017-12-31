package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnCommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsToken;
import shane.pennihome.local.smartboard.Data.Sort.DeviceSorter;
import shane.pennihome.local.smartboard.Data.Sort.RoutineSorter;

@SuppressLint("StaticFieldLeak")
public class STDevicesGetter extends AsyncTask<String, String, RESTCommunicatorResult> {
    private final String mUriRequest;
    private ProgressDialog mDialog;
    private List<Device> mDevices;
    private List<Routine> mRoutines;
    private boolean mSuccess;
    private final boolean mIncludeRoutine;
    private final Context mContext;

    private final OnProcessCompleteListener<STDevicesGetter> mProcessCompleteListener;

    public STDevicesGetter(String uriRequest, @SuppressWarnings("SameParameterValue") boolean includeRoutine, Context context, OnProcessCompleteListener<STDevicesGetter> processCompleteListener) {
        mUriRequest = uriRequest;
        mIncludeRoutine = includeRoutine;
        mContext = context;
        mProcessCompleteListener = processCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        mDevices = null;
        if (mIncludeRoutine)
            mRoutines = null;

        mSuccess = false;
        super.onPreExecute();
        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Getting devices/routines from SmartThings ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... args) {               //DevicesGet

        RESTCommunicatorResult ret = new RESTCommunicatorResult();
        SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
        final JSONArray devices = new JSONArray();
        final JSONArray routines = new JSONArray();

        RESTCommunicator coms = new RESTCommunicator();
        try {
            JSONObject jRet = new JSONObject();

            coms.getJson(mUriRequest + "/switches", smartThingsTokenInfo.getToken(), new OnCommResponseListener() {
                @Override
                public void Process(JSONObject obj) {
                    devices.put(obj);
                }
            });
            jRet.put("devices", devices);

            if (mIncludeRoutine) {
                coms.getJson(mUriRequest + "/routines", smartThingsTokenInfo.getToken(), new OnCommResponseListener() {
                    @Override
                    public void Process(JSONObject obj) {
                        routines.put(obj);
                    }
                });
                jRet.put("routines", routines);
            }

            ret.setResult(jRet);

        } catch (Exception e) {
            ret.setException(e);
        }

        return ret;
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult result) {              //DevicesGet
        if (mDialog != null) {
            mDialog.dismiss();
            mDevices = null;
        }
        try {
            if (!result.isSuccess())
                throw result.getException();

            mDevices = new ArrayList<>();

            JSONObject jres = result.getResult();
            JSONArray devices = jres.getJSONArray("devices");
            for (int i = 0; i < devices.length(); i++) {
                JSONObject jDev = devices.getJSONObject(i);

                Device d = new Device();
                d.setId(jDev.getString("id"));
                d.setName(jDev.getString("name"));
                d.setOn(jDev.getString("value").equals("on"));
                d.setType(jDev.getString("type"));
                d.setSource(Thing.Source.SmartThings);
                mDevices.add(d);
            }
            Collections.sort(mDevices, new DeviceSorter());

            if (mIncludeRoutine) {
                mRoutines = new ArrayList<>();
                JSONArray routines = jres.getJSONArray("routines");
                for (int i = 0; i < routines.length(); i++) {
                    JSONObject jRout = routines.getJSONObject(i);

                    Routine r = new Routine();
                    r.setId(jRout.getString("id"));
                    r.setName(jRout.getString("name"));
                    r.setSource(Thing.Source.SmartThings);
                    mRoutines.add(r);
                }
            }
            Collections.sort(mRoutines, new RoutineSorter());

            mSuccess = true;
        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, "Could not get SmartThing Devices", Toast.LENGTH_SHORT).show();
            mSuccess = false;
        }

        if (mProcessCompleteListener != null)
            mProcessCompleteListener.Complete(mSuccess, this);
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    public List<Routine> getRoutines() {
        return mRoutines;
    }

    @SuppressWarnings("unused")
    public Boolean getSuccess() {
        return mSuccess;
    }
}