package shane.pennihome.local.smartboard.data.interfaces;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public abstract class ITokenInfo {

    private static <V extends ITokenInfo> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.get().fromJson(json, cls);
    }

    public static <V extends ITokenInfo> V Load(Class<V> cls) throws IllegalAccessException, InstantiationException {
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
        return JsonBuilder.get().toJson(this);
    }

    @SuppressLint("ApplySharedPref")
    public void Save() {
        SharedPreferences.Editor editor = Globals.getSharedPreferences().edit();
        editor.putString(getKey(), this.toJson());
        editor.commit();
    }
}
