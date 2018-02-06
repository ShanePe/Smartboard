package shane.pennihome.local.smartboard.things.switches;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Messages.SwitchStateChangedMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings({"ALL", "unused"})
public class Switch extends IThing implements IMessageSource {
    private String mType;
    private boolean mOn;
    @IgnoreOnCopy
    private OnSwitchStateChangeListener mOnSwitchStateChangeListener;

    public static Switch Load(String json) {
        try {
            return IThing.Load(Switch.class, json);
        } catch (Exception e) {
            return new Switch();
        }
    }

    public OnSwitchStateChangeListener getOnSwitchStateChangeListener() {
        return mOnSwitchStateChangeListener;
    }

    public void setOnSwitchStateChangeListener(OnSwitchStateChangeListener onSwitchStateChangeListener) {
        mOnSwitchStateChangeListener = onSwitchStateChangeListener;
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(final boolean on) {
        boolean pre = mOn;
        mOn = on;
        if (pre != mOn && mOnSwitchStateChangeListener != null)
            mOnSwitchStateChangeListener.OnStateChange(isOn());
    }

    @Override
    public void setUnreachable(boolean unreachable) {
        boolean pre = unreachable;
        super.setUnreachable(unreachable);
        if (pre != isUnreachable() && mOnSwitchStateChangeListener != null)
            mOnSwitchStateChangeListener.OnStateChange(isOn());
    }

    @Override
    public JsonExecutorResult execute() {
        JsonExecutorResult result = super.execute();
        if(result!=null)
            if(result.isSuccess())
                setOn(!isOn());
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
    public void messageReceived(IMessage<?> message) {
        if (message instanceof SwitchStateChangedMessage)
            if (message.getSource() != null) {
                Switch src = (Switch) message.getSource();
                setUnreachable(src.isUnreachable());
                setOn(src.isOn());
            }
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
