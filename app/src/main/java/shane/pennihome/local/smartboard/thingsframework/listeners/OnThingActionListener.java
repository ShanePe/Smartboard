package shane.pennihome.local.smartboard.thingsframework.listeners;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 05/02/2018.
 */

public interface OnThingActionListener {
    void OnReachableStateChanged(IThing thing);

    void OnStateChanged(IThing thing);

    void OnDimmerLevelChanged(IThing thing);

    void OnSupportColourFlagChanged(IThing thing);

    void OnSupportColourChanged(IThing thing);

}
