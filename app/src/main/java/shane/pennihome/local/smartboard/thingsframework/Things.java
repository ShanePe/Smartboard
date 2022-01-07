package shane.pennihome.local.smartboard.thingsframework;

import android.support.annotation.NonNull;

import java.util.Iterator;

import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public class Things extends IThings<IThing> {
    public Things getForBlock(IBlock block) {
        Things ret = new Things();
        for (IThing t : this)
            if (t.getThingType() == block.getThingType())
                ret.add(t);
        ret.sort();
        return ret;
    }

    public Things getForService(IService service) {
        Things ret = new Things();
        for (IThing t : this)
            if (t.getServiceType() == service.getServiceType())
                ret.add(t);

        return ret;
    }

    public <E extends IThing, F extends IThings<E>> F getOfType(Class<E> cls) {
        Things ret = new Things();
        for (IThing t : this)
            if (t.getClass() == cls)
                //noinspection unchecked
                ret.add(t);
        //noinspection unchecked
        return (F) ret;
    }

    public boolean hasThingWithKey(String key) {
        for (IThing t : this)
            if (t.getKey().equals(key))
                return true;
        return false;
    }

    public <T extends IThing> T get(Class<T> cls, int index) {
        return (T) get(index);
    }

    public <T extends IThing> Iterable<T> cast(Class<T> cls) {
        final Things me = this;
        return new Iterable<T>() {
            @NonNull
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Iterator<IThing> iter = me.iterator();

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @Override
                    public T next() {
                        return (T) iter.next();
                    }
                };
            }
        };
    }
}
