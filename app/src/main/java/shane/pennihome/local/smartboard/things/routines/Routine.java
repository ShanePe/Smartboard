package shane.pennihome.local.smartboard.things.routines;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 28/12/17.
 */

public class Routine extends IThing {
    public static Routine Load(String json) {
        try {
            return IThing.Load(Routine.class, json);
        } catch (Exception e) {
            return new Routine();
        }
    }

    @Override
    public void verifyState(IThing compare) {
    }

    @Override
    public boolean isStateful() {
        return false;
    }

    @Override
    public Types getThingType() {
        return Types.Routine;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
