package shane.pennihome.local.smartboard.things;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.blocks.interfaces.IBlocks;
import shane.pennihome.local.smartboard.blocks.switchblock.SwitchBlock;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.Interface.IThingCollection;
import shane.pennihome.local.smartboard.things.Routine.Routine;
import shane.pennihome.local.smartboard.things.Switch.Switch;

/**
 * Created by shane on 16/01/18.
 */

public class Things extends IThingCollection<IThing> {
    @Override
    public int SortCompare(IThing a, IThing b) {
        return a.getId().compareTo(b.getId());
    }

    public <E extends IThing, F extends ArrayList<E>> F getOfType(Class<E> cls) {
        F ret = (F) new ArrayList<E>();
        for (IThing t : this)
            if (t.getClass() == cls)
                ret.add((E) t);
        return (F) ret;

    }

    public static Things getAvailableTypes()
    {
        Things things = new Things();
        things.add(new Switch());
        things.add(new Routine());
        return things;
    }
}
