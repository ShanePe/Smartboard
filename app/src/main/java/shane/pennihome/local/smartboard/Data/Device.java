package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Comms.SmartThings.STDeviceToggler;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 28/12/17.
 */

public class Device extends Thing{
    private boolean _on;
    private String _type;

    public boolean getOn() {
        return _on;
    }
    public void setOn(boolean _on) {
        this._on = _on;
    }

    @Override
    public void Toggle(final ProcessCompleteListener processComplete) {
        final Device me = this;
        STDeviceToggler toggler = new STDeviceToggler(this, new ProcessCompleteListener<STDeviceToggler>() {
            @Override
            public void Complete(boolean success, STDeviceToggler source) {
                if (success)
                    me.setOn(!me.getOn());
                if (processComplete != null)
                    processComplete.Complete(success, me);
            }
        });
        toggler.execute();
    }

    public String getType() {
        return _type;
    }

    public void setType(String _type) {
        this._type = _type;
    }
}
