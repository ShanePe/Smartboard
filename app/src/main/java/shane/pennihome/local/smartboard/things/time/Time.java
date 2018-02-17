package shane.pennihome.local.smartboard.things.time;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 17/02/18.
 */

@SuppressWarnings("ALL")
public class Time extends IThing {
    public static Time Load(String json) {
        try {
            return IThing.Load(Time.class, json);
        } catch (Exception e) {
            return new Time();
        }
    }

    @Override
    public void verifyState(IThing compare) {
    }

    @Override
    public Types getThingType() {
        return Types.Time;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }


}
