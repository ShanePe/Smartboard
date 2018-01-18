package shane.pennihome.local.smartboard.thingsframework.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shane on 16/01/18.
 */

public abstract class IThings<T extends IThing> extends ArrayList<T> {
    public void remove(IThing.Sources sources) {
        for (T t : this)
            if (t.getSource() == sources)
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
            if(get(i).getKey().equals(t.getKey()))
              return i;
        }

        return -1;
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final T item = this.remove(fromPosition);
        this.add(toPosition, item);
    }

    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(this, toPosition, fromPosition);
    }

    public void replaceAt(int index, T thing)
    {
        this.remove(index);
        this.add(index, thing);
    }
}
