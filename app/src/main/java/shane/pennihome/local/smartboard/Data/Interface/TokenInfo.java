package shane.pennihome.local.smartboard.Data.Interface;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import shane.pennihome.local.smartboard.Data.Globals;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public abstract class TokenInfo {

    private static <V extends TokenInfo> V fromJson(Class<V> cls, String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }

    public static <V extends TokenInfo> V Load(Class<V> cls) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        String objJson = Globals.getSharedPreferences().getString(inst.getKey(), "");
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);

        return inst;
    }

    protected abstract String getKey();

    public abstract boolean isAuthorised();

    public abstract boolean isAwaitingAuthorisation();

    private String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @SuppressLint("ApplySharedPref")
    public void Save() {
        SharedPreferences.Editor editor = Globals.getSharedPreferences().edit();
        editor.putString(getKey(), this.toJson());
        editor.commit();
    }
}
