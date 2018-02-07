package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Messages.SwitchStateChangedMessage;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing extends IDatabaseObject {
    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    @IgnoreOnCopy
    private transient boolean mUnreachable;
    private String mId;
    private IService.ServicesTypes mServicesTypes;
    @IgnoreOnCopy
    private transient OnThingActionListener mOnThingActionListener;

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

    public void setUnreachable(boolean unreachable) {
        boolean pre = mUnreachable;
        mUnreachable = unreachable;
        if(mOnThingActionListener != null)
            mOnThingActionListener.OnReachableStateChanged(mUnreachable);
    }

    public abstract void messageReceived(IMessage<?> message);

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

    public OnThingActionListener getOnThingActionListener() {
        return mOnThingActionListener;
    }

    public void setOnThingActionListener(OnThingActionListener onthingactionlistener) {
        this.mOnThingActionListener = onthingactionlistener;
    }

    public abstract Types getThingType();
    /**
     * Created by shane on 29/12/17.
     */

    public enum Types {
        Switch, Routine
    }
}
