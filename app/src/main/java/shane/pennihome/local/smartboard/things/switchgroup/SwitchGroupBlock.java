package shane.pennihome.local.smartboard.things.switchgroup;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroupBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchGroupBlock extends SwitchBlock implements IGroupBlock {
    @IgnoreOnCopy
    private ArrayList<String> mThingKeys;

    public static SwitchGroupBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SwitchGroupBlock.class, json);
        } catch (Exception e) {
            return new SwitchGroupBlock();
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
        return "Switch Group";
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.SwitchGroup;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new SwitchGroupUIHandler(this);
    }

    @Override
    public boolean IsServiceLess() {
        return true;
    }

    @Override
    public void renderForegroundColourTo(final ProgressBar destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                boolean isOn = false;
                if (s != null)
                    isOn = s.isOn();
                if (destination.getProgressDrawable() != null)
                    destination.getProgressDrawable().setColorFilter(isOn ? getForegroundColourOn() : getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
                else if (destination.getIndeterminateDrawable() != null)
                    destination.getIndeterminateDrawable().setColorFilter(isOn ? getForegroundColourOn() : getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    @Override
    public void renderForegroundColourTo(final SeekBar destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                boolean isOn = false;
                if (s != null)
                    isOn = s.isOn();

                if (destination.getThumb() != null)
                    destination.getThumb().setColorFilter(isOn ? getForegroundColourOn() : getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
                if (destination.getProgressDrawable() != null)
                    destination.getProgressDrawable().setColorFilter(isOn ? getForegroundColourOn() : getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    @Override
    public void renderForegroundColourTo(final TextView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                boolean isOn = false;
                if (s != null)
                    isOn = s.isOn();
                destination.setTextColor(isOn ? getForegroundColourOn() : getForegroundColour());
            }
        });
    }

    public void loadChildThings() {
        if (getThing() == null)
            return;

        SwitchGroup group = (SwitchGroup) getThing();
        group.getChildThings().clear();
        for (String key : getThingKeys()) {
            IThing thing = Monitor.getMonitor().getThings().getByKey(key);
            if (thing != null)
                group.getChildThings().add(thing);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void execute(final View indicator, final OnProcessCompleteListener<String> onProcessCompleteListener) {
        new AsyncTask<Things, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Things... things) {
                try {
                    boolean isOn = getThing(SwitchGroup.class).isOn();
                    for (Switch s : things[0].cast(Switch.class))
                        if (s.isOn() == isOn)
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
        }.execute(getThing(SwitchGroup.class).getChildThings());
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void execute(final View indicator, boolean delay, final OnProcessCompleteListener<String> onProcessCompleteListener) {
        if (delay) {
            final OnProcessCompleteListener processCompleteListener = new OnProcessCompleteListener() {
                @Override
                public void complete(boolean success, Object source) {
                    execute(indicator, onProcessCompleteListener);
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(IBlock.EXECUTE_DELAY);
                        processCompleteListener.complete(true, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else
            execute(indicator, onProcessCompleteListener);
    }

    @SuppressLint("StaticFieldLeak")
    public <T> void execute(final View indicator, @SuppressWarnings("SameParameterValue") final String executorName, final T value, final OnProcessCompleteListener<String> onProcessCompleteListener) {
        new AsyncTask<Things, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Things... things) {
                try {
                    for (Switch s : things[0].cast(Switch.class)) {
                        @SuppressWarnings("unchecked") IExecutor<T> executor = (IExecutor<T>) s.getExecutor(executorName);
                        if (executor != null) {
                            executor.setValue(value);
                            s.execute(executor);
                        }
                    }
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (getOnThingActionListener() != null && aBoolean)
                    getOnThingActionListener().OnDimmerLevelChanged(getThing());

                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(aBoolean, aBoolean ? "" : "Failed tp execute");

                indicator.setVisibility(View.GONE);
            }
        }.execute(getThing(SwitchGroup.class).getChildThings());
    }

    @Override
    public void startListeningForChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ThingChangedMessage thingChangedMessage = getThingChangeMessage(intent);
                    if (thingChangedMessage != null)
                        for (IThing thing : getThing(SwitchGroup.class).getChildThings())
                            handleThingChangeMessage(thingChangedMessage, thing.getKey());
                }
            };
            LocalBroadcastManager.getInstance(Globals.getContext()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(new ThingChangedMessage().getMessageType()));
        }
    }
}
