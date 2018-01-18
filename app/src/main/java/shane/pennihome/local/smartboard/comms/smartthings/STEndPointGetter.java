package shane.pennihome.local.smartboard.comms.smartthings;

import android.content.Context;

import org.json.JSONObject;

import shane.pennihome.local.smartboard.comms.RESTCommunicator;
import shane.pennihome.local.smartboard.comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.ITokenSmartThings;

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
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();
        RESTCommunicator httpCommunicator = new RESTCommunicator();
        return httpCommunicator.getJson(Globals.ST_ENDPOINT_URL, tokenSmartThingsInfo.getToken(), null);
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();
        tokenSmartThingsInfo.setRequestUrl(result.getResult().getString("uri"));
        tokenSmartThingsInfo.Save();
    }
}