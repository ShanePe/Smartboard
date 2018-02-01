package shane.pennihome.local.smartboard.things.switches;

import android.graphics.Color;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThingUIHandler;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings({"ALL", "unused"})
public class Switch extends IThing {
    private String mType;
    private boolean mOn;

    public static Switch Load(String json) {
        try {
            return IThing.Load(Switch.class, json);
        } catch (Exception e) {
            return new Switch();
        }
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        boolean preOn = mOn;
        mOn = on;

        if (preOn != mOn)
            if (getOnThingListener() != null)
                getOnThingListener().StateChanged();
    }

    public String getType() {
        return mType;
    }

    public void setType(String _type) {
        this.mType = _type;
    }

    @Override
    public void setBlockDefaults(Group group) {
        super.setBlockDefaults(group);

        SwitchBlock block = (SwitchBlock)getBlock();

        block.setBackgroundColourTransparencyOn(100);
        block.setBackgroundColourOn(group.getDefaultBlockBackgroundColourOn() != 0 ?
                group.getDefaultBlockBackgroundColourOn() :
                Color.parseColor("#FF4081"));

        block.setForegroundColourOn(group.getDefaultBlockForeColourOn() != 0 ?
                group.getDefaultBlockForeColourOn() :
                Color.parseColor("black"));
    }

    @Override
    public void successfulToggle(IThing thing) {
        setOn(!isOn());
    }

    @Override
    public String getFriendlyName() {
        return "Switch";
    }

    @Override
    public Things getFilteredView(Things source) {
        Things things = new Things();
        things.addAll(source.getOfType(Switch.class));
        things.sort();
        return things;
    }

    @Override
    public  Class getBlockType() {
        return SwitchBlock.class;
    }

    @Override
    public Types getThingType() {
        return Types.Switch;
    }

    @Override
    public IThingUIHandler getUIHandler() {
        return new SwitchThingHandler(this);
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_switch;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
