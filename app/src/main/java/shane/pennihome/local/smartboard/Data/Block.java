package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Block extends IDatabaseObject {
    @Override
    public Types getType() {
        return Types.Dashboard;
    }

    public static Block Load(String json)
    {
        try {
            return IDatabaseObject.Load(Block.class, json);
        } catch (Exception e) {
            return new Block();
        }
    }
}
