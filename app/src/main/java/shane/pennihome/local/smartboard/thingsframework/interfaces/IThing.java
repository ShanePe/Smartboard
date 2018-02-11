package shane.pennihome.local.smartboard.thingsframework.interfaces;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing extends IDatabaseObject {
    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    @IgnoreOnCopy
    private transient boolean mUnreachable;
    private String mId;
    private IService.ServicesTypes mServicesTypes;
    //@IgnoreOnCopy
    //private transient OnThingActionListener mOnThingActionListener;

    public abstract void verifyState(IThing compare);
    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    private static <V extends IThing> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.get().fromJson(json, cls);
    }

    public void initialise() {
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isUnreachable() {
        return mUnreachable;
    }

    public void setUnreachable(boolean unreachable, boolean fireBroadcast) {
        boolean pre = mUnreachable;
        mUnreachable = unreachable;

        if (pre != mUnreachable && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.Unreachable));
//        if(mOnThingActionListener != null)
//            mOnThingActionListener.OnReachableStateChanged(mUnreachable);
    }

    public JsonExecutorResult execute()
    {
        IService service = Monitor.getMonitor().getServices().getByType(getServiceType());
        if(service!=null)
            return service.executeThing(this);
        return null;
    }

    public IService.ServicesTypes getServiceType() {
        return mServicesTypes;
    }

    public void setService(IService.ServicesTypes servicesTypes) {
        this.mServicesTypes = servicesTypes;
    }

    public String getId() {
        return mId == null ? "" : mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String toJson() {
        return JsonBuilder.get().toJson(this);
    }

    public String getKey() {
        return String.format("%s%s%s%s", getId(), getName(), getServiceType(), getThingType());
    }

    public abstract Types getThingType();
    /**
     * Created by shane on 29/12/17.
     */

    public enum Types {
        Switch, Routine, Temperature
    }
}
