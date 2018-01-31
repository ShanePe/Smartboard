package shane.pennihome.local.smartboard.comms.smartthings;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import shane.pennihome.local.smartboard.comms.RESTCommunicator;
import shane.pennihome.local.smartboard.comms.RESTCommunicatorResult;
import shane.pennihome.local.smartboard.comms.interfaces.ICommunicator;
import shane.pennihome.local.smartboard.comms.interfaces.OnCommResponseListener;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.TokenSmartThings;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

@SuppressWarnings("unused")
@SuppressLint("StaticFieldLeak")
public class STRoutineGetter extends ICommunicator<STRoutineGetter> {
    private final Things mThings = new Things();

    STRoutineGetter(Context mContext, OnProcessCompleteListener<STRoutineGetter> mProcessCompleteListener) {
        super(mContext, mProcessCompleteListener);
    }

    IThings<Routine> getRoutines() {
        return mThings.getOfType(Routine.class);
    }

    @Override
    public void PreProcess() {
        super.PreProcess();
        mThings.clear();
    }

    @Override
    public String getFailedMessage() {
        return "Could not get SmartThing Routines";
    }

    @Override
    public String getDialogMessage() {
        return "Getting routines from SmartThings ...";
    }

    @Override
    public JSONObject Process() throws Exception {
        TokenSmartThings tokenSmartThingsInfo = TokenSmartThings.Load();
        @SuppressWarnings("unused") final JSONArray devices = new JSONArray();
        final JSONArray routines = new JSONArray();

        RESTCommunicator coms = new RESTCommunicator();
        JSONObject jRet = new JSONObject();
        coms.getJson(tokenSmartThingsInfo.getRequestUrl() + "/routines", tokenSmartThingsInfo.getToken(), new OnCommResponseListener() {
            @Override
            public void process(JSONObject obj) {
                routines.put(obj);
            }
        });
        jRet.put("routines", routines);

        return jRet;
    }

    @Override
    public void Complete(RESTCommunicatorResult result) throws Exception {
        JSONArray routines = result.getResult().getJSONArray("routines");
        for (int i = 0; i < routines.length(); i++) {
            JSONObject jRout = routines.getJSONObject(i);

            Routine r = new Routine();
            r.setId(jRout.getString("id"));
            r.setName(jRout.getString("name"));
            r.setService(IService.ServicesTypes.SmartThings);
            mThings.add(r);
        }
    }
}