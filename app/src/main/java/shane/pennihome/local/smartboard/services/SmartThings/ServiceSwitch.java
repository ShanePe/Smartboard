package shane.pennihome.local.smartboard.services.SmartThings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import shane.pennihome.local.smartboard.comms.Executor;
import shane.pennihome.local.smartboard.comms.ExecutorRequest;
import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 29/01/18.
 */

public class ServiceSwitch extends Service {

    public ServiceSwitch() {

    }

    public static ServiceSwitch Load(String json) {
        try {
            return IDatabaseObject.Load(ServiceSwitch.class, json);
        } catch (Exception e) {
            return new ServiceSwitch();
        }
    }

    @Override
    protected Things getThings() throws Exception {
        Things things = new Things();
        Executor executor = new Executor();
        ExecutorResult result = executor.execute(new ExecutorRequest(
                new URL(mRequestUrl + "/switches"),
                ExecutorRequest.Types.GET,
                new OnExecutorRequestActionListener() {
                    @Override
                    public void OnPresend(HttpURLConnection connection) {
                        connection.setRequestProperty("Authorization", "Bearer " + mToken);
                    }
                })).get();

        if (!result.isSuccess())
            throw result.getError();

        JSONArray jObjURI = new JSONArray(result.getResult());
        for (int i = 0; i < jObjURI.length(); i++) {
            JSONObject jDev = jObjURI.getJSONObject(i);
            Switch d = new Switch();
            d.setId(jDev.getString("id"));
            d.setName(jDev.getString("name"));
            d.setState(getState(jDev));
            d.setType(jDev.getString("type"));
            d.setSource(IThing.Sources.SmartThings);
            things.add(d);
        }

        return things;
    }

    private Switch.States getState(JSONObject j) throws JSONException {
        if (j.getString("value").equals("on"))
            return Switch.States.On;
        else
            return Switch.States.Off;
    }

    @Override
    protected String getDescription() {
        return "SmartThings Switches";
    }

    @Override
    public String getDataID() {
        return "SmartThingsSwitches";
    }
}
