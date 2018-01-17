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
import shane.pennihome.local.smartboard.Data.Switch;
import shane.pennihome.local.smartboard.Data.Switches;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;

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

            Switch d = new Switch();
            d.setId(jDev.getString("id"));
            d.setName(jDev.getString("name"));
            d.setState(getState(jDev));
            d.setType(jDev.getString("type"));
            d.setSource(IThing.Source.SmartThings);
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