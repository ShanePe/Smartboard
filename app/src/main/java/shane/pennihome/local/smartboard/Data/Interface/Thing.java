package shane.pennihome.local.smartboard.Data.Interface;

import com.google.gson.Gson;

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
    private String mInstance;

    protected transient onThingListener mOnThingListener;

    public Thing() {
        mInstance = this.getClass().getSimpleName();
    }

    private static <V extends Thing> V fromJson(Class<V> cls, String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }

    public static <V extends Thing> V Load(Class<V> cls, String objJson) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);

        return inst;
    }

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

    public void setOnThingListener(onThingListener onThingListener) {
        mOnThingListener = onThingListener;
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

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public enum Source {SmartThings, PhilipsHue}
}
