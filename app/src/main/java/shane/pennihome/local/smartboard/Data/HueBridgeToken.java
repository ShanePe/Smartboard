package shane.pennihome.local.smartboard.Data;

import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by shane on 30/12/17.
 */

public class HueBridgeToken {
    private String mAddress;
    private String mId;
    private String mToken;

    public String getAddress() {
        if(mAddress == null)
            return "";
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    private static HueBridgeToken fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, HueBridgeToken.class);
    }

    public static HueBridgeToken Load() {
        String objJson = Globals.getSharedPreferences().getString("hueHubInfo", "");
        if (objJson.equals(""))
            return new HueBridgeToken();
        else
            return HueBridgeToken.fromJson(objJson);
    }

    private String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void Save() {
        SharedPreferences.Editor editor = Globals.getSharedPreferences().edit();
        editor.putString("hueHubInfo", this.toJson());
        editor.commit();
    }

    public String getToken() {
        if(mToken == null)
            return "";
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }
}
