package shane.pennihome.local.smartboard.things.time;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 17/02/18.
 */

public class Time extends IThing {
    @IgnoreOnCopy
    private Thread mTimeThread;
    @IgnoreOnCopy
    private TickHandler mTickHandler;
    private boolean mLoop = true;

    public static Time Load(String json) {
        try {
            return IThing.Load(Time.class, json);
        } catch (Exception e) {
            return new Time();
        }
    }

    public TickHandler getTickHandler() {
        return mTickHandler;
    }

    public void setTickHandler(TickHandler tickHandler) {
        this.mTickHandler = tickHandler;
    }

    @Override
    public void verifyState(IThing compare) {
    }

    @Override
    public boolean isStateful() {
        return false;
    }

    @Override
    public Types getThingType() {
        return Types.Time;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }

    @Override
    public String getName() {
        return "Time";
    }

    public void start(TickHandler tickHandler) {
        if (mTimeThread != null) {
            mTimeThread.interrupt();
            mTimeThread = null;
        }

        setTickHandler(tickHandler);
        mTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mLoop) {
                    try {
                        int curSec = Calendar.getInstance().get(Calendar.SECOND);
                        int laps = 60 - curSec;
                        Log.i(Globals.ACTIVITY, "Timer Thread Sleeping for " + laps);
                        //noinspection BusyWait
                        Thread.sleep(laps * 1000L);
                        if (getTickHandler() != null)
                            getTickHandler().OnTick(Calendar.getInstance().getTime());
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        });

        mTimeThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
        super.finalize();
    }

    @Override
    public void clear() {
        mLoop = false;
        if (mTimeThread != null) {
            mTimeThread.interrupt();
            mTimeThread = null;
        }
    }

    public interface TickHandler {
        void OnTick(Date date);
    }
}
