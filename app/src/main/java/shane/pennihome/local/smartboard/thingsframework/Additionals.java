package shane.pennihome.local.smartboard.thingsframework;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IAdditional;

public class Additionals extends ArrayList<IAdditional> {
    public IAdditional getByKey(String key) {
        for (IAdditional t : this)
            if (t.getKey().equalsIgnoreCase(key))
                return t;

        return null;
    }

    public <T extends IAdditional> Iterable<T> cast(Class<T> cls) {
        final Additionals me = this;
        return new Iterable<T>() {
            @NonNull
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    final Iterator<IAdditional> iter = me.iterator();

                    @Override
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public T next() {
                        return (T) iter.next();
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
