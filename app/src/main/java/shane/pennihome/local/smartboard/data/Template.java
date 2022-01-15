package shane.pennihome.local.smartboard.data;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 09/02/2018.
 */

public class Template extends IDatabaseObject{

    private final String mInstance;
    private IBlock mBlock;
    private IThing.Types mThingType;

    public IBlock getBlock() {
        return mBlock;
    }
    public <E extends IBlock> E getBlock(Class<E> cls) {
        //noinspection unchecked
        return (E) getBlock();
    }
    public void setBlock(IBlock block) {
        this.mBlock = block;
    }

    public IThing.Types getThingType() {
        return mThingType;
    }

    private void setThingType(IThing.Types thingtype) {
        this.mThingType = thingtype;
    }

    public Template() {
        mInstance = this.getClass().getSimpleName();
    }

    public Template(IBlock block)
    {
        mInstance = this.getClass().getSimpleName();
        setBlock(block);
        setThingType(block.getThingType());
        setName(block.getName());
    }

    @Override
    public Types getDatabaseType() {
        return Types.Template;
    }
}
