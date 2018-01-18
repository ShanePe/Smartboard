package shane.pennihome.local.smartboard.things.routines.block;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;

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
}
