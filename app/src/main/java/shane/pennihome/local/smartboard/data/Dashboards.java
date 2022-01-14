package shane.pennihome.local.smartboard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public class Dashboards extends ArrayList<Dashboard> {
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Dashboard item = this.remove(fromPosition);
        this.add(toPosition, item);
    }

    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(this, toPosition, fromPosition);
    }

    public void sort() {
        Collections.sort(this, new Comparator<Dashboard>() {
            @Override
            public int compare(Dashboard o1, Dashboard o2) {
                return Long.compare(o1.getPosition(), o2.getPosition());
            }
        });
    }

    public void clear() {
        for (int i = this.size() - 1; i >= 0; i--) {
            this.get(i).clear();
            this.set(i, null);
            this.remove(i);
        }
    }
}
