package shane.pennihome.local.smartboard.Comms.SmartThings;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.NameValuePair;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;


public class STTokenGetter extends AsyncTask<String, String, ComResult> {
    private ProgressDialog pDialog;
    private Boolean _success;
    private ProcessCompleteListener<STTokenGetter> _procComplete;

    public STTokenGetter(ProcessCompleteListener<STTokenGetter> procComplete) {
        _procComplete = procComplete;
    }

    @Override
    protected void onPreExecute() {
        _success = false;
        super.onPreExecute();
        pDialog = new ProgressDialog(Globals.getContext());
        pDialog.setMessage("Contacting SmartThings ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        //Code = pref.getString("Code", "");
        pDialog.show();
    }

    @Override
    protected ComResult doInBackground(String... args) {
        Log.i(Globals.ACTIVITY, "doInBackground");
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        HttpCommunicator coms = new HttpCommunicator();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new NameValuePair("code", smartThingsTokenInfo.getAuthCode()));
        params.add(new NameValuePair("client_id", Globals.CLIENT_ID));
        params.add(new NameValuePair("client_secret", Globals.CLIENT_SECRET));
        params.add(new NameValuePair("redirect_uri", Globals.REDIRECT_URI));
        params.add(new NameValuePair("grant_type", Globals.GRANT_TYPE));

        /** GET requests to https://graph.api.smartthings.com/oauth/token will also work, but POST is preferred.
         ** That will return a response like:
         **  {
         **      "access_token": "XXXXXXXXXXX",
         **      "expires_in"  : 1576799999,
         **      "token_type"  : "bearer"
         **  }
         **/
        ComResult ret = new ComResult();
        try {
            ret.setResult(coms.postJson(Globals.TOKEN_URL, params));
        } catch (Exception e) {
            ret.setException(e);
        }

        return ret;
    }

    @Override
    protected void onPostExecute(ComResult result) {
        pDialog.dismiss();
        try {
            if (!result.isSuccess())
                throw result.getException();
            SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
            smartThingsTokenInfo.setToken(result.getResult().getString("access_token"));
            smartThingsTokenInfo.setType(result.getResult().getString("token_type"));

            int minutes = Integer.parseInt(result.getResult().getString("expires_in"));
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, minutes);
            smartThingsTokenInfo.setExpires(c.getTime());

            smartThingsTokenInfo.Save();
            _success = true;
            Log.e("avancement : ", "Get EndPoint ");
        } catch (Exception ex) {
            Toast.makeText(Globals.getContext(), "Token Get Error", Toast.LENGTH_SHORT).show();
        }

        if(_procComplete != null)
            _procComplete.Complete(_success, this);
    }

    public Boolean getSuccess() {
        return _success;
    }
}

