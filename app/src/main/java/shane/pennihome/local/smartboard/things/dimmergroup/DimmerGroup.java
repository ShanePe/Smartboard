package shane.pennihome.local.smartboard.things.dimmergroup;

import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class DimmerGroup extends Switch {
    public static DimmerGroup Load(String json) {
        try {
            return IThing.Load(DimmerGroup.class, json);
        } catch (Exception e) {
            return new DimmerGroup();
        }
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.DimmerGroup;
    }

    @Override
    public void verifyState(IThing compare) {
//        super.verifyState(compare);
    }
}
