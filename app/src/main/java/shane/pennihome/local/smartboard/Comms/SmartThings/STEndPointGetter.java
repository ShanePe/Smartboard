package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;

public class STEndPointGetter extends AsyncTask<String, String, ComResult> {
    private ProgressDialog mDialog;
    private Boolean mSuccess;
    private ProcessCompleteListener<STEndPointGetter> mProcessCompleteListener;
    private Activity mContext;

    public STEndPointGetter(ProcessCompleteListener<STEndPointGetter> processCompleteListener, Activity context) {
        mProcessCompleteListener = processCompleteListener;
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
            mDialog.show();
        }
    }

    @Override
    protected ComResult doInBackground(String... args) {                       //UriGet
        ComResult ret = new ComResult();
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        HttpCommunicator coms = new HttpCommunicator();
        try {
            ret.setResult(coms.getJson(Globals.ENDPOINT_URL, smartThingsTokenInfo.getToken(), null));
        } catch (Exception e) {
            ret.setException(e);
        }
        return ret;
        //Log.i("json : ", jsonUri.toString());

        /** That will return a response like:
         **  {
         **    "oauthClient": {
         **        "clientSecret": "CLIENT-SECRET",
         **        "clientId": "CLIENT-ID"
         **    },
         **    "uri": "BASE-URL/api/smartapps/installations/INSTALLATION-ID",
         **    "base_url": "BASE-URL",
         **    "url": "/api/smartapps/installations/INSTALLATION-ID"
         **  }
         **/
    }

    @Override
    protected void onPostExecute(ComResult result) {                          //UriGet
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        try {
            if (!result.isSuccess())
                throw result.getException();

            SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
            smartThingsTokenInfo.setRequestUrl(result.getResult().getString("uri"));
            smartThingsTokenInfo.Save();
            mSuccess = true;

        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, "Uri Get Error", Toast.LENGTH_SHORT).show();
        }

        if (mProcessCompleteListener != null)
            mProcessCompleteListener.Complete(mSuccess, this);
    }

    public Boolean getSuccess() {
        return mSuccess;
    }
}