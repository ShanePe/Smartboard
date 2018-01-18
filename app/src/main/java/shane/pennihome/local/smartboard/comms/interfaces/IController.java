package shane.pennihome.local.smartboard.comms.interfaces;

import android.app.Activity;

import shane.pennihome.local.smartboard.things.routines.Routines;
import shane.pennihome.local.smartboard.things.switches.Switches;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings({"ALL", "ConstantConditions"})
public abstract class IController<T> {
    protected final Activity mActivity;
    private Switches mSwitches;
    private Routines mRoutines;

    protected IController(Activity activity) {
        this.mActivity = activity;
    }

    protected abstract void Connect(OnProcessCompleteListener<T> processCompleteListener);

    public abstract void getDevices(OnProcessCompleteListener<Switches> processCompleteListener);

    protected abstract void getRoutines(OnProcessCompleteListener<Routines> processCompleteListener);

    public Switches Devices() {
        return mSwitches;
    }

    public Routines Routine() {
        return mRoutines;
    }

    public void getAll(final OnProcessCompleteListener<T> onProcessCompleteListener) {
        mSwitches = new Switches();
        mRoutines = new Routines();

        final IController<T> me = this;
        Connect(new OnProcessCompleteListener<T>() {
            @Override
            public void complete(boolean success, T source) {
                if (success && onProcessCompleteListener != null)
                    getThings(new OnProcessCompleteListener<T>() {
                        @Override
                        public void complete(boolean success, T source) {
                            //noinspection ConstantConditions
                            if (success && onProcessCompleteListener != null)
                                //noinspection ConstantConditions
                                onProcessCompleteListener.complete(success, (T) me);
                        }
                    });

            }
        });
    }

    public void getThings(final OnProcessCompleteListener<T> onProcessCompleteListener) {
        final IController<T> me = this;
        getDevices(new OnProcessCompleteListener<Switches>() {
            @Override
            public void complete(boolean success, Switches source) {
                mSwitches = success ? source : new Switches();

                getRoutines(new OnProcessCompleteListener<Routines>() {
                    @Override
                    public void complete(boolean success, Routines source) {
                        mRoutines = success ? source : new Routines();

                        if (onProcessCompleteListener != null)
                            //noinspection ConstantConditions
                            onProcessCompleteListener.complete(success, (T) me);
                    }
                });
            }
        });
    }
}
