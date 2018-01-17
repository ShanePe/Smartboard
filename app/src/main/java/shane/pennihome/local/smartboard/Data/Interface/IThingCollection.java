package shane.pennihome.local.smartboard.Data.Interface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shane on 16/01/18.
 */

public abstract class IThingCollection<T extends IThing> extends ArrayList<T> {
    public void remove(IThing.Source source) {
        for (T t : this)
            if (t.getSource() == source)
                remove(t);
    }

    public T getbyId(String Id) {
        for (T t : this)
            if (t.getId().equals(Id))
                return t;

        return null;
    }

    public abstract int SortCompare(T a, T b);

    public void sort() {
        Collections.sort(this, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return SortCompare(o1, o2);
            }
        });
    }

    public int GetIndex(IThing t) {
        for (int i = 0; i < size(); i++) {
            IThing c = get(i);
            if (t.getId().equals(c.getId()) && t.getName().equals(c.getName()) && t.getSource().equals(c.getSource()))
                return i;
        }

        return -1;
    }

}
