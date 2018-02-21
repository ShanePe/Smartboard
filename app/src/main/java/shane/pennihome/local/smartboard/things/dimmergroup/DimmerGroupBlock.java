package shane.pennihome.local.smartboard.things.dimmergroup;

import android.view.View;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroupBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DimmerGroupBlock extends SwitchBlock implements IGroupBlock {
    @IgnoreOnCopy
    private ArrayList<String> mThingKeys;

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

    public void loadChildThings() {
        if (getThing() == null)
            return;

        DimmerGroup group = (DimmerGroup) getThing();
        for (String key : getThingKeys())
            group.getThings().add(Monitor.getMonitor().getThings().getByKey(key));
    }


    @Override
    public void execute(View indicator, OnProcessCompleteListener<String> onProcessCompleteListener) {
        try {
            boolean isOn = getThing(DimmerGroup.class).isOn();
            for (Switch s : getThing(DimmerGroup.class).getThings().cast(Switch.class))
                if (s.isOn() == isOn)
                    s.execute();

            if (getOnThingActionListener() != null)
                getOnThingActionListener().OnStateChanged(getThing());

            if (onProcessCompleteListener != null)
                onProcessCompleteListener.complete(true, null);
        } catch (Exception ex) {
            if (onProcessCompleteListener != null)
                onProcessCompleteListener.complete(false, ex.getMessage());
        }
    }

    public <T> void execute(View indicator, @SuppressWarnings("SameParameterValue") String executorName, T value, OnProcessCompleteListener<String> onProcessCompleteListener) {
        try {
            for (Switch s : getThing(DimmerGroup.class).getThings().cast(Switch.class)) {
                @SuppressWarnings("unchecked") IExecutor<T> executor = (IExecutor<T>) s.getExecutor(executorName);
                if (executor != null) {
                    executor.setValue(value);
                    s.execute(executor);
                }
            }
            if (getOnThingActionListener() != null)
                getOnThingActionListener().OnDimmerLevelChanged(getThing());

            if (onProcessCompleteListener != null)
                onProcessCompleteListener.complete(true, null);
        } catch (Exception ex) {
            if (onProcessCompleteListener != null)
                onProcessCompleteListener.complete(false, ex.getMessage());
        }
    }
}
