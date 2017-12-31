package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsToken;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
@SuppressLint("StaticFieldLeak")
public class STThingToggler extends AsyncTask<String, String, RESTCommunicatorResult> {
    private final Thing mThing;
    private final OnProcessCompleteListener<STThingToggler> mProcessComplete;
    private boolean mSuccess;
    private final Context mContext;

    public STThingToggler(Thing thing, OnProcessCompleteListener<STThingToggler> processComplete) {
        mThing = thing;
        mProcessComplete = processComplete;
        mContext = null;
    }

    @Override
    protected void onPreExecute() {
        mSuccess = false;
        super.onPreExecute();
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... strings) {
        RESTCommunicatorResult ret = new RESTCommunicatorResult();
        RESTCommunicator httpCommunicator = new RESTCommunicator();
        SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
        try {
            String url = null;
            switch(mThing.getSource())
            {
                case SmartThings:
                    if(mThing instanceof Device)
                        url = smartThingsTokenInfo.getRequestUrl() + "/switches/" +
                                URLEncoder.encode(mThing.getId(), "UTF-8") + "/" +
                                (((Device) mThing).getOn() ? "off" : "on");
                    else if(mThing instanceof Routine)
                        url = smartThingsTokenInfo.getRequestUrl() + "/routines/" +
                                URLEncoder.encode(mThing.getId(), "UTF-8");
                    break;
            }

            if(url == null)
                throw new Exception("Could not determine endpoint");

            httpCommunicator.putJson(url, smartThingsTokenInfo.getToken());
            ret.setResult(new JSONObject());
        } catch (Exception e) {
            ret.setException(e);
        }
        return ret;
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult result) {
        try {
            if (!result.isSuccess())
                throw result.getException();

            mSuccess = true;
        } catch (Exception e) {
            if(mContext !=null)
                Toast.makeText(mContext, "Cannot toggle switch", Toast.LENGTH_SHORT).show();
        }

        if (mProcessComplete != null)
            mProcessComplete.Complete(mSuccess, this);
    }

    @SuppressWarnings("unused")
    public boolean getSuccess() {
        return mSuccess;
    }
}
