package shane.pennihome.local.smartboard.thingsframework.interfaces;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;

/**
 * Created by shane on 11/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IExecutor<T> {
    private T mValue;

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }

    protected abstract JsonExecutorResult execute(IThing thing);
}
