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

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.NameValuePair;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;


@SuppressLint("StaticFieldLeak")
public class STTokenGetter extends AsyncTask<String, String, ComResult> {
    private ProgressDialog mDialog;
    private Boolean mSuccess;
    private final ProcessCompleteListener<STTokenGetter> mProcComplete;
    private final Context mContext;

    public STTokenGetter(ProcessCompleteListener<STTokenGetter> procComplete, Context context) {
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
    protected ComResult doInBackground(String... args) {
        Log.i(Globals.ACTIVITY, "doInBackground");
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        HttpCommunicator coms = new HttpCommunicator();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new NameValuePair("code", smartThingsTokenInfo.getAuthCode()));
        params.add(new NameValuePair("client_id", Globals.CLIENT_ID));
        params.add(new NameValuePair("client_secret", Globals.CLIENT_SECRET));
        params.add(new NameValuePair("redirect_uri", Globals.REDIRECT_URI));
        params.add(new NameValuePair("grant_type", Globals.GRANT_TYPE));

        ComResult ret = new ComResult();
        try {
            ret.setResult(coms.postJson(params));
        } catch (Exception e) {
            ret.setException(e);
        }

        return ret;
    }

    @Override
    protected void onPostExecute(ComResult result) {
        if(mDialog !=null) {
            mDialog.dismiss();
            mDialog = null;
        }
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

