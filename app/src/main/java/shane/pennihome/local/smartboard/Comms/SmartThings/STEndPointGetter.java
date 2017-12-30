package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;

public class STEndPointGetter extends AsyncTask<String, String, ComResult> {
    private ProgressDialog pDialog;
    private Boolean _success;
    private ProcessCompleteListener<STEndPointGetter> _processCompleteListener;

    public STEndPointGetter(ProcessCompleteListener<STEndPointGetter> processCompleteListener) {
        _processCompleteListener = processCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        _success = false;
        super.onPreExecute();
        pDialog = new ProgressDialog(Globals.getContext());
        pDialog.setMessage("Contacting SmartThings ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
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
        pDialog.dismiss();
        try {
            if (!result.isSuccess())
                throw result.getException();

            SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
            smartThingsTokenInfo.setRequestUrl(result.getResult().getString("uri"));
            smartThingsTokenInfo.Save();
            _success = true;

        } catch (Exception e) {
            Toast.makeText(Globals.getContext(), "Uri Get Error", Toast.LENGTH_SHORT).show();
        }

        if (_processCompleteListener != null)
            _processCompleteListener.Complete(_success, this);
    }

    public Boolean getSuccess() {
        return _success;
    }
}