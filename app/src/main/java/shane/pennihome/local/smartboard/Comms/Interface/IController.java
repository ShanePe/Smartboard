package shane.pennihome.local.smartboard.Comms.Interface;

import android.app.Activity;

import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Routines;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings({"ALL", "ConstantConditions"})
public abstract class IController<T> {
    protected final Activity mActivity;
    private Devices mDevices;
    private Routines mRoutines;

    protected IController(Activity activity) {
        this.mActivity = activity;
    }

    protected abstract void Connect(OnProcessCompleteListener<T> processCompleteListener);

    public abstract void getDevices(OnProcessCompleteListener<Devices> processCompleteListener);

    protected abstract void getRoutines(OnProcessCompleteListener<Routines> processCompleteListener);

    public Devices Devices() {
        return mDevices;
    }

    public Routines Routine() {
        return mRoutines;
    }

    public void getAll(final OnProcessCompleteListener<T> onProcessCompleteListener) {
        mDevices = new Devices();
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
        getDevices(new OnProcessCompleteListener<Devices>() {
            @Override
            public void complete(boolean success, Devices source) {
                if (success) {
                    mDevices = source;
                    getRoutines(new OnProcessCompleteListener<Routines>() {
                        @Override
                        public void complete(boolean success, Routines source) {
                            if (success) {
                                mRoutines = source;
                                if (onProcessCompleteListener != null)
                                    //noinspection ConstantConditions
                                    onProcessCompleteListener.complete(success, (T) me);
                            }
                        }
                    });
                }
            }
        });
    }
}
