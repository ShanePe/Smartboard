package shane.pennihome.local.smartboard.thingsframework.interfaces;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;

/**
 * Created by shane on 11/02/18.
 */

public abstract class IExecutor<T> {
    private T mValue;

    public String getId() {
        return "";
    }

    public T getValue() {
        return mValue;
    }

    public void setValue(T value) {
        mValue = value;
    }

    protected abstract JsonExecutorResult execute(IThing thing);

    public boolean delayVerification(){
        return false;
    }

    public boolean doVerification(IThing thing){
        return true;
    }
}
