package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.content.Context;

import org.json.JSONObject;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;

public class STEndPointGetter extends ICommunicator<STEndPointGetter> {

    STEndPointGetter(Context mContext, OnProcessCompleteListener<STEndPointGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    @Override
    public String getFailedMessage() {
        return "Could not contact SmartThings Cloud";
    }

    @Override
    public String getDialogMessage() {
        return "Contacting SmartThings ...";
    }

    @Override
    public JSONObject Process() throws Exception {
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
        RESTCommunicator httpCommunicator = new RESTCommunicator();
        return httpCommunicator.getJson(Globals.ST_ENDPOINT_URL, tokenSmartThingsInfo.getToken(), null);
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
        tokenSmartThingsInfo.setRequestUrl(result.getResult().getString("uri"));
        tokenSmartThingsInfo.Save();
    }
}