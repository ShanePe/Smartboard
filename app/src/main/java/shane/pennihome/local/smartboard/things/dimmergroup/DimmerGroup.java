package shane.pennihome.local.smartboard.things.dimmergroup;

import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroup;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DimmerGroup extends Switch implements IGroup {
    private Things mThings = null;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean mLastOnState;

    public static DimmerGroup Load(String json) {
        try {
            return IThing.Load(DimmerGroup.class, json);
        } catch (Exception e) {
            return new DimmerGroup();
        }
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.DimmerGroup;
    }

    @Override
    public void verifyState(IThing compare) {
        for (Switch s : getThings().cast(Switch.class))
            s.verifyState(compare);
    }

    @Override
    public void setDimmerLevel(int dimmerLevel, boolean fireBroadcast) {
        for (Switch s : getThings().cast(Switch.class))
            s.setDimmerLevel(dimmerLevel, fireBroadcast);
    }

    @Override
    public void setOn(boolean on, boolean fireBroadcast) {
        for (Switch s : getThings().cast(Switch.class))
            if (s.isOn() != on)
                s.setOn(on, fireBroadcast);
    }

    @Override
    public boolean isOn() {
        boolean state = true;
        for (Switch s : getThings().cast(Switch.class))
            if (!s.isOn()) {
                state = false;
                break;
            }

        mLastOnState = state;
        return state;
    }

    @Override
    public boolean isUnreachable() {
        for (Switch s : getThings().cast(Switch.class))
            if (s.isUnreachable())
                return true;
        return false;
    }

    @Override
    public int getDimmerLevel() {
        int level = 0;
        for (Switch s : getThings().cast(Switch.class))
            level += s.getDimmerLevel();

        return Math.round(level / getThings().size());
    }

    @Override
    public Things getThings() {
        if (mThings == null)
            mThings = new Things();
        return mThings;
    }

    @Override
    public void setThings(Things things) {
        mThings = things;
    }

    @Override
    public String getKey() {
        return getDataID();
    }
}
