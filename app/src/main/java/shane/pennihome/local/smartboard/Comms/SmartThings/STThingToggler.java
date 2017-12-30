package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;

/**
 * Created by shane on 28/12/17.
 */

public class STThingToggler extends AsyncTask<String, String, ComResult> {
    private Thing mThing;
    private ProcessCompleteListener<STThingToggler> mProcessComplete;
    private boolean mSuccess;
    private Context mContext;

    public STThingToggler(Thing thing, ProcessCompleteListener<STThingToggler> processComplete, Context context) {
        mThing = thing;
        mProcessComplete = processComplete;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mSuccess = false;
        super.onPreExecute();
    }

    @Override
    protected ComResult doInBackground(String... strings) {
        ComResult ret = new ComResult();
        HttpCommunicator coms = new HttpCommunicator();
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
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

            coms.putJson(url, smartThingsTokenInfo.getToken(), null);
            ret.setResult(new JSONObject());
        } catch (Exception e) {
            ret.setException(e);
        }
        return ret;
    }

    @Override
    protected void onPostExecute(ComResult result) {
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

    public boolean isSuccess() {
        return mSuccess;
    }
}
