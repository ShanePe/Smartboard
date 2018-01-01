package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnCommResponseListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;

@SuppressLint("StaticFieldLeak")
public class STDevicesGetter extends ICommunicator<STDevicesGetter> {
    private final Devices mDevices = new Devices();

    STDevicesGetter(Context mContext, OnProcessCompleteListener<STDevicesGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    Devices getDevices() {
        return mDevices;
    }

    @Override
    public void PreProcess() {
        mDevices.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not get SmartThing Devices";
    }

    @Override
    public String getDialogMessage() {
        return "Getting devices from SmartThings ...";
    }

    @Override
    public JSONObject Process() throws Exception {
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
        final JSONArray devices = new JSONArray();

        RESTCommunicator coms = new RESTCommunicator();
        JSONObject jRet = new JSONObject();

        coms.getJson(tokenSmartThingsInfo.getRequestUrl() + "/switches", tokenSmartThingsInfo.getToken(), new OnCommResponseListener() {
            @Override
            public void process(JSONObject obj) {
                devices.put(obj);
            }
        });
        jRet.put("devices", devices);

        return jRet;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        JSONArray devices = result.getResult().getJSONArray("devices");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject jDev = devices.getJSONObject(i);

            Device d = new Device();
            d.setId(jDev.getString("id"));
            d.setName(jDev.getString("name"));
            d.setState(getState(jDev));
            d.setType(jDev.getString("type"));
            d.setSource(Thing.Source.SmartThings);
            mDevices.add(d);
        }

    }

    private Device.States getState(JSONObject j) throws JSONException {
        if (j.getString("value").equals("on"))
            return Device.States.On;
        else
            return Device.States.Off;
    }
}