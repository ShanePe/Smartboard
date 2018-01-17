package shane.pennihome.local.smartboard.things.Interface;

import shane.pennihome.local.smartboard.comms.ThingToggler;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.things.Listeners.onThingListener;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing {
    protected transient onThingListener mOnThingListener;
    private String mId;
    private String mName;
    private Source mSource;
    private String mInstance;

    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    private static <V extends IThing> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
    }

    public static <V extends IThing> V Load(Class<V> cls, String objJson) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);

        return inst;
    }

    public IThing.Source getSource() {
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

    protected abstract void successfulToggle(@SuppressWarnings("unused") IThing thing);

    public void setOnThingListener(onThingListener onThingListener) {
        mOnThingListener = onThingListener;
    }

    public void Toggle() {
        final IThing me = this;
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

    public String toJson() {
        return JsonBuilder.Get().toJson(this);
    }

    public enum Source {SmartThings, PhilipsHue}
}
