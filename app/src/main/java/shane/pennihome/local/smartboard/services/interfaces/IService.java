package shane.pennihome.local.smartboard.services.interfaces;


import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 29/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IService extends IDatabaseObject {
    @SuppressWarnings("FieldCanBeLocal")
    @IgnoreOnCopy
    private final String mInstance;
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

    public boolean isActive() {
        return Monitor.getMonitor().getServices().hasService(this);
    }

    public abstract DialogFragment getRegisterDialog();

    @SuppressWarnings("SameReturnValue")
    public abstract int getDrawableIconResource();

    protected abstract void register() throws Exception;

    protected abstract boolean isRegistered();

    @SuppressWarnings("SameReturnValue")
    public abstract boolean isAwaitingAction();

    public abstract void connect() throws Exception;

    public abstract ArrayList<IThingsGetter> getThingGetters();

    public abstract Things getThings() throws Exception;

    public abstract <T extends IThing> ArrayList<IThingsGetter> getThingsGetter(Class<T> cls);
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
