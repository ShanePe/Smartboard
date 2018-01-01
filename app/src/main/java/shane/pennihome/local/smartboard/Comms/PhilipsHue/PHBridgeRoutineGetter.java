package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import shane.pennihome.local.smartboard.Comms.Interface.ICommunicator;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.RESTCommunicator;
import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.Routines;
import shane.pennihome.local.smartboard.Data.TokenHueBridge;

/**
 * Created by shane on 31/12/17.
 */

@SuppressWarnings("ALL")
public class PHBridgeRoutineGetter extends ICommunicator<PHBridgeRoutineGetter> {
    private final Routines mRoutines = new Routines();

    PHBridgeRoutineGetter(Context mContext, OnProcessCompleteListener<PHBridgeRoutineGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    public Routines getRoutines() {
        return mRoutines;
    }

    @Override
    public void PreProcess() {
        super.PreProcess();
        mRoutines.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not get Hue Bridge Routines";
    }

    @Override
    public String getDialogMessage() {
        return "Getting routines from to Hue Bridge...";
    }

    @Override
    public JSONObject Process() throws Exception {
        JSONObject jRet = new JSONObject();

        TokenHueBridge tokenHueBridge = TokenHueBridge.Load();
        RESTCommunicator restCommunicator = new RESTCommunicator();
        String url = "http://" + tokenHueBridge.getAddress() + "/api/" + tokenHueBridge.getToken() + "/groups";
        final JSONArray groups = new JSONArray();
        JSONObject jGroup = restCommunicator.getJson(url, null);
        Iterator<String> iterator = jGroup.keys();

        while (iterator.hasNext()) {
            String k = iterator.next();
            JSONObject jGp = jGroup.getJSONObject(k);
            JSONArray jLight = jGp.getJSONArray("lights");
            StringBuilder sLightKey = new StringBuilder();
            for (int i = 0; i < jLight.length(); i++)
                sLightKey.append(jLight.getString(i));
            jGp.put("id", k);
            jGp.put("lkey", sLightKey.toString());
            groups.put(jGp);
        }

        url = "http://" + tokenHueBridge.getAddress() + "/api/" + tokenHueBridge.getToken() + "/scenes";
        final JSONArray routines = new JSONArray();
        JSONObject jRoutine = restCommunicator.getJson(url, null);

        iterator = jRoutine.keys();
        while (iterator.hasNext()) {
            String k = iterator.next();
            JSONObject jRt = jRoutine.getJSONObject(k);
            JSONArray jLight = jRt.getJSONArray("lights");
            StringBuilder sLightKey = new StringBuilder();
            for (int i = 0; i < jLight.length(); i++)
                sLightKey.append(jLight.getString(i));

            jRt.put("id", k);
            jRt.put("lkey", sLightKey.toString());

            JSONArray relGroups = new JSONArray();
            for (int x = 0; x < groups.length(); x++) {
                JSONObject jG = groups.getJSONObject(x);
                if (jG.getString("lkey").equals(sLightKey.toString()))
                    relGroups.put(jG);
            }

            jRt.put("groups", relGroups);
            routines.put(jRt);
        }

        jRet.put("routines", routines);
        return jRet;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        JSONArray jRoutines = result.getResult().getJSONArray("routines");
        for (int i = 0; i < jRoutines.length(); i++) {
            JSONObject jRout = jRoutines.getJSONObject(i);
            JSONArray jGroups = jRout.getJSONArray("groups");

            if (jGroups.length() == 0) {
                Routine r = new Routine();
                r.setId(jRout.getString("id"));
                r.setName(jRout.getString("name") + " in all");
                r.setSource(Thing.Source.PhilipsHue);
                mRoutines.add(r);
            } else {
                for (int x = 0; x < jGroups.length(); x++) {
                    JSONObject jGroup = jGroups.getJSONObject(x);
                    Routine r = new Routine();
                    r.setId(jRout.getString("id"));
                    r.setName(jRout.getString("name") + " in " + jGroup.getString("name"));
                    r.setSource(Thing.Source.PhilipsHue);
                    mRoutines.add(r);
                }
            }
        }
    }
}
