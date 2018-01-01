package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Routines extends ArrayList<Routine> {
    public void remove(Thing.Source source) {
        for (Routine r : this)
            if (r.getSource() == source)
                remove(r);
    }

    public void sort() {
        Collections.sort(this, new Comparator<Routine>() {
            @Override
            public int compare(Routine o1, Routine o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
