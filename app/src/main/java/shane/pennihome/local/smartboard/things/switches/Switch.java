package shane.pennihome.local.smartboard.things.switches;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
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
    }

    @Override
    public void setUnreachable(boolean unreachable, boolean fireBroadcast) {
        boolean pre = unreachable;
        super.setUnreachable(unreachable, fireBroadcast);
    }

    @Override
    public JsonExecutorResult execute(IExecutor<?> executor) {
        JsonExecutorResult result = super.execute(executor);
        if(result!=null)
            if (result.isSuccess()) {
                if (executor instanceof SmartThingsService.SwitchGetter.LevelExecutor)
                    setDimmerLevel(((SmartThingsService.SwitchGetter.LevelExecutor) executor).getValue(), true);
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

    @Override
    public Types getThingType() {
        return Types.Switch;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
