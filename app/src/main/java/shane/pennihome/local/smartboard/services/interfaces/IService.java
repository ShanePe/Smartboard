package shane.pennihome.local.smartboard.services.interfaces;


import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 29/01/18.
 */

public abstract class IService extends IDatabaseObject {
    @IgnoreOnCopy
    private String mInstance;
    public IService() {

        mInstance = this.getClass().getSimpleName();
    }

    public static IService Load(String json) {
        IService ret = null;
        try {
            ret = JsonBuilder.Get().fromJson(json, IService.class);
        } catch (Exception e) {
            Log.e("Smartboard", "Error : " + e.getMessage());
        }

        return ret;
    }

    protected abstract Things getThings() throws Exception;

    public abstract DialogFragment getRegisterDialog();

    protected abstract void register() throws Exception;

    public abstract boolean isRegistered();

    public abstract boolean isAwaitingAction();

    public abstract void connect() throws Exception;

    public abstract ArrayList<IThingsGetter> getThingGetters();

    public boolean isValid() {
        return isRegistered() && !isAwaitingAction();
    }

    protected JSONObject buildJsonResponse(String data) throws JSONException {
        Object json = new JSONTokener(data).nextValue();
        if (json instanceof JSONObject)
            return (JSONObject) json;
        else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            return array.getJSONObject(0);//return the first object
        }

        throw new JSONException("Invalid JSON Response");
    }

    public enum ServicesTypes {SmartThings, PhilipsHue}
}
