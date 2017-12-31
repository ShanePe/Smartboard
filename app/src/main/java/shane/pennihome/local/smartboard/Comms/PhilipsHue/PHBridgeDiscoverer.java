package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnCommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.HueBridge;

/**
 * Created by shane on 30/12/17.
 */

public class PHBridgeDiscoverer extends AsyncTask<String, String, RESTCommunicatorResult> {
    private ProgressDialog mDialog;
    private boolean mSuccess;
    private final Context mContext;
    private List<HueBridge> mBridgeDiscoveryResults;
    private final OnProcessCompleteListener<PHBridgeDiscoverer> mProcessCompleteListener;

    public PHBridgeDiscoverer(Context mContext, OnProcessCompleteListener<PHBridgeDiscoverer> processCompleteListener) {
        this.mContext = mContext;
        mProcessCompleteListener = processCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBridgeDiscoveryResults = null;
        mSuccess = false;
        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Scanning for Hue Bridges on the network...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... strings) {
        final RESTCommunicatorResult result = new RESTCommunicatorResult();

        try {
            JSONObject jRet = new JSONObject();
            final JSONArray bridges = new JSONArray();
            RESTCommunicator httpCommunicator = new RESTCommunicator();
            httpCommunicator.getJson(Globals.PH_DISCOVER_URL, new OnCommResponseListener() {
                @Override
                public void Process(JSONObject obj) {
                    bridges.put(obj);
                }
            });
            jRet.put("bridges", bridges);
            result.setResult(jRet);

        }catch (Exception e)
        {
            result.setException(e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult comResult) {
        super.onPostExecute(comResult);

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        try {
            if (!comResult.isSuccess())
                throw comResult.getException();

            mBridgeDiscoveryResults = new ArrayList<>();

            JSONObject jres = comResult.getResult();
            JSONArray bridges = jres.getJSONArray("bridges");
            for (int i = 0; i < bridges.length(); i++) {
                JSONObject jBrid = bridges.getJSONObject(i);
                HueBridge b = new HueBridge();
                b.setId(jBrid.getString("id"));
                b.setIp(jBrid.getString("internalipaddress"));

                mBridgeDiscoveryResults.add(b);
            }
            mSuccess = true;

        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, "Could not discover Hue Bridges", Toast.LENGTH_SHORT).show();
        }

        if (mProcessCompleteListener != null)
            mProcessCompleteListener.Complete(mSuccess, this);
    }

    public List<HueBridge> getBridgeDiscoveryResults() {
        return mBridgeDiscoveryResults;
    }

    public void setBridgeDiscoveryResults(List<HueBridge> bridgeDiscoveryResults) {
        mBridgeDiscoveryResults = bridgeDiscoveryResults;
    }
}


