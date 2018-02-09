package shane.pennihome.local.smartboard.data;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;

/**
 * Created by SPennicott on 09/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Template extends IDatabaseObject{

    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    private IBlock mBlock;

    public IBlock getBlock() {
        return mBlock;
    }

    public void setBlock(IBlock block) {
        this.mBlock = block;
    }

    public Template() {
        mInstance = this.getClass().getSimpleName();
    }

    @Override
    public Types getDatabaseType() {
        return Types.Template;
    }
}
