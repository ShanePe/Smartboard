package shane.pennihome.local.smartboard.Data.Interface;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.ThingToggler;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class Thing {
    private String mId;
    private String mName;
    private Source mSource;
    private onThingListener mOnThingListener;

    public Thing.Source getSource() {
        return mSource;
    }

    public void setSource(Source source) {
        this.mSource = source;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    protected abstract void successfulToggle(@SuppressWarnings("unused") Thing thing);

    public void setOnThingToggledListener(onThingListener _onThingListener) {
        this.mOnThingListener = _onThingListener;
    }

    public void Toggle() {
        final Thing me = this;
        ThingToggler thingToggler = new ThingToggler(this, new OnProcessCompleteListener<ThingToggler>() {
            @Override
            public void complete(boolean success, ThingToggler source) {
                if (success) {
                    successfulToggle(me);
                    if (mOnThingListener != null)
                        mOnThingListener.Toggled();
                }
            }
        });
        thingToggler.execute();
    }

    public enum Source {SmartThings, PhilipsHue}
}
