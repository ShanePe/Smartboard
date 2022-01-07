package shane.pennihome.local.smartboard.things.switchgroup;

import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroup;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class SwitchGroup extends Switch implements IGroup {
    private Things mThings = null;

    public static SwitchGroup Load(String json) {
        try {
            return IThing.Load(SwitchGroup.class, json);
        } catch (Exception e) {
            return new SwitchGroup();
        }
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.SwitchGroup;
    }

    @Override
    public void verifyState(IThing compare) {
        for (Switch s : getChildThings().cast(Switch.class))
            s.verifyState(compare);
    }

    @Override
    public void setDimmerLevel(int dimmerLevel, boolean fireBroadcast) {
        for (Switch s : getChildThings().cast(Switch.class))
            s.setDimmerLevel(dimmerLevel, fireBroadcast);
    }

    @Override
    public boolean isDimmer() {
        for (Switch s : getChildThings().cast(Switch.class))
            if (!s.isDimmer())
                return false;
        return true;
    }

    @Override
    public void setOn(boolean on, boolean fireBroadcast) {
        for (Switch s : getChildThings().cast(Switch.class))
            if (s.isOn() != on)
                s.setOn(on, fireBroadcast);
    }

    @Override
    public boolean isOn() {
        boolean state = true;
        for (Switch s : getChildThings().cast(Switch.class))
            if (!s.isOn()) {
                state = false;
                break;
            }

        return state;
    }

    @Override
    public boolean isUnreachable() {
        for (Switch s : getChildThings().cast(Switch.class))
            if (s.isUnreachable())
                return true;

        return getChildThings().size() == 0;
    }

    @Override
    public int getDimmerLevel() {
        int level = 0;
        if (getChildThings().size() == 0)
            return 0;

        for (Switch s : getChildThings().cast(Switch.class))
            level += s.getDimmerLevel();

        //noinspection IntegerDivisionInFloatingPointContext
        return Math.round(level / getChildThings().size());
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public Things getChildThings() {
        if (mThings == null)
            mThings = new Things();

        for (int i = mThings.size() - 1; i >= 0; i--)
            if (mThings.get(i) == null)
                mThings.remove(i);
        return mThings;
    }

    @Override
    public void setChildThings(Things things) {
        mThings = things;
    }

    @Override
    public String getKey() {
        return getDataID();
    }
}
