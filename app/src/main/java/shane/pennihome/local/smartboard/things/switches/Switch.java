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
    private States mState;
    private String mType;

    public static Switch Load(String json) {
        try {
            return IThing.Load(Switch.class, json);
        } catch (Exception e) {
            return new Switch();
        }
    }

    public States getState() {
        return mState;
    }

    public void setState(States state) {
        States pre = getState();
        this.mState = state;

        if (pre != state && getOnThingListener() != null)
            getOnThingListener().StateChanged();
    }

    public String getType() {
        return mType;
    }

    public void setType(String _type) {
        this.mType = _type;
    }

    public boolean isOn() {
        return getState() == States.On;
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
        if (getState() == States.Off)
            setState(States.On);
        else if (getState() == States.On)
            setState(States.Off);
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

    public enum States {Off, On, Unreachable}
}
