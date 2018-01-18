package shane.pennihome.local.smartboard.blocks.routineblock;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.ui.interfaces.IBlockUI;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineBlock extends IBlock {
    public static RoutineBlock Load(String json) {
        try {
            return IDatabaseObject.Load(RoutineBlock.class, json);
        } catch (Exception e) {
            return new RoutineBlock();
        }
    }

    @Override
    public IBlockUI getUIHandler() {
        return new RoutineBlockHandler(this);
    }

    @Override
    public int getViewResourceID() {
        return R.layout.dashboard_block_routine;
    }

    @Override
    public int getEditorViewResourceID() {
        return R.layout.prop_block_routine;
    }

}
