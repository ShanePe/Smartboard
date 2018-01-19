package shane.pennihome.local.smartboard.comms.interfaces;

import android.app.Activity;

import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings({"ALL", "ConstantConditions"})
public abstract class IController<T> {
    protected final Activity mActivity;
    private Things mThings = new Things();
    protected IController(Activity activity) {
        this.mActivity = activity;
    }

    protected abstract void Connect(OnProcessCompleteListener<T> processCompleteListener);

    public abstract void getDevices(OnProcessCompleteListener<IThings<Switch>> processCompleteListener);

    protected abstract void getRoutines(OnProcessCompleteListener<IThings<Routine>> processCompleteListener);

    public <T extends IThing> IThings<T> get(Class<T> cls)
    {
        IThings<T> items = mThings.getOfType(cls);
        items.sort();
        return items;
    }

    public Things getThings()
    {
        return mThings;
    }

    public void getAll(final OnProcessCompleteListener<T> onProcessCompleteListener) {
        mThings = new Things();

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
        getDevices(new OnProcessCompleteListener<IThings<Switch>>() {
            @Override
            public void complete(boolean success, IThings<Switch> source) {
                mThings.remove(Switch.class);
                if(success)
                    mThings.addAll(source);

                getRoutines(new OnProcessCompleteListener<IThings<Routine>>() {
                    @Override
                    public void complete(boolean success, IThings<Routine> source) {
                        mThings.remove(Routine.class);
                        if(success)
                            mThings.addAll(source);

                        if (onProcessCompleteListener != null)
                            //noinspection ConstantConditions
                            onProcessCompleteListener.complete(success, (T) me);
                    }
                });
            }
        });
    }
}
