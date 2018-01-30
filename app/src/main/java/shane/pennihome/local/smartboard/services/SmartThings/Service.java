package shane.pennihome.local.smartboard.services.SmartThings;

import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import shane.pennihome.local.smartboard.comms.Executor;
import shane.pennihome.local.smartboard.comms.ExecutorRequest;
import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 29/01/18.
 */

public class Service extends IService {
    public final static String ST_CLIENT_ID = "953f9b86-9a48-461a-91e9-c9544dd980c4";
    public final static String ST_CLIENT_SECRET = "8da94852-2c55-47b0-89ee-79ce8ac3bcd5";
    public final static String ST_REDIRECT_URI = "http://localhost:4567/oauth/callback";
    public final static String ST_GRANT_TYPE = "authorization_code";
    public final static String ST_TOKEN_URL = "https://graph.api.smartthings.com/oauth/token";
    public final static String ST_OAUTH_URL = "https://graph.api.smartthings.com/oauth/authorize";
    public final static String ST_ENDPOINT_URL = "https://graph.api.smartthings.com/api/smartapps/endpoints";
    public final static String ST_OAUTH_SCOPE = "app";
    public final static String ST_SERVER_URI = "https://www.googleapis.com/auth/urlshortener";

    String mToken;
    String mRequestUrl;
    private Date mExpires;
    private String mType;
    private String mAuthorisationCode;

    @Override
    protected Things getThings() throws Exception {
        return null;
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    public DialogFragment getRegisterDialog() {
        RegisterDialog registerDialog = new RegisterDialog();
        registerDialog.setService(this);
        return registerDialog;
    }

    @Override
    protected boolean isValid() {
        return false;
    }

    @Override
    protected void register() throws Exception {
        Executor executor = new Executor();

        ExecutorRequest request = new ExecutorRequest(new URL(ST_TOKEN_URL), ExecutorRequest.Types.POST);
        request.getQueryStringParameters().add(new NameValuePair("code", getAuthorisationCode()));
        request.getQueryStringParameters().add(new NameValuePair("client_id", ST_CLIENT_ID));
        request.getQueryStringParameters().add(new NameValuePair("client_secret", ST_CLIENT_SECRET));
        request.getQueryStringParameters().add(new NameValuePair("redirect_uri", ST_REDIRECT_URI));
        request.getQueryStringParameters().add(new NameValuePair("grant_type", ST_GRANT_TYPE));

        ExecutorResult executorResult = executor.execute(request).get();
        if (!executorResult.isSuccess())
            throw executorResult.getError();

        JSONObject jsToken = buildJson(executorResult.getResult());

        mToken = jsToken.getString("access_token");
        mType = jsToken.getString("token_type");
        int minutes = Integer.parseInt(jsToken.getString("expires_in"));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, minutes);
        mExpires = c.getTime();
    }

    @Override
    public boolean isAuthorised() {
        Calendar c = Calendar.getInstance();
        return (!TextUtils.isEmpty(mToken) && mExpires.after(c.getTime()) && !TextUtils.isEmpty(mRequestUrl));
    }

    @Override
    public boolean isAwaitingAuthorisation() {
        Calendar c = Calendar.getInstance();
        return (!TextUtils.isEmpty(mToken) && mExpires.after(c.getTime()) && TextUtils.isEmpty(mRequestUrl));
    }

    @Override
    protected void connect() throws Exception {
        Executor executor = new Executor();
        ExecutorResult result = executor.execute(
                new ExecutorRequest(new URL(ST_ENDPOINT_URL),
                        ExecutorRequest.Types.GET,
                        new OnExecutorRequestActionListener() {
                            @Override
                            public void OnPresend(HttpURLConnection connection) {
                                connection.setRequestProperty("Authorization", "Bearer " + mToken);
                            }
                        })).get();

        if (!result.isSuccess())
            throw result.getError();

        JSONObject jsUrl = buildJson(result.getResult());
        mRequestUrl = jsUrl.getString("uri");
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
}
