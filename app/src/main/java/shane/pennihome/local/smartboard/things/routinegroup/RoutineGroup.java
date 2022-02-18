package shane.pennihome.local.smartboard.things.routinegroup;

import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroup;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class RoutineGroup extends Routine implements IGroup {
    private Things mThings = null;

    public static RoutineGroup Load(String json) {
        try {
            return IThing.Load(RoutineGroup.class, json);
        } catch (Exception e) {
            return new RoutineGroup();
        }
    }

    @Override
    public Types getThingType() {
        return Types.RoutineGroup;
    }

    @Override
    public void verifyState(IThing compare) {
        for (Routine s : getChildThings().cast(Routine.class))
            s.verifyState(compare);
    }

    @Override
    public boolean isUnreachable() {
        for (Routine s : getChildThings().cast(Routine.class))
            if (s.isUnreachable())
                return true;
        return false;
    }

    @Override
    public Things getChildThings() {
        if (mThings == null)
            mThings = new Things();
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

    @Override
    public boolean isStateful() {
        return false;
    }
}
