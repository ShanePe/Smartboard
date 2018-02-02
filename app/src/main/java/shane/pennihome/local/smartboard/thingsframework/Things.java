package shane.pennihome.local.smartboard.thingsframework;

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
}
