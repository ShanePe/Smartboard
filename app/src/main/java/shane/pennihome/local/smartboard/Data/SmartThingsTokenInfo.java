package shane.pennihome.local.smartboard.Data;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shane on 27/12/17.
 */

public class SmartThingsTokenInfo {
    private String mToken;
    private Date mExpires;
    private String mType;
    private String mAuthCode;
    private String mRequestUrl;

    public static SmartThingsTokenInfo fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, SmartThingsTokenInfo.class);
    }

    public static SmartThingsTokenInfo Load() {
        String objJson = Globals.getSharedPreferences().getString("tokenInfo", "");
        if (objJson.equals(""))
            return new SmartThingsTokenInfo();
        else
            return SmartThingsTokenInfo.fromJson(objJson);
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public Date getExpires() {
        if (mExpires == null) {
            Calendar c = Calendar.getInstance();
            return c.getTime();
        } else
            return mExpires;
    }

    public void setExpires(Date expires) {
        this.mExpires = expires;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public void Save() {
        SharedPreferences.Editor editor = Globals.getSharedPreferences().edit();
        editor.putString("tokenInfo", this.toJson());
        editor.apply();
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public void setRequestUrl(String _requestUrl) {
        this.mRequestUrl = _requestUrl;
    }
}
