package shane.pennihome.local.smartboard.things.Switch;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.blocks.switchblock.SwitchBlock;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.Interface.IThingCollection;
import shane.pennihome.local.smartboard.things.Things;

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

        if (pre != state && mOnThingListener != null)
            mOnThingListener.StateChanged();
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
        return things;
    }

    @Override
    public  Class getBlockType() {
        return SwitchBlock.class;
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_switch;
    }

    public enum States {Off, On, Unreachable}
}
