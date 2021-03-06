package shane.pennihome.local.smartboard.thingsframework.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public abstract class IThings<T extends IThing> extends ArrayList<T> {
    public void remove(IService service) {
        Things remove = new Things();
        for (T t : this)
            if (t.getServiceType() == service.getServiceType())
                remove.add(t);

        this.removeAll(remove);
    }

    public <T extends IThing> boolean containsType(Class<T> cls) {
        for (IThing t : this)
            if (t.getClass() == cls)
                return true;
        return false;
    }

    public <E extends IThing> void remove(Class<E> cls) {
        for (T t : this)
            if (t.getClass().equals(cls))
                remove(t);
    }

    public T getbyId(String Id) {
        for (T t : this)
            if (t.getId().equals(Id))
                return t;

        return null;
    }

    public T getByKey(String key) {
        for (T t : this)
            if (t.getKey().equals(key))
                return t;
        return null;
    }

    public void sort() {
        Collections.sort(this, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public int getIndex(IThing t) {
        for (int i = 0; i < size(); i++) {
            if (get(i).getKey().equals(t.getKey()))
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

    public void replaceAt(int index, T thing) {
        this.remove(index);
        this.add(index, thing);
    }

    public Things toThings() {
        return new Things(this);
    }
}
