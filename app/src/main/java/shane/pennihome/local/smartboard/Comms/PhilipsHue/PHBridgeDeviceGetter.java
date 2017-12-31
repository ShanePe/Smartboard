package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.Interface.OnCommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Data.HueBridgeToken;
import shane.pennihome.local.smartboard.Data.Routine;

/**
 * Created by shane on 31/12/17.
 */

public class PHBridgeDeviceGetter  extends AsyncTask<String, String, RESTCommunicatorResult> {
    private final Context mContext;
    private final OnProcessCompleteListener<PHBridgeDeviceGetter> mProcessCompleteListener;
    private ProgressDialog mDialog;
    private boolean mSuccess;
    private final boolean mIncludeRoutine;
    private List<Device> mDevices;
    private List<Routine> mRoutines;

    public PHBridgeDeviceGetter(Context mContext,boolean mIncludeRoutine, OnProcessCompleteListener<PHBridgeDeviceGetter> mProcessCompleteListener) {
        this.mContext = mContext;
        this.mProcessCompleteListener = mProcessCompleteListener;
        this.mIncludeRoutine = mIncludeRoutine;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = false;
        mDevices = null;
        if (mIncludeRoutine)
            mRoutines = null;

        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Get Devices/Routines from to Hue Bridge...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult restCommunicatorResult) {
        super.onPostExecute(restCommunicatorResult);
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        try {
            if (!restCommunicatorResult.isSuccess())
                throw restCommunicatorResult.getException();


            mSuccess = true;

        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, "Could not connect to the Hue Bridge", Toast.LENGTH_SHORT).show();
        }

        if (mProcessCompleteListener != null)
            mProcessCompleteListener.Complete(mSuccess, this);
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... strings) {
        RESTCommunicatorResult result = new RESTCommunicatorResult();
        try {
            HueBridgeToken hueBridgeToken = HueBridgeToken.Load();

            String url = "http://" + hueBridgeToken.getAddress() + "/api/" + hueBridgeToken.getToken() + "/lights";
            final JSONArray devices = new JSONArray();
            RESTCommunicator restCommunicator = new RESTCommunicator();
            JSONObject jDevices = restCommunicator.getJson(url, null);

            Iterator<String> iterator = jDevices.keys();
            while(iterator.hasNext()) {
                String k = iterator.next();
                JSONObject jDev = jDevices.getJSONObject(k);
                jDev.put("id", k);
                devices.put(jDev);
            }

            JSONObject jRet = new JSONObject();
            jRet.put("devices", devices);

            if(mIncludeRoutine) {
                url = "http://" + hueBridgeToken.getAddress() + "/api/" + hueBridgeToken.getToken() + "/groups";
                final JSONArray groups = new JSONArray();
                JSONObject jGroup = restCommunicator.getJson(url, null);
                iterator = jGroup.keys();

                while(iterator.hasNext()) {
                    String k = iterator.next();
                    JSONObject jGp = jGroup.getJSONObject(k);
                    JSONArray jLight = jGp.getJSONArray("lights");
                    String sLightKey = "";
                    for(int i=0;i<jLight.length();i++)
                        sLightKey += jLight.getString(i);
                    jGp.put("id", k);
                    jGp.put("lkey", sLightKey);
                    groups.put(jGp);
                }

                url = "http://" + hueBridgeToken.getAddress() + "/api/" + hueBridgeToken.getToken() + "/scenes";
                final JSONArray routines = new JSONArray();
                JSONObject jRoutine = restCommunicator.getJson(url, null);

                iterator = jRoutine.keys();
                while(iterator.hasNext()) {
                    String k = iterator.next();
                    JSONObject jRt = jRoutine.getJSONObject(k);
                    JSONArray jLight = jRt.getJSONArray("lights");
                    String sLightKey = "";
                    for(int i=0;i<jLight.length();i++)
                        sLightKey += jLight.getString(i);

                    jRt.put("id", k);
                    jRt.put("lkey", sLightKey);
                    routines.put(jRt);
                }

                jRet.put("groups", groups);
                jRet.put("routines", routines);
            }

            result.setResult(jRet);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        return result;
    }

    public List<Device> getDevices() {
        return mDevices;
    }

    public List<Routine> getRoutines() {
        return mRoutines;
    }
}
