package shane.pennihome.local.smartboard.things.switches;

import android.support.annotation.ColorInt;

import java.util.Date;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings({"ALL", "unused"})
public class Switch extends IThing {
    private String mType;
    private boolean mOn;
    private boolean mIsDimmer;
    private int mDimmerLevel;
    private boolean mSupportsColour;
    private int mCurrentColour;
    private String mResource;
    @IgnoreOnCopy
    private Date mLastStateUpdate;

    public static Switch Load(String json) {
        try {
            return IThing.Load(Switch.class, json);
        } catch (Exception e) {
            return new Switch();
        }
    }

    public boolean isDimmer() {
        return mIsDimmer;
    }

    public void setIsDimmer(boolean isDimmer) {
        mIsDimmer = isDimmer;
    }

    public int getDimmerLevel() {
        return mDimmerLevel;
    }

    public void setDimmerLevel(int dimmerLevel, boolean fireBroadcast) {
        int pre = mDimmerLevel;
        mDimmerLevel = dimmerLevel;
        if (pre != mDimmerLevel && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.Level));
    }

    public Date getLastStateUpdate() {
        return mLastStateUpdate;
    }

    public void setLastStateUpdate(Date lastStateUpdate) {
        this.mLastStateUpdate = lastStateUpdate;
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(final boolean on, boolean fireBroadcast) {
        boolean pre = mOn;
        mOn = on;

        if (pre != mOn && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.State));
    }

    @Override
    public void verifyState(IThing compare) {
        Switch newSwitch = (Switch) compare;

        if (isOn() != newSwitch.isOn())
            setOn(newSwitch.isOn(), true);

        if (isDimmer()) {
            if (getDimmerLevel() != newSwitch.getDimmerLevel())
                setDimmerLevel(newSwitch.getDimmerLevel(), true);
        }

        if (SupportsColour() != newSwitch.SupportsColour())
            setSupportsColour(newSwitch.SupportsColour(), true);

        if (SupportsColour()) {
            if (getCurrentColour() != newSwitch.getCurrentColour())
                setCurrentColour(newSwitch.getCurrentColour(), true);
        }
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void setUnreachable(boolean unreachable, boolean fireBroadcast) {
        boolean pre = unreachable;
        super.setUnreachable(unreachable, fireBroadcast);
    }

    @Override
    public JsonExecutorResult execute(IExecutor executor) {
        JsonExecutorResult result = super.execute(executor);
        if (result != null)
            if (result.isSuccess()) {
                if (executor.getId().equals("level"))
                    setDimmerLevel((int) executor.getValue(), true);
                else
                    setOn(!isOn(), true);
            }
        return result;
    }

    public String getType() {
        return mType;
    }

    public void setType(String _type) {
        this.mType = _type;
    }

    public boolean SupportsColour() {
        return mSupportsColour;
    }

    public void setSupportsColour(boolean supportsColour, boolean fireBroadcast) {
        boolean pre = this.mSupportsColour;
        this.mSupportsColour = supportsColour;

        if (pre != supportsColour && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.SupportColour));
    }

    @ColorInt
    public int getCurrentColour() {
        return mCurrentColour;
    }

    public void setCurrentColour(@ColorInt int currentColour, boolean fireBroadcast) {
        int pre = this.mCurrentColour;
        this.mCurrentColour = currentColour;

        if (pre != currentColour && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.SupportColourChange));
    }

    public String getResource() {
        return mResource;
    }

    public void setResource(String mResource) {
        this.mResource = mResource;
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
