package shane.pennihome.local.smartboard.things.routines;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Routines extends IThings<Routine> {
    @Override
    public int SortCompare(Routine a, Routine b) {
        return a.getName().compareTo(b.getName());
    }
}
