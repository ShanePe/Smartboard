package shane.pennihome.local.smartboard.things.routines;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Routine extends IThing {
    public static Routine Load(String json) {
        try {
            return IThing.Load(Routine.class, json);
        } catch (Exception e) {
            return new Routine();
        }
    }

    @Override
    public Types getThingType() {
        return Types.Routine;
    }

//    @Override
//    public void messageReceived(IMessage<?> message) {
//
//    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
