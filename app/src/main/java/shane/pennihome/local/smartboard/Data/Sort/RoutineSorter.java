package shane.pennihome.local.smartboard.Data.Sort;

import java.util.Comparator;

import shane.pennihome.local.smartboard.Data.Routine;

/**
 * Created by shane on 28/12/17.
 */

public class RoutineSorter implements Comparator<Routine> {
    @Override
    public int compare(Routine o1, Routine o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
