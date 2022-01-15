package shane.pennihome.local.smartboard.things.temperature;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 10/02/2018.
 */

public class TemperatureBlock extends IBlock {
    public static TemperatureBlock Load(String json) {
        try {
            return IDatabaseObject.Load(TemperatureBlock.class, json);
        } catch (Exception e) {
            return new TemperatureBlock();
        }
    }
    @Override
    public int getDefaultIconResource() {
        return R.mipmap.icon_def_temperature_mm_fg;
    }

    @Override
    public String getFriendlyName() {
        return "Temperature";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.Temperature;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new TemperatureUIHandler(this);
    }
}
