package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;
import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Block extends IDatabaseObject {
    private Thing mThing;
    @Override
    public Types getType() {
        return Types.Dashboard;
    }

    public static Block Load(String json)
    {
        try {
            return IDatabaseObject.Load(Block.class, json);
        } catch (Exception e) {
            return new Block();
        }
    }

    public Thing getThing() {
        return mThing;
    }

    public void setThing(Thing thing) {
        mThing = thing;
    }
}
