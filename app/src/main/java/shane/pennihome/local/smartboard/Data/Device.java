package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings({"ALL", "unused"})
public class Device extends Thing {
    private States mState;
    private String mType;

    public static Device Load(String json) {
        try {
            return Thing.Load(Device.class, json);
        } catch (Exception e) {
            return new Device();
        }
    }

    public States getState() {
        return mState;
    }

    public void setState(States state) {
        States pre = getState();
        this.mState = state;

        if (pre != state && mOnThingListener != null)
            mOnThingListener.StateChanged();
    }

    public String getType() {
        return mType;
    }

    public void setType(String _type) {
        this.mType = _type;
    }

    public boolean isOn() {
        return getState() == States.On;
    }

    @Override
    public void successfulToggle(Thing thing) {
        if (getState() == States.Off)
            setState(States.On);
        else if (getState() == States.On)
            setState(States.Off);
    }

    public enum States {Off, On, Unreachable}
}
