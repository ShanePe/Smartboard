package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Row extends IDatabaseObject {
    final List<Block> mBlocks = new ArrayList<>();

    public List<Block> getBlocks(){return mBlocks;}
    @Override
    public Types getType() {
        return Types.Dashboard;
    }

    public static Row Load(String json)
    {
        try {
            return IDatabaseObject.Load(Row.class, json);
        } catch (Exception e) {
            return new Row();
        }
    }
}
