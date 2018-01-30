package shane.pennihome.local.smartboard.comms.philipshue;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import shane.pennihome.local.smartboard.comms.RESTCommunicator;
import shane.pennihome.local.smartboard.comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.TokenHueBridge;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 31/12/17.
 */

@SuppressWarnings("ALL")
public class PHBridgeDeviceGetter extends ICommunicator<PHBridgeDeviceGetter> {
    private final Things mThings = new Things();

    PHBridgeDeviceGetter(Context mContext, OnProcessCompleteListener<PHBridgeDeviceGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    public IThings<Switch> getSwitches() {
        return mThings.getOfType(Switch.class);
    }

    @Override
    public void PreProcess() {
        super.PreProcess();
        mThings.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not get Hue Bridge Lights";
    }

    @Override
    public String getDialogMessage() {
        return "Getting lights from to Hue Bridge...";
    }

    @Override
    public JSONObject Process() throws Exception {
        TokenHueBridge tokenHueBridge = TokenHueBridge.Load();

        String url = "http://" + tokenHueBridge.getAddress() + "/api/" + tokenHueBridge.getToken() + "/lights";
        final JSONArray devices = new JSONArray();
        RESTCommunicator restCommunicator = new RESTCommunicator();
        JSONObject jDevices = restCommunicator.getJson(url, null);

        Iterator<String> iterator = jDevices.keys();
        while (iterator.hasNext()) {
            String k = iterator.next();
            JSONObject jDev = jDevices.getJSONObject(k);
            jDev.put("id", k);
            devices.put(jDev);
        }

        JSONObject jRet = new JSONObject();
        jRet.put("devices", devices);
        return jRet;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        JSONArray jDevices = result.getResult().getJSONArray("devices");
        for (int i = 0; i < jDevices.length(); i++) {
            JSONObject jDev = jDevices.getJSONObject(i);
            Switch d = new Switch();
            d.setId(jDev.getString("id"));
            d.setName(jDev.getString("name"));
            d.setState(getState(jDev));
            d.setType(jDev.getString("type"));
            d.setService(IService.Services.PhilipsHue);
            mThings.add(d);
        }
    }

    private Switch.States getState(JSONObject j) throws JSONException {
        JSONObject jState = j.getJSONObject("state");
        if (!jState.getBoolean("reachable"))
            return Switch.States.Unreachable;
        if (jState.getBoolean("on"))
            return Switch.States.On;
        else
            return Switch.States.Off;
    }
}
