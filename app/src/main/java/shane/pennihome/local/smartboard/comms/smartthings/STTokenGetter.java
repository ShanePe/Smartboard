package shane.pennihome.local.smartboard.comms.smartthings;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shane.pennihome.local.smartboard.comms.RESTCommunicator;
import shane.pennihome.local.smartboard.comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.data.ITokenSmartThings;


@SuppressWarnings("unused")
@SuppressLint("StaticFieldLeak")
public class STTokenGetter extends ICommunicator<STTokenGetter> {

    public STTokenGetter(Context mContext, OnProcessCompleteListener<STTokenGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    @Override
    public String getFailedMessage() {
        return "Could not get SmartThings Token";
    }

    @Override
    public String getDialogMessage() {
        return "Getting SmartThings Token ...";
    }

    @Override
    public JSONObject Process() throws Exception {
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();

        RESTCommunicator coms = new RESTCommunicator();

        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("code", tokenSmartThingsInfo.getAuthCode()));
        params.add(new NameValuePair("client_id", Globals.ST_CLIENT_ID));
        params.add(new NameValuePair("client_secret", Globals.ST_CLIENT_SECRET));
        params.add(new NameValuePair("redirect_uri", Globals.ST_REDIRECT_URI));
        params.add(new NameValuePair("grant_type", Globals.ST_GRANT_TYPE));

        @SuppressWarnings("unused") RESTCommunicatorResult ret = new RESTCommunicatorResult();
        return coms.postJson(Globals.ST_TOKEN_URL, params);
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();
        tokenSmartThingsInfo.setToken(result.getResult().getString("access_token"));
        tokenSmartThingsInfo.setType(result.getResult().getString("token_type"));

        int minutes = Integer.parseInt(result.getResult().getString("expires_in"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, minutes);
        tokenSmartThingsInfo.setExpires(c.getTime());

        tokenSmartThingsInfo.Save();

    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... args) {
        Log.i(Globals.ACTIVITY, "doInBackground");
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();
        RESTCommunicator coms = new RESTCommunicator();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("code", tokenSmartThingsInfo.getAuthCode()));
        params.add(new NameValuePair("client_id", Globals.ST_CLIENT_ID));
        params.add(new NameValuePair("client_secret", Globals.ST_CLIENT_SECRET));
        params.add(new NameValuePair("redirect_uri", Globals.ST_REDIRECT_URI));
        params.add(new NameValuePair("grant_type", Globals.ST_GRANT_TYPE));

        RESTCommunicatorResult ret = new RESTCommunicatorResult();
        try {
            ret.setResult(coms.postJson(Globals.ST_TOKEN_URL, params));
        } catch (Exception e) {
            ret.setException(e);
        }

        return ret;
    }
}

