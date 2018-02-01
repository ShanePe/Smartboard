package shane.pennihome.local.smartboard.things.switches;

/**
 * Created by shane on 01/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnSwitchStateChangeListener {
    void OnStateChange(boolean isOn, boolean isUnreachable);
}
