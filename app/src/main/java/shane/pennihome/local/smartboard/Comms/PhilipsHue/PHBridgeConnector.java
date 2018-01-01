package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.content.Context;

import org.json.JSONObject;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.TokenHueBridge;

/**
 * Created by shane on 31/12/17.
 */

@SuppressWarnings("ALL")
public class PHBridgeConnector extends ICommunicator<PHBridgeConnector> {
    PHBridgeConnector(Context mContext, OnProcessCompleteListener<PHBridgeConnector> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    @Override
    public String getFailedMessage() {
        return "Could not connect to the Hue Bridge";
    }

    @Override
    public String getDialogMessage() {
        return "Connecting to Hue Bridge...";
    }

    @Override
    public JSONObject Process() throws Exception {
        TokenHueBridge tokenHueBridge = TokenHueBridge.Load();

        String url = "http://" + tokenHueBridge.getAddress() + "/api";

        JSONObject req = new JSONObject("{\"devicetype\":\"" + Globals.ACTIVITY + "#" +
                Globals.getSharedPreferences().getString("uid","unknown") + "\"}");

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
        JSONObject jSuc;

        if (jObj.has("success")) {
            jSuc = jObj.getJSONObject("success");
        } else
            throw new Exception("Did not get authorisation for Hue Bridge");

        return jSuc;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        if (!result.isSuccess())
            throw result.getException();

        TokenHueBridge tokenHueBridge = TokenHueBridge.Load();
        tokenHueBridge.setToken(result.getResult().getString("username"));
        tokenHueBridge.Save();
    }
}
