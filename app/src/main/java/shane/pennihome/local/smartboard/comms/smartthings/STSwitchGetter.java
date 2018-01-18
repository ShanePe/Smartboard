package shane.pennihome.local.smartboard.comms.smartthings;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import shane.pennihome.local.smartboard.comms.RESTCommunicator;
import shane.pennihome.local.smartboard.comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnCommResponseListener;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.ITokenSmartThings;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.Switches;

@SuppressLint("StaticFieldLeak")
public class STSwitchGetter extends ICommunicator<STSwitchGetter> {
    private final Switches mSwitches = new Switches();

    STSwitchGetter(Context mContext, OnProcessCompleteListener<STSwitchGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    Switches getDevices() {
        return mSwitches;
    }

    @Override
    public void PreProcess() {
        mSwitches.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not get SmartThing Switches";
    }

    @Override
    public String getDialogMessage() {
        return "Getting devices from SmartThings ...";
    }

    @Override
    public JSONObject Process() throws Exception {
        ITokenSmartThings tokenSmartThingsInfo = ITokenSmartThings.Load();
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

            Switch d = new Switch();
            d.setId(jDev.getString("id"));
            d.setName(jDev.getString("name"));
            d.setState(getState(jDev));
            d.setType(jDev.getString("type"));
            d.setSource(IThing.Sources.SmartThings);
            mSwitches.add(d);
        }

    }

    private Switch.States getState(JSONObject j) throws JSONException {
        if (j.getString("value").equals("on"))
            return Switch.States.On;
        else
            return Switch.States.Off;
    }
}