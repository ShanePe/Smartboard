package shane.pennihome.local.smartboard.things.routines;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineBlock extends IBlock {
    private String mIcon;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public static RoutineBlock Load(String json) {
        try {
            return IDatabaseObject.Load(RoutineBlock.class, json);
        } catch (Exception e) {
            return new RoutineBlock();
        }
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_routine;
    }

    @Override
    public String getFriendlyName() {
        return "Routine";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.Routine;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new RoutineBlockHandler(this);
    }
}
