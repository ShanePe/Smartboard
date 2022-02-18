package shane.pennihome.local.smartboard.services.harmony;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

public class HarmonyHubService extends IService {
    public static final String WS_ORIGIN = "http://sl.dhg.myharmony.com";
    final String HH_CONFIG = "vnd.logitech.harmony/vnd.logitech.harmony.engine?config";
    final String HH_CURRENT = "vnd.logitech.harmony/vnd.logitech.harmony.engine?getCurrentActivity";
    final String HH_ACTION = "vnd.logitech.harmony/vnd.logitech.harmony.engine?holdAction";
    final String HH_ACT = "vnd.logitech.harmony/vnd.logitech.harmony.engine?startactivity";

    final String WS_CLIENT = "ws://%s:%s/?domain=svcs.myharmony.com&hubId=%s";
    final String ACT_KEY = "activity";

    private String mIp;
    private int mPort = 8088;
    private String mRemoteId;
    private int mMessageId = 1;

    public static HarmonyHubService Load(String json) {
        try {
            return IService.fromJson(HarmonyHubService.class, json);
        } catch (Exception e) {
            return new HarmonyHubService();
        }
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        this.mIp = ip;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    public String getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(String remoteId) {
        this.mRemoteId = remoteId;
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new HarmonyHubFragment();
    }

    @Override
    public int getDrawableIconResource() {
        return R.mipmap.harm_logo_mm_large_foreground;
    }

    @Override
    protected boolean isRegistered() {
        return mRemoteId != null && !TextUtils.isEmpty(mRemoteId);
    }

    @Override
    public boolean isAwaitingAction() {
        return false;
    }

    @Override
    public void connect() {
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        ArrayList<IThingsGetter> thingsGetters = new ArrayList<>();
        thingsGetters.add(new ActivityGetter());
        return thingsGetters;
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.HarmonyHub;
    }

    @Override
    public String getName() {
        return "Harmony Hub";
    }

    private String sendMessage(String command) throws Exception {
        return sendMessage(command, false);
    }

    private String sendMessage(String command, boolean noResponse) throws Exception {
        return sendMessage(command, new JSONObject(), noResponse);
    }

    private String sendMessage(String command, JSONObject params, boolean noResponse) throws Exception {
        return HarmonyMessage.sendMessage(new URI(String.format(WS_CLIENT, mIp, mPort, mRemoteId)), mMessageId++, mRemoteId, command, params, noResponse);
    }

    public class ActivityGetter implements IThingsGetter {
        @Override
        public String getLoadMessage() {
            return "Getting Harmony Hub Activities";
        }

        private Things processActivies(JSONArray activities) throws Exception {
            Things things = new Things();
            for (int i = 0; i < activities.length(); i++) {
                JSONObject jActivity = activities.getJSONObject(i);
                if (!jActivity.getString("id").equals("-1")) {
                    Switch s = new Switch();
                    s.setId(jActivity.getString("id"));
                    s.setResource(ACT_KEY);
                    s.setName(jActivity.getString("label"));
                    s.setType("Harmony Activity");
                    s.setService(ServicesTypes.HarmonyHub);
                    s.initialise();

                    JSONObject jRelated = jActivity.getJSONObject("fixit");
                    Iterator<String> keys = jRelated.keys();
                    while (keys.hasNext()) {
                        JSONObject jData = jRelated.getJSONObject(keys.next());
                        s.getAdditional().add(new HarmonyActivityDevice(jData.getString("id"), jData.getString("Power").equalsIgnoreCase("on")));
                    }
                    things.add(s);
                }
            }

            doStateForSwitches(things);

            return things;
        }

        private Things processDevices(JSONArray devices) throws JSONException {
            Things things = new Things();
            for (int i = 0; i < devices.length(); i++) {
                JSONObject jDevice = devices.getJSONObject(i);
                JSONArray jControls = jDevice.getJSONArray("controlGroup");
                for (int x = 0; x < jControls.length(); x++) {
                    JSONArray jFunctions = jControls.getJSONObject(x).getJSONArray("function");
                    for (int c = 0; c < jFunctions.length(); c++) {
                        JSONObject jFunction = jFunctions.getJSONObject(c);
                        Routine routine = new Routine();
                        routine.setId(String.format("%s-%s", jDevice.getString("id"), jFunction.getString("label").replace(' ', '-')));
                        routine.setName(String.format("%s %s", jDevice.getString("label"), jFunction.getString("label")));
                        routine.setService(ServicesTypes.HarmonyHub);
                        routine.getAdditional().add(new HarmonyFunction(jFunction.getString("action"), jFunction.getString("name"), jFunction.getString("label")));

                        routine.initialise();
                        things.add(routine);
                    }
                }
            }

            return things;
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();

            JSONObject jData = new JSONObject(sendMessage(HH_CONFIG)).getJSONObject("data");

            things.addAll(processDevices(jData.getJSONArray("device")));
            things.addAll(processActivies(jData.getJSONArray("activity")));

            return things;
        }

        private void doStateForSwitches(Things things) throws Exception {
            JSONObject jActive = new JSONObject(sendMessage(HH_CURRENT));
            String result = jActive.getJSONObject("data").getString("result");
            HashMap<String, Boolean> onThings = new HashMap<>();

            if (!result.equals("-1")) {
                Switch activity = (Switch) things.getbyId(result);
                for (HarmonyActivityDevice h : activity.getAdditional().cast(HarmonyActivityDevice.class))
                    onThings.put(h.getId(), h.isOn());

                if (!onThings.isEmpty())
                    onThings.put(result, true);
            }

            for (Switch s : things.getOfType(Switch.class)) {
                if (onThings.containsKey(s.getId()))
                    s.setOn(onThings.get(s.getId()), false);
                else
                    s.setOn(false, false);
            }
        }

        @Override
        public int getUniqueId() {
            return 6;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{Switch.class, Routine.class};
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return new HarmonyExecutor();
        }

        @Override
        public IThing getThingState(IThing thing) {
            Switch s = (Switch) thing;
            if (s.getResource().equals(ACT_KEY)) {
                try {
                    JSONObject jActive = new JSONObject(sendMessage(HH_CURRENT));
                    String result = jActive.getJSONObject("data").getString("result");
                    s.setOn(result.equalsIgnoreCase(s.getId()), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return thing;
        }
    }

    public class HarmonyExecutor extends IExecutor<Void> {

        @Override
        public int delayVerification() {
            return 20000;
        }

        private JSONObject getCommandParams(HarmonyFunction cmd) throws JSONException {
            JSONObject jAction = new JSONObject(cmd.getAction());

            JSONObject jCmd = new JSONObject();
            jCmd.put("status", "press");
            jCmd.put("verb", "render");
            jCmd.put("timestamp", new Date().getTime());
            jCmd.put("action", cmd.getAction());

            return jCmd;
        }

        private JsonExecutorResult executeRoutine(Routine routine) throws Exception {
            HarmonyFunction funct = (HarmonyFunction) routine.getAdditional().get(0);
            String response = sendMessage(HH_ACTION, getCommandParams(funct), true);

            return new JsonExecutorResult(response);
        }

        private JsonExecutorResult executeSwitch(Switch s, boolean forceOff) throws Exception {
            HarmonyFunction func;

            if (s.isOn() || forceOff)
                func = (HarmonyFunction) s.getAdditional().getByKey("PowerOff");
            else
                func = (HarmonyFunction) s.getAdditional().getByKey("PowerOn");

            if (func == null)
                func = (HarmonyFunction) s.getAdditional().getByKey("PowerToggle");

            if (func != null) {
                String response = sendMessage(HH_ACTION, getCommandParams(func), true);

                return new JsonExecutorResult(response);
            } else
                return new JsonExecutorResult(new Exception("No power control for switch"));
        }

        private JsonExecutorResult executeActivity(Switch s) throws Exception {
            JSONObject jParams = new JSONObject();
            jParams.put("timestame", new Date().getTime());
            jParams.put("activityId", s.isOn() ? "-1" : s.getId());

            sendMessage(HH_ACT, jParams, true);

            return new JsonExecutorResult("");
        }

        @Override
        public boolean doVerification(IThing thing) {
            return thing.getThingType() != IThing.Types.Routine;
        }

        @Override
        protected JsonExecutorResult execute(IThing thing) {
            JsonExecutorResult result = null;
            try {
                if (thing instanceof Switch) {
                    Switch s = (Switch) thing;
                    if (s.getResource().equals(ACT_KEY)) {
                        result = executeActivity(s);
                    } else {
                        result = executeSwitch(s, false);
                    }
                } else if (thing instanceof Routine)
                    result = executeRoutine((Routine) thing);

            } catch (Exception ex) {
                result = new JsonExecutorResult(ex);
            }
            if (result == null)
                result = new JsonExecutorResult(new Exception("Executor failed"));
            return result;
        }
    }
}
