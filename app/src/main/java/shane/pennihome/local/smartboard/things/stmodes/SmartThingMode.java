package shane.pennihome.local.smartboard.things.stmodes;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 11/02/18.
 */

public class SmartThingMode extends IThing {
    private ArrayList<String> mModes;
    private int mSelectedIndex = -1;

    public static SmartThingMode Load(String json) {
        try {
            return IThing.Load(SmartThingMode.class, json);
        } catch (Exception e) {
            return new SmartThingMode();
        }
    }

    ArrayList<String> getModes() {
        if (mModes == null)
            mModes = new ArrayList<>();
        return mModes;
    }

    public void setModes(ArrayList<String> modes) {
        mModes = modes;
    }

    int getSelectedIndex() {
        return mSelectedIndex;
    }

    private void setSelectedIndex(int selectedIndex, @SuppressWarnings("SameParameterValue") boolean fireBroadcast) {
        int pre = mSelectedIndex;
        mSelectedIndex = selectedIndex;
        if (pre != mSelectedIndex && fireBroadcast)
            Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.State));
    }

    String getSelectedText() {
        if (mSelectedIndex == -1)
            return "";
        else
            return getModes().get(getSelectedIndex());
    }

    private void setSelectedText(String mode) {
        setSelectedIndex(findIndex(mode), true);
    }

    public void addMode(String mode, boolean selected) {
        getModes().add(mode);
        if (selected)
            mSelectedIndex = getModes().size() - 1;
    }

    private int findIndex(String mode) {
        for (int i = 0; i < getModes().size(); i++)
            if (getModes().get(i).equalsIgnoreCase(mode))
                return i;
        return -1;
    }

    @Override
    public void verifyState(IThing compare) {
        SmartThingMode smartThingMode = (SmartThingMode) compare;
        if (!getSelectedText().equalsIgnoreCase(smartThingMode.getSelectedText()))
            setSelectedText(smartThingMode.getSelectedText());
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public Types getThingType() {
        return Types.SmartThingMode;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }

    @Override
    public JsonExecutorResult execute(@SuppressWarnings("rawtypes") IExecutor executor) {
        JsonExecutorResult result = super.execute(executor);
        if (result != null)
            if (result.isSuccess()) {
                //noinspection unchecked
                setSelectedText(((IExecutor<String>) executor).getValue());
            }
        return result;
    }
}
