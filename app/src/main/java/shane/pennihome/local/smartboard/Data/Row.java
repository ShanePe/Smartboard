package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Row extends IDatabaseObject {
    private boolean mDisplayName = false;
    final List<Block> mBlocks = new ArrayList<>();
    private boolean mExpanded = false;

    public Row(){}
    public Row(String name){
        this.setName(name);
    }

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

    public boolean getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(boolean displayName) {
        this.mDisplayName = displayName;
    }

    public Block getBlockAt(int index)
    {
        return mBlocks.get(index);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }
}
