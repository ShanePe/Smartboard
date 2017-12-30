package shane.pennihome.local.smartboard.Data;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by shane on 27/12/17.
 */

public class SmartThingsTokenInfo {
    private String _token;
    private Date _expires;
    private String _type;
    private String _authCode;
    private String _requestUrl;

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
        return _token;
    }

    public void setToken(String token) {
        this._token = token;
    }

    public Date getExpires() {
        if (_expires == null) {
            Calendar c = Calendar.getInstance();
            return c.getTime();
        } else
            return _expires;
    }

    public void setExpires(Date expires) {
        this._expires = expires;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getAuthCode() {
        return _authCode;
    }

    public void setAuthCode(String authCode) {
        _authCode = authCode;
    }

    public void Save() {
        SharedPreferences.Editor editor = Globals.getSharedPreferences().edit();
        editor.putString("tokenInfo", this.toJson());
        editor.apply();
    }

    public String getRequestUrl() {
        return _requestUrl;
    }

    public void setRequestUrl(String _requestUrl) {
        this._requestUrl = _requestUrl;
    }
}
