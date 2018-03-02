package shane.pennihome.local.smartboard.thingsframework.listeners;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 20/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnMultiThingActionListener {
    void onThingsSelected(IThing iThing);

    void onThingUnSelected(IThing iThing);
}
