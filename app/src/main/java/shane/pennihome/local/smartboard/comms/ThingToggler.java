package shane.pennihome.local.smartboard.comms;

import org.json.JSONObject;

import java.net.URLEncoder;

import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.TokenHueBridge;
import shane.pennihome.local.smartboard.data.TokenSmartThings;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("ALL")
public class ThingToggler extends ICommunicator<ThingToggler> {
    private final IThing mThing;

    public ThingToggler(IThing thing, OnProcessCompleteListener<ThingToggler> processComplete) {
        super(null, processComplete);
        mThing = thing;
    }

    @Override
    public String getFailedMessage() {
        return "Cannot toggle switch";
    }

    @Override
    public String getDialogMessage() {
        return null;
    }

    @Override
    public JSONObject Process() throws Exception {
        RESTCommunicator httpCommunicator = new RESTCommunicator();

        String url = null;
        switch (mThing.getServiceType()) {
            case SmartThings:
                TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();

                if (mThing instanceof Switch)
                    url = tokenSmartThingsInfo.getRequestUrl() + "/switches/" +
                            URLEncoder.encode(mThing.getId(), "UTF-8") + "/" +
                            (((Switch) mThing).isOn() ? "off" : "on");
                else if (mThing instanceof Routine)
                    url = tokenSmartThingsInfo.getRequestUrl() + "/routines/" +
                            URLEncoder.encode(mThing.getId(), "UTF-8");
                if (url == null)
                    throw new Exception("Could not determine endpoint");

                httpCommunicator.putJson(url, tokenSmartThingsInfo.getToken());
                break;

            case PhilipsHue:
                TokenHueBridge token = TokenHueBridge.Load();
                JSONObject body = new JSONObject();

                if (mThing instanceof Switch) {
                    body.put("on", !((Switch) mThing).isOn());
                    url = "http://" + token.getAddress() + "/api/" + token.getToken() + "/lights/" + mThing.getId() + "/state";
                } else if (mThing instanceof Routine) {
                    body.put("scene", mThing.getId());
                    url = "http://" + token.getAddress() + "/api/" + token.getToken() + "/groups/0/action";
                }
                if (url == null)
                    throw new Exception("Could not determine endpoint");

                httpCommunicator.putJson(url, body);
                break;
        }
        return new JSONObject();
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {

    }
}
