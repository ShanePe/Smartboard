package shane.pennihome.local.smartboard.services.SmartThings;

import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Executor;
import shane.pennihome.local.smartboard.comms.ExecutorRequest;
import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 29/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SmartThingsService extends IService {
    final static String ST_CLIENT_ID = "953f9b86-9a48-461a-91e9-c9544dd980c4";
    final static String ST_REDIRECT_URI = "http://localhost:4567/oauth/callback";
    final static String ST_OAUTH_URL = "https://graph.api.smartthings.com/oauth/authorize";
    final static String ST_OAUTH_SCOPE = "app";
    private final static String ST_CLIENT_SECRET = "8da94852-2c55-47b0-89ee-79ce8ac3bcd5";
    private final static String ST_GRANT_TYPE = "authorization_code";
    private final static String ST_TOKEN_URL = "https://graph.api.smartthings.com/oauth/token";
    private final static String ST_ENDPOINT_URL = "https://graph.api.smartthings.com/api/smartapps/endpoints";
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
    public Things getThings() throws Exception {
        Things things = new Things();
        for (IThingsGetter g : getThingGetters())
            things.addAll(g.getThings());
        return things;
    }

    @Override
    public DialogFragment getRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.setService(this);
        return registerDialog;
    }

    @Override
    public int getDrawableIconResource() {
        return R.drawable.icon_st_logo_large;
    }

    @Override
    protected void register() throws Exception {
        ExecutorRequest request = new ExecutorRequest(new URL(ST_TOKEN_URL), ExecutorRequest.Types.POST);
        request.getQueryStringParameters().add(new NameValuePair("code", getAuthorisationCode()));
        request.getQueryStringParameters().add(new NameValuePair("client_id", ST_CLIENT_ID));
        request.getQueryStringParameters().add(new NameValuePair("client_secret", ST_CLIENT_SECRET));
        request.getQueryStringParameters().add(new NameValuePair("redirect_uri", ST_REDIRECT_URI));
        request.getQueryStringParameters().add(new NameValuePair("grant_type", ST_GRANT_TYPE));

        ExecutorResult executorResult = Executor.fulfil(request);
        if (!executorResult.isSuccess())
            throw executorResult.getError();

        JSONObject jsToken = buildJsonResponse(executorResult.getResult());

        mToken = jsToken.getString("access_token");
        mType = jsToken.getString("token_type");
        int minutes = Integer.parseInt(jsToken.getString("expires_in"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, minutes);
        mExpires = c.getTime();
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
        @SuppressWarnings("unused") ExecutorResult result = Executor.fulfil(
                new ExecutorRequest(new URL(ST_ENDPOINT_URL),
                        ExecutorRequest.Types.GET,
                        new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPreExecute(HttpURLConnection connection) {
                                connection.setRequestProperty("Authorization", "Bearer " + mToken);
                            }
                        }));

        if (!result.isSuccess())
            throw result.getError();

        JSONObject jsUrl = buildJsonResponse(result.getResult());
        mRequestUrl = jsUrl.getString("uri");
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        ArrayList<IThingsGetter> thingsGetters = new ArrayList<>();
        if(TextUtils.isEmpty(mRequestUrl))
            thingsGetters.add(new Connector());
        thingsGetters.add(new SwitchGetter());
        thingsGetters.add(new RoutineGetter());
        return thingsGetters;
    }

    @Override
    public <T extends IThing> ArrayList<IThingsGetter> getThingsGetter(Class<T> cls) {
        ArrayList<IThingsGetter> thingGetters = new ArrayList<>();
        for (IThingsGetter t : getThingGetters())
            if (t.getThingType().equals(cls) || t.getThingType().equals(IThing.class))
                thingGetters.add(t);
        return thingGetters;
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

    protected class Connector implements IThingsGetter{

        public String getLoadMessage() {
            return "Connecting to SmartThings";
        }

        public Things getThings() throws Exception {
            ExecutorResult result = Executor.fulfil(
                    new ExecutorRequest(new URL(ST_ENDPOINT_URL),
                            ExecutorRequest.Types.GET,
                            new OnExecutorRequestActionListener() {
                                @Override
                                public void OnPreExecute(HttpURLConnection connection) {
                                    connection.setRequestProperty("Authorization", "Bearer " + mToken);
                                }
                            }));

            if (!result.isSuccess())
                throw result.getError();

            JSONObject jsUrl = buildJsonResponse(result.getResult());
            mRequestUrl = jsUrl.getString("uri");

            return new Things();
        }

        @Override
        public int getUniqueId() {
            return 1;
        }

        @Override
        public Type getThingType() {
            return IThing.class;
        }
    }

    protected class SwitchGetter implements IThingsGetter {
        public String getLoadMessage() {
            return "Getting SmartThings switches";
        }

        public Things getThings() throws Exception {
            Things things = new Things();
            ExecutorResult result = Executor.fulfil(new ExecutorRequest(
                    new URL(mRequestUrl + "/switches"),
                    ExecutorRequest.Types.GET,
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
                d.setOn(jDev.getString("value").equals("on"));
                d.setType(jDev.getString("type"));
                d.setService(ServicesTypes.SmartThings);
                things.add(d);
            }

            return things;
        }

        @Override
        public int getUniqueId() {
            return 2;
        }

        @Override
        public Type getThingType() {
            return Switch.class;
        }
    }

    protected class RoutineGetter implements IThingsGetter {

        public String getLoadMessage() {
            return "Getting SmartThings Routines";
        }

        public Things getThings() throws Exception {
            Things things = new Things();
            ExecutorResult result = Executor.fulfil(new ExecutorRequest(
                    new URL(mRequestUrl + "/routines"),
                    ExecutorRequest.Types.GET,
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
                things.add(r);
            }

            return things;
        }

        @Override
        public int getUniqueId() {
            return 3;
        }

        @Override
        public Type getThingType() {
            return Routine.class;
        }
    }
}
