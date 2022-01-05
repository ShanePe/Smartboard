package shane.pennihome.local.smartboard.thingsframework.interfaces;

import java.util.Date;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.routinegroup.RoutineGroup;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingMode;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switchgroup.SwitchGroup;
import shane.pennihome.local.smartboard.things.temperature.Temperature;
import shane.pennihome.local.smartboard.things.time.Time;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;

@SuppressWarnings({"unused", "unchecked"})
public abstract class IThing extends IDatabaseObject {
    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    @IgnoreOnCopy
    private transient boolean mUnreachable;
    private String mId;
    private IService.ServicesTypes mServicesTypes;

    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    private static <V extends IThing> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.get().fromJson(json, cls);
    }

    static <T extends IThing> T CreateFromType(Types type) throws Exception {
        switch (type) {
            case Switch:
                return (T) new Switch();
            case Routine:
                return (T) new Routine();
            case Temperature:
                return (T) new Temperature();
            case SmartThingMode:
                return (T) new SmartThingMode();
            case Time:
                return (T) new Time();
            case SwitchGroup:
                return (T) new SwitchGroup();
            case RoutineGroup:
                return (T) new RoutineGroup();
            default:
                throw new Exception("Invalid Type to create");
        }
    }

    public abstract void verifyState(IThing compare);

    public void initialise() {
    }

    public boolean isUnreachable() {
        return mUnreachable;
    }

    public void setUnreachable(boolean unreachable, boolean fireBroadcast) {
        boolean pre = mUnreachable;
        mUnreachable = unreachable;

        if (pre != mUnreachable && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.Unreachable));
    }

    public JsonExecutorResult execute() {
        return execute(getExecutor());
    }

    public JsonExecutorResult execute(IExecutor<?> executor) {
        return executor.execute(this);
    }

    public IExecutor<?> getExecutor() {
        return getExecutor("");
    }

    public IExecutor<?> getExecutor(String id) {
        IService service = Monitor.getMonitor().getServices().getByType(getServiceType());
        if(service!=null)
            return service.getExecutor(this, id);
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

    public enum Types {
        Switch, Routine, Temperature, SmartThingMode, Time, SwitchGroup, RoutineGroup
    }
}
