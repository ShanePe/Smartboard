package shane.pennihome.local.smartboard.thingsframework.listeners;

/**
 * Created by SPennicott on 05/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnThingActionListener {
    void OnReachableStateChanged(boolean isUnReachable);

    void OnStateChanged();
}
