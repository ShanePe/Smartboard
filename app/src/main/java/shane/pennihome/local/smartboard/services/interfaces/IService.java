package shane.pennihome.local.smartboard.services.interfaces;


import android.support.v4.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 29/01/18.
 */

public abstract class IService extends IDatabaseObject {
    public enum Services {SmartThings, PhilipsHue}
    @IgnoreOnCopy
    private String mInstance;

    public IService() {
        mInstance = this.getClass().getSimpleName();
    }

    protected abstract Things getThings() throws Exception;

    protected abstract String getDescription();

    public abstract DialogFragment getRegisterDialog();

    protected abstract boolean isValid();

    protected abstract void register() throws Exception;

    protected abstract boolean isAuthorised();

    protected abstract boolean isAwaitingAuthorisation();

    protected abstract void connect() throws Exception;

    protected abstract ArrayList<IThingsGetter> getThingGetters();

    protected JSONObject buildJson(String data) throws JSONException {
        Object json = new JSONTokener(data).nextValue();
        if (json instanceof JSONObject)
            return (JSONObject) json;
        else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            return array.getJSONObject(0);//return the first object
        }

        throw new JSONException("Invalid JSON Response");
    }
}
