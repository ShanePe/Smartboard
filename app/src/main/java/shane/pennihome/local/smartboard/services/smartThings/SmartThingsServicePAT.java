package shane.pennihome.local.smartboard.services.smartThings;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutor;
import shane.pennihome.local.smartboard.comms.JsonExecutorRequest;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.temperature.Temperature;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 29/01/18.
 */

public class SmartThingsServicePAT extends IService {
    private final static String ST_ROUTINE_URL = "https://api.smartthings.com/v1/scenes";
    private final static String ST_DEVICE_URL = "https://api.smartthings.com/v1/devices";
    private final static String ST_DEVICE_URL_EXE = "https://api.smartthings.com/v1/devices/%s/commands";
    private final static String ST_ROUTINE_URL_EXE = "https://api.smartthings.com/v1/scenes/%s/execute";
    private final static String ST_DEVICE_STATE_URL_EXE = "https://api.smartthings.com/v1/devices/%s/status";
    private final static String ST_DEVICE_HEALTH_URL_EXE = "https://api.smartthings.com/v1/devices/%s/health";

    private String mPersonalAccessToken;

    public static SmartThingsServicePAT Load(String json) {
        try {
            return IService.fromJson(SmartThingsServicePAT.class, json);
        } catch (Exception e) {
            return new SmartThingsServicePAT();
        }
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.SmartThings;
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new SmartThingsFragmentPAT();
    }

    @Override
    public int getDrawableIconResource() {
        return R.mipmap.icon_st_logo_large_mm_fg;
    }

    @Override
    public void register(Context context, OnProcessCompleteListener<IService> onProcessCompleteListener) {
        try {
            super.register(context, onProcessCompleteListener);
        } catch (Exception ex) {
            Toast.makeText(context, "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String getName() {
        return "SmartThings";
    }

    @Override
    public boolean isRegistered() {
        //Calendar c = Calendar.getInstance();
        return (!TextUtils.isEmpty(mPersonalAccessToken));
    }

    @Override
    public boolean isAwaitingAction() {
        return false;
    }

    @Override
    public void connect() throws Exception {
        try {
            Connector connector = new Connector();
            connector.getThings();
        } catch (Exception ex) {
            setPersonalAccessToken("");
            throw ex;
        }
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        ArrayList<IThingsGetter> thingsGetters = new ArrayList<>();
        thingsGetters.add(new SwitchGetter());
        thingsGetters.add(new RoutineGetter());
        return thingsGetters;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    public String getPersonalAccessToken() {
        return mPersonalAccessToken;
    }

    void setPersonalAccessToken(String authorisationCode) {
        mPersonalAccessToken = authorisationCode;
    }

    protected static class Connector implements IThingsGetter {

        public String getLoadMessage() {
            return "Connecting to SmartThings";
        }

        public Things getThings() throws Exception {
            return new Things();
        }

        @Override
        public int getUniqueId() {
            return 1;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{IThing.class};
        }


        @Override
        public IExecutor<?> getExecutor(String Id) {
            return null;
        }

        @Override
        public IThing getThingState(IThing thing) {
            return thing;
        }
    }

    class SwitchGetter implements IThingsGetter {
        public String getLoadMessage() {
            return "Getting SmartThings switches";
        }

        public Things getThings() throws Exception {
            Things things = new Things();
            JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(
                    new URL(ST_DEVICE_URL),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mPersonalAccessToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jItems = result.getResultAsJsonObject().getJSONArray("items");

            for (int i = 0; i < jItems.length(); i++) {
                JSONObject jDev = jItems.getJSONObject(i);
                if (IsOfType(jDev, "switch")) {
                    Switch d = new Switch();
                    d.setId(jDev.getString("deviceId"));
                    d.setName(jDev.getString("label"));
                    d.setType(getCategories(jDev));
                    d.setService(ServicesTypes.SmartThings);
                    d.initialise();
                    things.add(d);
                }
                if (IsOfType(jDev, "temperatureMeasurement")) {
                    Temperature d = new Temperature();
                    d.setId(jDev.getString("deviceId"));
                    d.setName(jDev.getString("label"));
                    d.setService(ServicesTypes.SmartThings);
                    d.initialise();
                    things.add(d);
                }
            }

            return getThingsState(things);
        }

        private String getCategories(JSONObject jDevice) throws JSONException {
            StringBuilder sbCat = new StringBuilder();
            JSONArray jComps = jDevice.getJSONArray("components");
            for (int i = 0; i < jComps.length(); i++) {
                JSONArray jCats = jComps.getJSONObject(i).getJSONArray("categories");
                for (int x = 0; x < jCats.length(); x++) {
                    if (sbCat.length() > 0)
                        sbCat.append(", ");
                    sbCat.append(jCats.getJSONObject(x).getString("name"));
                }
            }
            return sbCat.toString();
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean isThingOnline(String id) throws MalformedURLException, JSONException {
            JSONObject jHeath = getDeviceHealth(id);
            return jHeath != null && jHeath.getString("state").equalsIgnoreCase("online");
        }

        private Things getThingsState(Things things) throws MalformedURLException, JSONException {
            for (IThing s : things.getOfType(Switch.class)) {
                getThingState(s);
            }
            for (IThing t : things.getOfType(Temperature.class)) {
                getThingState(t);
            }

            return things;
        }

        private int getColourFromColourControl(JSONObject jDev) {
            try {
                float hue = ((jDev.getJSONObject("colorControl").getJSONObject("hue").getInt("value")) * 360f) / 100f;
                float sat = jDev.getJSONObject("colorControl").getJSONObject("saturation").getInt("value") / 100f;

                return Color.HSVToColor(new float[]{hue, sat, 1.0f});
            } catch (Exception ignore) {
                return Color.TRANSPARENT;
            }
        }

        private JSONObject getDeviceState(String id) throws JSONException, MalformedURLException {
            JsonExecutorResult state = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format(ST_DEVICE_STATE_URL_EXE, id)),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mPersonalAccessToken);
                        }
                    }));

            if (state.isSuccess())
                return state.getResultAsJsonObject().getJSONObject("components").getJSONObject("main");

            return null;
        }

