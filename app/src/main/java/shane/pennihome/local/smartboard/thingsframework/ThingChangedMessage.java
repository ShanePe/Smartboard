package shane.pennihome.local.smartboard.thingsframework;

import shane.pennihome.local.smartboard.comms.interfaces.IMessage;

/**
 * Created by shane on 08/02/18.
 */

public class ThingChangedMessage extends IMessage<String> {
    private What mWhatChanged = What.State;

    public ThingChangedMessage() {
    }

    public ThingChangedMessage(String key, What whatChanged) {
        super(key);
        setWhatChanged(whatChanged);
    }

    public static ThingChangedMessage Load(String json) {
        try {
            return IMessage.fromJson(ThingChangedMessage.class, json);
        } catch (Exception e) {
            return null;
        }
    }

    public What getWhatChanged() {
        return mWhatChanged;
    }

    private void setWhatChanged(What whatChanged) {
        mWhatChanged = whatChanged;
    }

    public enum What {State, Unreachable, Level, SupportColour, SupportColourChange, Disable, Enable}
}
