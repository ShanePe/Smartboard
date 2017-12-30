package shane.pennihome.local.smartboard.Data.Interface;

import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.SmartThings.STThingToggler;

/**
 * Created by shane on 29/12/17.
 */

public abstract class Thing {
    public enum Source {SmartThings}

    private String mId;
    private String mName;
    private Source mSource;
    private onThingToggledListener mOnThingToggledListener;

    public Thing.Source getSource() { return mSource; }
    public void setSource(Source _source) { this.mSource = _source; }

    public String getName() {
        return mName;
    }
    public void setName(String _name) {
        this.mName = _name;
    }

    public String getId() {
        return mId;
    }
    public void setId(String _id) {
        this.mId = _id;
    }

    public abstract void successfulToggle(Thing thing);

    public void setOnThingToggledListener(onThingToggledListener _onThingToggledListener) {
        this.mOnThingToggledListener = _onThingToggledListener;
    }

    public void Toggle() {
        final Thing me = this;
        STThingToggler toggler = new STThingToggler(this, new ProcessCompleteListener<STThingToggler>() {
            @Override
            public void Complete(boolean success, STThingToggler source) {
                if(success) {
                    successfulToggle(me);
                    if (mOnThingToggledListener != null)
                        mOnThingToggledListener.Toggled();
                }
            }
        }, null);
        toggler.execute();
    }
}
