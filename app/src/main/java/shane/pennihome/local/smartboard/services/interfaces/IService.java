package shane.pennihome.local.smartboard.services.interfaces;


import android.content.Context;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingMode;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
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
        return JsonBuilder.get().fromJson(json, cls);
    }

    private <V extends IThing> IThingsGetter getThingExecutor(Class<V> cls) {
        for (IThingsGetter t : getThingGetters())
            if (t.getThingType().equals(cls) || t.getThingType().equals(IThing.class))
                return t;

        return null;
    }

    public <T extends IThing> ArrayList<IThingsGetter> getThingsGetter(Class<T> cls) {
        ArrayList<IThingsGetter> thingGetters = new ArrayList<>();
        for (IThingsGetter t : getThingGetters())
            if (t.getThingType().equals(cls) || t.getThingType().equals(IThing.class))
                thingGetters.add(t);
        return thingGetters;
    }

    public boolean isActive() {
        return Monitor.getMonitor().getServices().hasService(this);
    }

    public abstract IRegisterServiceFragment getRegisterDialog();

    @SuppressWarnings("SameReturnValue")
    public abstract int getDrawableIconResource();

    public void register(Context context, OnProcessCompleteListener<IService> onProcessCompleteListener)
    {
        new DBEngine(context).writeToDatabase(this);
        Monitor.getMonitor().AddService(context, this);
        if(onProcessCompleteListener!=null)
            onProcessCompleteListener.complete(true, this);
    }

    protected abstract boolean isRegistered();

    @SuppressWarnings("SameReturnValue")
    public abstract boolean isAwaitingAction();

    public abstract void connect() throws Exception;

    public abstract ArrayList<IThingsGetter> getThingGetters();

    public Things getThings() throws Exception
    {
        Things things = new Things();
        for (IThingsGetter g : getThingGetters())
            things.addAll(g.getThings());
        return things;
    }



    @SuppressWarnings("SameReturnValue")
    public abstract ServicesTypes getServiceType();

    public void unregister(Context context, OnProcessCompleteListener<Void> onProcessCompleteListener) {
        IService storedService = Monitor.getMonitor().getServices().getByType(this.getServiceType());
        if (storedService != null) {
            DBEngine engine = new DBEngine(context);
            engine.deleteFromDatabase(storedService);

            Monitor.getMonitor().removeService(storedService);
        }
        if (onProcessCompleteListener != null)
            onProcessCompleteListener.complete(true, null);
    }

    public IExecutor<?> getExecutor(IThing thing, String id)
    {
        IThingsGetter getter = null;
        if(thing instanceof Switch)
            getter = getThingExecutor(Switch.class);
        else if(thing instanceof Routine)
            getter = getThingExecutor(Routine.class);
        else if (thing instanceof SmartThingMode)
            getter = getThingExecutor(SmartThingMode.class);

        if (getter != null)
            return getter.getExecutor(id);
        return null;
    }

//    public JsonExecutorResult executeThing<T>(IThing thing)
//    {
//        IThingsGetter executor = null;
//        if(thing instanceof Switch)
//            executor = getThingExecutor(Switch.class);
//        else if(thing instanceof Routine)
//            executor = getThingExecutor(Routine.class);
//
//        if(executor!=null)
//            return executor.execute(thing);
//        return null;
//    }

    public boolean isValid() {
        return isRegistered() && !isAwaitingAction();
    }

    public enum ServicesTypes {SmartThings, PhilipsHue}
}
