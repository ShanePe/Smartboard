package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Device extends Thing{
    private boolean mOn;
    private String mType;

    public boolean getOn() {
        return mOn;
    }
    public void setOn(boolean _on) {
        this.mOn = _on;
    }

    public String getType() {
        return mType;
    }
    public void setType(String _type) {
        this.mType = _type;
    }

    @Override
    public void successfulToggle(Thing thing) {
        setOn(!getOn());
    }
}
