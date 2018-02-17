package shane.pennihome.local.smartboard.things.time;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 17/02/18.
 */

@SuppressWarnings("ALL")
public class TimeBlock extends IBlock {
    public static TimeBlock Load(String json) {
        try {
            return IDatabaseObject.Load(TimeBlock.class, json);
        } catch (Exception e) {
            return new TimeBlock();
        }
    }

    @Override
    public int getDefaultIconResource() {
        return R.mipmap.icon_def_time_mm_fg;
    }

    @Override
    public String getFriendlyName() {
        return "Time";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.Time;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new TimeUIHandler(this);
    }

    @Override
    public boolean IsServiceLess() {
        return true;
    }
}
