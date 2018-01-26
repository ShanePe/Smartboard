package shane.pennihome.local.smartboard.thingsframework;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public class Things extends IThings<IThing> {
    public <E extends IThing, F extends IThings<E>> F getOfType(Class<E> cls) {
        Things ret = new Things();
        for (IThing t : this)
            if (t.getClass() == cls)
                //noinspection unchecked
                ret.add(t);
        //noinspection unchecked
        return (F) ret;
    }

    public static Things getAvailableTypes() {

        Things things = new Things();
        if(Monitor.getThings().containsType(Switch.class))
            things.add(new Switch());
        if(Monitor.getThings().containsType(Routine.class))
            things.add(new Routine());
        return things;
    }
}
