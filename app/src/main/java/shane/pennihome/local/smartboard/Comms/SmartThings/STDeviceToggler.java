package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import shane.pennihome.local.smartboard.Comms.ComResult;
import shane.pennihome.local.smartboard.Comms.HttpCommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;

/**
 * Created by shane on 28/12/17.
 */

public class STDeviceToggler extends AsyncTask<String, String, ComResult> {
    private Device _device;
    private ProcessCompleteListener<STDeviceToggler> _processComplete;
    private boolean _success;

    public STDeviceToggler(Device device, ProcessCompleteListener<STDeviceToggler> processComplete) {
        _device = device;
        _processComplete = processComplete;
    }

    @Override
    protected void onPreExecute() {
        _success = false;
        super.onPreExecute();
    }

    @Override
    protected ComResult doInBackground(String... strings) {
        ComResult ret = new ComResult();
        HttpCommunicator coms = new HttpCommunicator();
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        try {
            String url = smartThingsTokenInfo.getRequestUrl() + "/switches/" + URLEncoder.encode(_device.getId(), "UTF-8") + "/" + (_device.getOn() ? "off" : "on");
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

            _success = true;
        } catch (Exception e) {
            Toast.makeText(Globals.getContext(), "Cannot toggle switch", Toast.LENGTH_SHORT).show();
        }

        if (_processComplete != null)
            _processComplete.Complete(_success, this);
    }

    public boolean isSuccess() {
        return _success;
    }
}
