package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.CommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;
import shane.pennihome.local.smartboard.Data.Sort.DeviceSorter;
import shane.pennihome.local.smartboard.Data.Sort.RoutineSorter;

public class STDevicesGetter extends AsyncTask<String, String, ComResult> {
    private String mUriRequest;
    private ProgressDialog mDialog;
    private List<Device> mDevices;
    private List<Routine> mRoutines;
    private boolean mSuccess;
    private boolean mIncludeRoutine;
    private Context mContext;

    private ProcessCompleteListener<STDevicesGetter> _processCompleteListener;

    public STDevicesGetter(String uriRequest, boolean includeRoutine, Context context, ProcessCompleteListener<STDevicesGetter> processCompleteListener) {
        mUriRequest = uriRequest;
        mIncludeRoutine = includeRoutine;
        mContext = context;
        _processCompleteListener = processCompleteListener;
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
    protected ComResult doInBackground(String... args) {               //DevicesGet

        ComResult ret = new ComResult();
        String path = "switches";
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        final JSONArray devices = new JSONArray();
        final JSONArray routines = new JSONArray();

        HttpCommunicator coms = new HttpCommunicator();
        try {
            JSONObject jRet = new JSONObject();

            coms.getJson(mUriRequest + "/switches", smartThingsTokenInfo.getToken(), new CommResponseListener() {
                @Override
                public void Process(JSONObject obj) {
                    devices.put(obj);
                }
            });
            jRet.put("devices", devices);

            if (mIncludeRoutine) {
                coms.getJson(mUriRequest + "/routines", smartThingsTokenInfo.getToken(), new CommResponseListener() {
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
    protected void onPostExecute(ComResult result) {              //DevicesGet
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
                Toast.makeText(mContext, "Communication Request Error", Toast.LENGTH_SHORT).show();
            mSuccess = false;
        }

        if (_processCompleteListener != null)
            _processCompleteListener.Complete(mSuccess, this);
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    public List<Routine> getRoutines() {
        return mRoutines;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }
}