package shane.pennihome.local.smartboard.things.routinegroup;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroupBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineGroupBlock extends RoutineBlock implements IGroupBlock {
    @IgnoreOnCopy
    private ArrayList<String> mThingKeys;

    public static RoutineGroupBlock Load(String json) {
        try {
            return IDatabaseObject.Load(RoutineGroupBlock.class, json);
        } catch (Exception e) {
            return new RoutineGroupBlock();
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
        return R.mipmap.icon_def_rtgroup_mm_fg;
    }

    @Override
    public String getFriendlyName() {
        return "Routine Group";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.RoutineGroup;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new RoutineGroupUIHandler(this);
    }

    @Override
    public boolean IsServiceLess() {
        return true;
    }

    public void loadChildThings() {
        if (getThing() == null)
            return;

        RoutineGroup group = (RoutineGroup) getThing();
        group.getChildThings().clear();
        for (String key : getThingKeys())
            group.getChildThings().add(Monitor.getMonitor().getThings().getByKey(key));
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void execute(final View indicator, final OnProcessCompleteListener<String> onProcessCompleteListener) {
        new AsyncTask<Things, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Things... things) {
                try {
                    for (Routine s : things[0].cast(Routine.class))
                        s.execute();
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (getOnThingActionListener() != null && aBoolean)
                    getOnThingActionListener().OnStateChanged(getThing());

                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(aBoolean, aBoolean ? "" : "Could not execute");
                indicator.setVisibility(View.GONE);

            }
        }.execute(getThing(RoutineGroup.class).getChildThings());

    }
}
