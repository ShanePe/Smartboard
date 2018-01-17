package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shane on 16/01/18.
 */

public class Dashboards extends ArrayList<Dashboard> {
    public void sort() {
        Collections.sort(this, new Comparator<Dashboard>() {
            @Override
            public int compare(Dashboard o1, Dashboard o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
