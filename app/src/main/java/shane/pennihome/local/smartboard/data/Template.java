package shane.pennihome.local.smartboard.data;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 09/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Template extends IDatabaseObject{

    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    private IBlock mBlock;
    private IThing.Types mThingType;

    public IBlock getBlock() {
        return mBlock;
    }

    public void setBlock(IBlock block) {
        this.mBlock = block;
    }

    public IThing.Types getThingType() {
        return mThingType;
    }

    public void setThingType(IThing.Types thingtype) {
        this.mThingType = thingtype;
    }

    public Template() {
        mInstance = this.getClass().getSimpleName();
    }



    @Override
    public Types getDatabaseType() {
        return Types.Template;
    }
}
