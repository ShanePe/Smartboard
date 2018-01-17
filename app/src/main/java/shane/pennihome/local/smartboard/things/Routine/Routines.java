package shane.pennihome.local.smartboard.things.Routine;

import shane.pennihome.local.smartboard.things.Interface.IThingCollection;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Routines extends IThingCollection<Routine> {
    @Override
    public int SortCompare(Routine a, Routine b) {
        return a.getName().compareTo(b.getName());
    }
}
