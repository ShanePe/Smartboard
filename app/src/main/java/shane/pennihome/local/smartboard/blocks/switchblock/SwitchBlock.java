package shane.pennihome.local.smartboard.blocks.switchblock;

import android.app.Activity;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.Switch.Switch;
import shane.pennihome.local.smartboard.things.Things;
import shane.pennihome.local.smartboard.ui.interfaces.IBlockUI;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchBlock extends IBlock {
    public static SwitchBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SwitchBlock.class, json);
        } catch (Exception e) {
            return new SwitchBlock();
        }
    }

    @Override
    public IBlockUI getUIHandler() {
        return new SwitchBlockHandler(this);
    }

    @Override
    public int getViewResourceID() {
        return R.layout.dashboard_block_switch;
    }

    @Override
    public int getEditorViewResourceID() {
        return R.layout.prop_block_switch;
    }

}
