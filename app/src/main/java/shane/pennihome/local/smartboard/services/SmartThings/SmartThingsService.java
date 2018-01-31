package shane.pennihome.local.smartboard.services.SmartThings;

import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

/**
 * Created by shane on 29/01/18.
 */

public class SmartThingsService extends IService {
    public final static String ST_CLIENT_ID = "953f9b86-9a48-461a-91e9-c9544dd980c4";
    public final static String ST_CLIENT_SECRET = "8da94852-2c55-47b0-89ee-79ce8ac3bcd5";
    public final static String ST_REDIRECT_URI = "http://localhost:4567/oauth/callback";
    public final static String ST_GRANT_TYPE = "authorization_code";
    public final static String ST_TOKEN_URL = "https://graph.api.smartthings.com/oauth/token";
    public final static String ST_OAUTH_URL = "https://graph.api.smartthings.com/oauth/authorize";
    public final static String ST_ENDPOINT_URL = "https://graph.api.smartthings.com/api/smartapps/endpoints";
    public final static String ST_OAUTH_SCOPE = "app";

    String mToken;
    String mRequestUrl;
    private Date mExpires;
    private String mType;
    private String mAuthorisationCode;

    public static SmartThingsService Load(String json) {
        try {
            return IService.Load(SmartThingsService.class, json);
        } catch (Exception e) {
            return new SmartThingsService();
        }
    }

    @Override
    protected Things getThings() throws Exception {
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
        ExecutorResult result = Executor.fulfil(
                new ExecutorRequest(new URL(ST_ENDPOINT_URL),
                        ExecutorRequest.Types.GET,
                        new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPresend(HttpURLConnection connection) {
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
    public Types getDatabaseType() {
        return Types.Service;
    }

    public String getAuthorisationCode() {
        return mAuthorisationCode;
    }

    public void setAuthorisationCode(String authorisationCode) {
        mAuthorisationCode = authorisationCode;
    }

    protected class Connector extends IThingsGetter{

        @Override
        public String getLoadMessage() {
            return "Connecting to SmartThings";
        }

        @Override
        public Things getThings() throws Exception {
            ExecutorResult result = Executor.fulfil(
                    new ExecutorRequest(new URL(ST_ENDPOINT_URL),
                            ExecutorRequest.Types.GET,
                            new OnExecutorRequestActionListener() {
                                @Override
                                public void OnPresend(HttpURLConnection connection) {
                                    connection.setRequestProperty("Authorization", "Bearer " + mToken);
                                }
                            }));

            if (!result.isSuccess())
                throw result.getError();

            JSONObject jsUrl = buildJsonResponse(result.getResult());
            mRequestUrl = jsUrl.getString("uri");

            return new Things();
        }
    }

    protected class SwitchGetter extends IThingsGetter {

        private Switch.States getState(JSONObject j) throws JSONException {
            if (j.getString("value").equals("on"))
                return Switch.States.On;
            else
                return Switch.States.Off;
        }

        @Override
        public String getLoadMessage() {
            return "Getting SmartThings switches";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();
            ExecutorResult result = Executor.fulfil(new ExecutorRequest(
                    new URL(mRequestUrl + "/switches"),
                    ExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPresend(HttpURLConnection connection) {
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
                d.setState(getState(jDev));
                d.setType(jDev.getString("type"));
                d.setService(ServicesTypes.SmartThings);
                things.add(d);
            }

            return things;
        }
    }

    protected class RoutineGetter extends IThingsGetter {

        @Override
        public String getLoadMessage() {
            return "Getting SmartThings Routines";
        }

        @Override
        public Things getThings() throws Exception {
            Things things = new Things();
            ExecutorResult result = Executor.fulfil(new ExecutorRequest(
                    new URL(mRequestUrl + "/routines"),
                    ExecutorRequest.Types.GET,
                    new OnExecutorRequestActionListener() {
                        @Override
                        public void OnPresend(HttpURLConnection connection) {
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
    }
}
