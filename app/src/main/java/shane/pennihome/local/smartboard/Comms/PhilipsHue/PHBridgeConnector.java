package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Data.HueBridgeToken;

/**
 * Created by shane on 31/12/17.
 */

public class PHBridgeConnector extends AsyncTask<String, String, RESTCommunicatorResult> {
    private final Context mContext;
    private final OnProcessCompleteListener<PHBridgeConnector> mProcessCompleteListener;
    private ProgressDialog mDialog;
    private boolean mSuccess;
    private HueBridge mConnectHueBridge;

    public PHBridgeConnector(Context mContext, OnProcessCompleteListener<PHBridgeConnector> mProcessCompleteListener) {
        this.mContext = mContext;
        this.mProcessCompleteListener = mProcessCompleteListener;
    }

    private void UpdateDialog(final String msg) {
        if (mContext == null)
            return;

        Handler mainHandler = new Handler(mContext.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if (mDialog == null)
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                else
                    mDialog.setMessage(msg);
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... strings) {
        RESTCommunicatorResult result = new RESTCommunicatorResult();

        try {
            HueBridgeToken hueBridgeToken = HueBridgeToken.Load();

            String url = "http://" + hueBridgeToken.getAddress() + "/api";
            JSONObject req = new JSONObject("{\"devicetype\":\"" + Globals.ACTIVITY + "#" + Build.SERIAL + "\"}");
            RESTCommunicator httpsCommunicator = new RESTCommunicator();
            JSONObject jObj = httpsCommunicator.postJson(url, req);

            if (jObj.has("error")) {
                JSONObject jError = jObj.getJSONObject("error");

                int loopCount = 0;
                int errorCode = jError.getInt("type");
                while (errorCode == 101) {
                    if (loopCount > 36)
                        throw new Exception("Timeout waiting for authorisation push.");

                    UpdateDialog("Please press the link button on the Hue Bridge.");
                    Thread.sleep(5000);

                    jObj = httpsCommunicator.postJson(url, req);
                    if (jObj.has("error")) {
                        jError = jObj.getJSONObject("error");

                        errorCode = jError.getInt("type");
                        if (errorCode != 101)
                            throw new Exception(jError.getString("description"));
                    } else
                        break;
                    loopCount += 1;
                }
            }

            if (jObj.has("success")) {
                JSONObject jSuc = jObj.getJSONObject("success");
                result.setResult(jSuc);
            } else
                throw new Exception("Did not get authorisation for Hue Bridge");

        } catch (Exception ex) {
            result.setException(ex);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mConnectHueBridge = null;
        mSuccess = false;

        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Connecting to Hue Bridge...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult httpsCommunicatorResult) {
        super.onPostExecute(httpsCommunicatorResult);

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        try {
            if (!httpsCommunicatorResult.isSuccess())
                throw httpsCommunicatorResult.getException();

            mConnectHueBridge = HueBridge.FromTokenInfo(HueBridgeToken.Load());
            mConnectHueBridge.setToken(httpsCommunicatorResult.getResult().getString("username"));

            mSuccess = true;

        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, "Could not connect to the Hue Bridge", Toast.LENGTH_SHORT).show();
        }

        if (mProcessCompleteListener != null)
            mProcessCompleteListener.Complete(mSuccess, this);
    }

    public HueBridge getConnectHueBridge() {
        return mConnectHueBridge;
    }

    public void setConnectHueBridge(HueBridge connectHueBridge) {
        mConnectHueBridge = connectHueBridge;
    }
}
