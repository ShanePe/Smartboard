package shane.pennihome.local.smartboard.services.interfaces;


import android.support.v4.app.DialogFragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
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

    public static <T extends IService> T fromJson(Class<T> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
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

    public abstract ServicesTypes getServiceType();

    public abstract <T extends IThing> ArrayList<IThingsGetter> getThingsGetter(Class<T> cls);
    public abstract <T extends IThingsGetter, V extends IThing> T getThingExecutor(Class<V> cls);

    public ExecutorResult executeThing(IThing thing)
    {
        IThingsGetter executor = null;
        if(thing instanceof Switch)
            executor = getThingExecutor(Switch.class);
        else if(thing instanceof Routine)
            executor = getThingExecutor(Routine.class);

        if(executor!=null)
            return executor.execute(thing);
        return null;
    }


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
