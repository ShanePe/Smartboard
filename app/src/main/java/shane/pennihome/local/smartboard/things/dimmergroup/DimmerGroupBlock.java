package shane.pennihome.local.smartboard.things.dimmergroup;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class DimmerGroupBlock extends SwitchBlock {
    @IgnoreOnCopy
    private ArrayList<String> mThingKeys;
    @IgnoreOnCopy
    private transient Things mThings;

    public static DimmerGroupBlock Load(String json) {
        try {
            return IDatabaseObject.Load(DimmerGroupBlock.class, json);
        } catch (Exception e) {
            return new DimmerGroupBlock();
        }
    }

    public ArrayList<String> getThingKeys() {
        if (mThingKeys == null)
            mThingKeys = new ArrayList<>();
        return mThingKeys;
    }

    public void setThingKeys(ArrayList<String> thingKeys) {
        mThingKeys = thingKeys;
    }

    public Things getThings() {
        if (mThings == null)
            mThings = new Things();

        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
    }

    @Override
    public int getDefaultIconResource() {
        return R.mipmap.icon_def_dimgroup_mm_fg;
    }

    @Override
    public String getFriendlyName() {
        return "Dimmer Group";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.DimmerGroup;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new DimmerGroupUIHandler(this);
    }

    @Override
    public boolean IsServiceLess() {
        return true;
    }
}
