package shane.pennihome.local.smartboard.services.SmartThings;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
import shane.pennihome.local.smartboard.things.stmodes.SmartThingMode;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.temperature.Temperature;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 29/01/18.
 */

public class SmartThingsService extends IService {
    final static String ST_CLIENT_ID = "953f9b86-9a48-461a-91e9-c9544dd980c4";
    final static String ST_REDIRECT_URI = "http://localhost:4567/oauth/callback";
    final static String ST_OAUTH_URL = "https://graph.api.smartthings.com/oauth/authorize";
    final static String ST_OAUTH_SCOPE = "app";
    private final static String ST_CLIENT_SECRET = "8da94852-2c55-47b0-89ee-79ce8ac3bcd5";
    private final static String ST_GRANT_TYPE = "authorization_code";
    private final static String ST_TOKEN_URL = "https://graph.api.smartthings.com/oauth/token";
    private final static String ST_ENDPOINT_URL = "https://graph.api.smartthings.com/api/smartapps/endpoints";
    private final static String ST_ROUTINE_URL = "https://api.smartthings.com/v1/scenes";
    private String mToken;
    private String mRequestUrl;
    private Date mExpires;
    @SuppressWarnings("FieldCanBeLocal")
    private String mType;
    private String mAuthorisationCode;

    public static SmartThingsService Load(String json) {
        try {
            return IService.fromJson(SmartThingsService.class, json);
        } catch (Exception e) {
            return new SmartThingsService();
        }
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.SmartThings;
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new SmartThingsFragment();
    }

    @Override
    public int getDrawableIconResource() {
        return R.mipmap.icon_st_logo_large_mm_fg;
    }

    @Override
    public void register(Context context, OnProcessCompleteListener<IService> onProcessCompleteListener) {
        try {
            JsonExecutorRequest request = new JsonExecutorRequest(new URL(ST_TOKEN_URL), JsonExecutorRequest.Types.POST);
            request.getQueryStringParameters().add(new NameValuePair("code", getAuthorisationCode()));
            request.getQueryStringParameters().add(new NameValuePair("client_id", ST_CLIENT_ID));
            request.getQueryStringParameters().add(new NameValuePair("client_secret", ST_CLIENT_SECRET));
            request.getQueryStringParameters().add(new NameValuePair("redirect_uri", ST_REDIRECT_URI));
            request.getQueryStringParameters().add(new NameValuePair("grant_type", ST_GRANT_TYPE));

            JsonExecutorResult jsonExecutorResult = JsonExecutor.fulfil(request);
            if (!jsonExecutorResult.isSuccess())
                throw jsonExecutorResult.getError();

            JSONObject jsToken = jsonExecutorResult.getResultAsJsonObject();

            mToken = jsToken.getString("access_token");
            mType = jsToken.getString("token_type");
            int minutes = Integer.parseInt(jsToken.getString("expires_in"));
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MINUTE, minutes);
            mExpires = c.getTime();

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
        Calendar c = Calendar.getInstance();
        return (!TextUtils.isEmpty(mToken) && mExpires.after(c.getTime()));
    }

    @Override
    public boolean isAwaitingAction() {
        return false;
    }

