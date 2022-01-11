package shane.pennihome.local.smartboard.services.Harmony;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

public class HarmonyHubService extends IService {
    public static final String WS_ORIGIN = "http://sl.dhg.myharmony.com";
    private final String WS_CLIENT = "ws://%s:%s/?domain=svcs.myharmony.com&hubId=%s";
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
        return sendMessage(command, new JSONObject());
    }

    private String sendMessage(String command, JSONObject params) throws Exception {
        return HarmonyMessage.sendMessage(new URI(String.format(WS_CLIENT, mIp, mPort, mRemoteId)), mMessageId++, mRemoteId, command, params);
    }

    public class ActivityGetter implements IThingsGetter {
        final String HH_CONFIG = "vnd.logitech.harmony/vnd.logitech.harmony.engine?config";

        @Override
        public String getLoadMessage() {
            return "Getting Harmony Hub Activities";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();

            JSONArray jDevices = new JSONObject(sendMessage(HH_CONFIG)).getJSONObject("data").getJSONArray("device");
            for (int i = 0; i < jDevices.length(); i++) {
                JSONObject jDevice = jDevices.getJSONObject(i);
                JSONArray jControls = jDevice.getJSONArray("controlGroup");
                for (int x = 0; x < jControls.length(); x++) {
                    JSONObject jControl = jControls.getJSONObject(x);
                    if (jControl.getString("name").equalsIgnoreCase("power")) {
                        Switch s = new Switch();
                        s.setId(jDevice.getString("id"));
                        s.setResource(jDevice.getString("deviceProfileUri"));
                        s.setName(String.format("%s (%S)",jDevice.getString("label"),jDevice.getString("model")) );
                        s.setType(jDevice.getString("model"));
                        s.setService(ServicesTypes.HarmonyHub);
                        s.initialise();

                        JSONArray jFunctions = jControl.getJSONArray("function");
                        for (int c = 0; c < jFunctions.length(); c++) {
                            JSONObject jFunction = jFunctions.getJSONObject(c);
                            s.getAdditional().add(new HarmonyFunction(
                                    jFunction.getString("action"),
                                    jFunction.getString("name"),
                                    jFunction.getString("label")));
                        }

                        things.add(s);
                    }
                }
            }


            return things;
        }

        @Override
        public int getUniqueId() {
            return 6;
        }

        @Override
        public Type getThingType() {
            return Switch.class;
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return null;
        }

        @Override
        public IThing getThingState(IThing thing) throws Exception {
            return null;
        }
    }

}
