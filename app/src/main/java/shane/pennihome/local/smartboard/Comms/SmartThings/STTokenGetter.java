package shane.pennihome.local.smartboard.Comms.SmartThings;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.NameValuePair;
import shane.pennihome.local.smartboard.Data.SmartThingsToken;


@SuppressLint("StaticFieldLeak")
public class STTokenGetter extends AsyncTask<String, String, RESTCommunicatorResult> {
    private ProgressDialog mDialog;
    private Boolean mSuccess;
    private final OnProcessCompleteListener<STTokenGetter> mProcComplete;
    private final Context mContext;

    public STTokenGetter(OnProcessCompleteListener<STTokenGetter> procComplete, Context context) {
        mProcComplete = procComplete;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mSuccess = false;
        super.onPreExecute();
        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("Contacting SmartThings ...");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            //Code = pref.getString("Code", "");
            mDialog.show();
        }
    }
    @Override
    protected RESTCommunicatorResult doInBackground(String... args) {
        Log.i(Globals.ACTIVITY, "doInBackground");
        SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
        RESTCommunicator coms = new RESTCommunicator();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("code", smartThingsTokenInfo.getAuthCode()));
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

    @Override
    protected void onPostExecute(RESTCommunicatorResult result) {
        if(mDialog !=null) {
            mDialog.dismiss();
            mDialog = null;
        }
        try {
            if (!result.isSuccess())
                throw result.getException();
            SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
            smartThingsTokenInfo.setToken(result.getResult().getString("access_token"));
            smartThingsTokenInfo.setType(result.getResult().getString("token_type"));

            int minutes = Integer.parseInt(result.getResult().getString("expires_in"));
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, minutes);
            smartThingsTokenInfo.setExpires(c.getTime());

            smartThingsTokenInfo.Save();
            mSuccess = true;
            Log.e("avancement : ", "Get EndPoint ");
        } catch (Exception ex) {
            if(mContext !=null)
                Toast.makeText(mContext, "Token Get Error", Toast.LENGTH_SHORT).show();
        }

        if(mProcComplete != null)
            mProcComplete.Complete(mSuccess, this);
    }

    @SuppressWarnings("unused")
    public Boolean getSuccess() {
        return mSuccess;
    }
}