    @Override
    public void connect() throws Exception {
        Connector connector = new Connector();
        connector.getThings();
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        ArrayList<IThingsGetter> thingsGetters = new ArrayList<>();
        if (TextUtils.isEmpty(mRequestUrl))
            thingsGetters.add(new Connector());
        thingsGetters.add(new SwitchGetter());
        thingsGetters.add(new RoutineGetter());
        thingsGetters.add(new TemperatureGetter());
        thingsGetters.add(new ModesGetter());
        return thingsGetters;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    private String getAuthorisationCode() {
        return mAuthorisationCode;
    }

    void setAuthorisationCode(String authorisationCode) {
        mAuthorisationCode = authorisationCode;
    }

    protected class Connector implements IThingsGetter {

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
                    new URL(mRequestUrl + "/switches"),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jObjURI = new JSONArray(result.getResult());
            for (int i = 0; i < jObjURI.length(); i++) {
                JSONObject jDev = jObjURI.getJSONObject(i);
                Switch d = new Switch();
                d.setId(jDev.getString("id"));
                d.setName(jDev.getString("name"));
                d.setOn(jDev.getString("value").equals("on"), false);
                d.setType(jDev.getString("type"));
                if (!jDev.getString("level").equalsIgnoreCase("null")) {
                    d.setIsDimmer(true);
                    d.setDimmerLevel(jDev.getInt("level"), false);
                }
                d.setService(ServicesTypes.SmartThings);
                d.initialise();
                things.add(d);
            }

            return things;
        }

        @Override
        public int getUniqueId() {
            return 2;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{Switch.class};
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            if (Id.equalsIgnoreCase("level"))
                return new LevelExecutor();

            return new IExecutor<Void>() {

                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    try {
                        String url = String.format("%s/switches/%s/%s", mRequestUrl, URLEncoder.encode(thing.getId(), "UTF-8"), ((Switch) thing).isOn() ? "off" : "on");
                        return JsonExecutor.fulfil(new JsonExecutorRequest(new URL(url), JsonExecutorRequest.Types.PUT, new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPreExecute(HttpURLConnection connection) {
                                connection.setRequestProperty("Authorization", "Bearer " + mToken);
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

        class LevelExecutor extends IExecutor<Integer> {
            @Override
            public String getId() {
                return "level";
            }

            @Override
            protected JsonExecutorResult execute(IThing thing) {
                try {
                    String url = String.format("%s/switches_level/%s/%s", mRequestUrl, URLEncoder.encode(thing.getId(), "UTF-8"), getValue());
                    return JsonExecutor.fulfil(new JsonExecutorRequest(new URL(url), JsonExecutorRequest.Types.PUT, new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mToken);
                        }
                    }));

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
                            connection.setRequestProperty("Authorization", "Bearer " + mToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jObjURI = new JSONArray(result.getResult());
            for (int i = 0; i < jObjURI.length(); i++) {
                JSONObject jRoutine = jObjURI.getJSONObject(i);

                Routine r = new Routine();
                r.setId(jRoutine.getString("id"));
                r.setName(jRoutine.getString("name"));
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
                        String url = mRequestUrl + "/routines/" + URLEncoder.encode(thing.getId(), "UTF-8");
                        return JsonExecutor.fulfil(new JsonExecutorRequest(new URL(url), JsonExecutorRequest.Types.PUT, new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPreExecute(HttpURLConnection connection) {
                                connection.setRequestProperty("Authorization", "Bearer " + mToken);
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

    protected class TemperatureGetter implements IThingsGetter {

        @Override
        public String getLoadMessage() {
            return "Getting SmartThings Temperature Gauges";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();
            JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(
                    new URL(mRequestUrl + "/temperatures"),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jObjURI = new JSONArray(result.getResult());
            for (int i = 0; i < jObjURI.length(); i++) {
                JSONObject jDev = jObjURI.getJSONObject(i);
                Temperature t = new Temperature();
                t.setId(jDev.getString("id"));
                t.setName(jDev.getString("name"));
                t.setTemperature(jDev.getInt("value"), false);
                t.setService(ServicesTypes.SmartThings);
                t.initialise();
                things.add(t);
            }

            return things;
        }

        @Override
        public int getUniqueId() {
            return 7;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{Temperature.class};
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

    protected class ModesGetter implements IThingsGetter {

        @Override
        public String getLoadMessage() {
            return "Getting SmartThings Modes";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();
            JsonExecutorResult result = JsonExecutor.fulfil(new JsonExecutorRequest(
                    new URL(mRequestUrl + "/modes"),
                    JsonExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPreExecute(HttpURLConnection connection) {
                            connection.setRequestProperty("Authorization", "Bearer " + mToken);
                        }
                    }));

            if (!result.isSuccess())
                throw result.getError();

            JSONArray jObjURI = new JSONArray(result.getResult());
            SmartThingMode smartThingMode = new SmartThingMode();
            smartThingMode.setName("SmartThings Modes");
            smartThingMode.setService(ServicesTypes.SmartThings);
            for (int i = 0; i < jObjURI.length(); i++) {
                JSONObject jMode = jObjURI.getJSONObject(i);
                smartThingMode.addMode(jMode.getString("name"), jMode.getBoolean("active"));
            }

            smartThingMode.initialise();
            things.add(smartThingMode);

            return things;
        }

        @Override
        public int getUniqueId() {
            return 8;
        }

        @Override
        public Type[] getThingType() {
            return new Type[]{SmartThingMode.class};
        }

        @Override
        public IExecutor<?> getExecutor(String Id) {
            return new IExecutor<String>() {
                @Override
                protected JsonExecutorResult execute(IThing thing) {
                    try {
                        String url = String.format("%s/modes/%s", mRequestUrl, getValue());
                        return JsonExecutor.fulfil(new JsonExecutorRequest(new URL(url), JsonExecutorRequest.Types.PUT, new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPreExecute(HttpURLConnection connection) {
                                connection.setRequestProperty("Authorization", "Bearer " + mToken);
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