        private JSONObject getDeviceHealth(String id) throws MalformedURLException {
            JsonExecutorResult state = JsonExecutor.fulfil(new JsonExecutorRequest(new URL(String.format(ST_DEVICE_HEALTH_URL_EXE, id)),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mPersonalAccessToken);
                        }
                    }));

            if (state.isSuccess())
                return state.getResultAsJsonObject();

            return null;
        }

        private boolean IsOfType(JSONObject jDevice, String type) throws JSONException {
            JSONArray jComp = jDevice.getJSONArray("components");
            boolean ret = false;
            for (int i = 0; i < jComp.length(); i++) {
                JSONArray jCap = jComp.getJSONObject(i).getJSONArray("capabilities");
                for (int x = 0; x < jCap.length(); x++) {
                    JSONObject jService = jCap.getJSONObject(x);
                    if (jService.getString("id").equalsIgnoreCase(type)) {
                        ret = true;
                        break;
                    }
                }
                if (ret)
                    break;
            }

            return ret;
        }

        @Override
        public int getUniqueId() {
            return 2;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{Switch.class};
        }

        private JsonExecutorResult executeSTCommand(IThing thing, String cmd) throws UnsupportedEncodingException, MalformedURLException, JSONException {

            String url = String.format(ST_DEVICE_URL_EXE, URLEncoder.encode(thing.getId(), "UTF-8"));
            JsonExecutorRequest request = new JsonExecutorRequest(new URL(url), JsonExecutorRequest.Types.POST);
            request.getHeaders().add(new NameValuePair("Authorization", "Bearer " + mPersonalAccessToken));
            request.setPostJson(new JSONObject(cmd));

            return JsonExecutor.fulfil(request);
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            if (Id.equalsIgnoreCase("level"))
                return new LevelExecutor();
            else
                return new OnOffExecutor();
        }

        @Override
        public IThing getThingState(IThing thing) throws MalformedURLException, JSONException {
            if (thing.getThingType() == IThing.Types.Switch) {
                Switch s = (Switch) thing;
                if (s.getLastStateUpdate() == null || TimeUnit.MILLISECONDS.toSeconds(Calendar.getInstance().getTime().getTime() - s.getLastStateUpdate().getTime()) > 60) {
                    JSONObject jState = getDeviceState(s.getId());
                    if (jState != null) {
                        s.setOn(jState.getJSONObject("switch").getJSONObject("switch").getString("value").equals("on"), false);
                        if (jState.has("switchLevel")) {
                            s.setIsDimmer(true);
                            s.setDimmerLevel(jState.getJSONObject("switchLevel").getJSONObject("level").getInt("value"), false);
                        }
                        if (jState.has("colorControl")) {
                            int c = getColourFromColourControl(jState);
                            s.setSupportsColour(c != Color.TRANSPARENT, false);
                            s.setCurrentColour(c, false);
                        }
                        s.setUnreachable(jState.getJSONObject("switch").getJSONObject("switch").getString("value").equals("null") || !isThingOnline(s.getId()), false);
                        s.setLastStateUpdate(Calendar.getInstance().getTime());
                    }
                }
            }
            if (thing.getThingType() == IThing.Types.Temperature) {
                assert thing instanceof Temperature;
                Temperature t = (Temperature) thing;
                JSONObject jState = getDeviceState(t.getId());
                assert jState != null;
                t.setTemperature(jState.getJSONObject("temperatureMeasurement").getJSONObject("temperature").getInt("value"), false);
                t.setUnreachable(!isThingOnline(t.getId()), false);
            }

            return thing;
        }

        class OnOffExecutor extends IExecutor<Void> {

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                try {
                    return executeSTCommand(thing,
                            String.format("{\"commands\": [{\"component\": \"main\",\"capability\": \"switch\",\"command\": \"%s\"}]}", ((Switch) thing).isOn() ? "off" : "on"));
                } catch (Exception e) {
                    return new JsonExecutorResult(e);
                }
            }
        }

        class LevelExecutor extends IExecutor<Integer> {
            @Override
            public String getId() {
                return "level";
            }

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                try {
                    return executeSTCommand(thing,
                            String.format("{\"commands\": [{\"component\": \"main\",\"capability\": \"switchLevel\",\"command\": \"setLevel\",\"arguments\":[%s]}]}", getValue()));

                } catch (Exception e) {
                    return new JsonExecutorResult(e);
                }
            }
        }
    }

    protected class RoutineGetter implements IThingsGetter {

        public String getLoadMessage() {
            return "Getting SmartThings Routines";
        }

        public Things getThings() throws Exception {
            Things things = new Things();
            JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(
                    new URL(ST_ROUTINE_URL),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mPersonalAccessToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jItems = result.getResultAsJsonObject().getJSONArray("items");

            for (int i = 0; i < jItems.length(); i++) {
                JSONObject jScene = jItems.getJSONObject(i);

                Routine r = new Routine();
                r.setId(jScene.getString("sceneId"));
                r.setName(jScene.getString("sceneName"));
                r.setService(ServicesTypes.SmartThings);
                r.initialise();
                things.add(r);
            }

            return things;
        }

        @Override
        public int getUniqueId() {
            return 3;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{Routine.class};
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return new IExecutor<Void>() {
                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    try {
                        return JsonExecutor.fulfil(new JsonExecutorRequest(
                                new URL(String.format(ST_ROUTINE_URL_EXE, URLEncoder.encode(thing.getId(), "UTF-8"))),
                                JsonExecutorRequest.Types.POST,
                                new OnExecutorRequestActionListener() {
                                    @Override
                                    public void OnPreExecute(HttpURLConnection connection) {
                                        connection.setRequestProperty("Authorization", "Bearer " + mPersonalAccessToken);
                                    }
                                }));

                    } catch (Exception e) {
                        return new JsonExecutorResult(e);
                    }
                }
            };
        }

        @Override
        public IThing getThingState(IThing thing) {
            return thing;
        }
    }
}
