package shane.pennihome.local.smartboard.comms.Messages;

import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;

/**
 * Created by SPennicott on 01/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchStateChangedMessage extends IMessage<SwitchStateChangedMessage.SwitchStates> {
    public SwitchStateChangedMessage() {
        super();
    }

    public SwitchStateChangedMessage(IMessageSource source, SwitchStates value) {
        super(source, value);
    }

    public static SwitchStateChangedMessage Load(String json) {
        try {
            return IMessage.fromJson(SwitchStateChangedMessage.class, json);
        }catch (Exception ignored)
        {
            return new SwitchStateChangedMessage();
        }
    }

    public enum SwitchStates {Off, On, Unreachable}
}
