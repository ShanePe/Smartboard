package shane.pennihome.local.smartboard.things.stmodes;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 11/02/18.
 */

public class SmartThingModeBlock extends IIconBlock {
    public static SmartThingModeBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SmartThingModeBlock.class, json);
        } catch (Exception e) {
            return new SmartThingModeBlock();
        }
    }

    @Override
    public int getDefaultIconResource() {
        return R.mipmap.icon_def_stmode_mm_fg;
    }

    @Override
    public String getFriendlyName() {
        return "SmartThings Mode";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.SmartThingMode;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new SmartThingModeUIHandler(this);
    }

    @Override
    protected int getIconColour() {
        return getForegroundColour();
    }
}
