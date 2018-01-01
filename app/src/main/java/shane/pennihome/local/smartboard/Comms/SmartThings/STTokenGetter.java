package shane.pennihome.local.smartboard.Comms.SmartThings;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.NameValuePair;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;


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
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();

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
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
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
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
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

