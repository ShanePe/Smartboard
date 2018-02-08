package shane.pennihome.local.smartboard.things.switches;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings({"ALL", "unused"})
public class Switch extends IThing {
    private String mType;
    private boolean mOn;
    @IgnoreOnCopy
//    private OnSwitchStateChangeListener mOnSwitchStateChangeListener;

    public static Switch Load(String json) {
        try {
            return IThing.Load(Switch.class, json);
        } catch (Exception e) {
            return new Switch();
        }
    }

//    public OnSwitchStateChangeListener getOnSwitchStateChangeListener() {
//        return mOnSwitchStateChangeListener;
//    }
//
//    public void setOnSwitchStateChangeListener(OnSwitchStateChangeListener onSwitchStateChangeListener) {
//        mOnSwitchStateChangeListener = onSwitchStateChangeListener;
//    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(final boolean on, boolean fireBroadcast) {
        boolean pre = mOn;
        mOn = on;
//        if (pre != mOn && mOnSwitchStateChangeListener != null)
//            mOnSwitchStateChangeListener.OnStateChange(isOn());
        if (pre != mOn && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.State));
    }

    @Override
    public void setUnreachable(boolean unreachable, boolean fireBroadcast) {
        boolean pre = unreachable;
        super.setUnreachable(unreachable, fireBroadcast);

//        if (pre != isUnreachable() && mOnSwitchStateChangeListener != null)
//            mOnSwitchStateChangeListener.OnStateChange(isOn());
    }

    @Override
    public JsonExecutorResult execute() {
        JsonExecutorResult result = super.execute();
        if(result!=null)
            if(result.isSuccess())
                setOn(!isOn(), true);
        return result;
    }

    public String getType() {
        return mType;
    }

    public void setType(String _type) {
        this.mType = _type;
    }

    @Override
    public Types getThingType() {
        return Types.Switch;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
