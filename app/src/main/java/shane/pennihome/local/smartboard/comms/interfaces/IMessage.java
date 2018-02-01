package shane.pennihome.local.smartboard.comms.interfaces;

import android.app.Notification;

import org.json.JSONException;

import shane.pennihome.local.smartboard.data.JsonBuilder;

/**
 * Created by SPennicott on 01/02/2018.
 */

public abstract class IMessage<T> {
    protected T mValue;
    private IMessageSource mSource;
    private String mInstance;

    public IMessage() {
        mInstance = this.getClass().getSimpleName();
    }

    public IMessage(IMessageSource source) {
        this.mSource = source;
        mInstance = this.getClass().getSimpleName();
    }

    public IMessage(IMessageSource source, T value) {
        this.mValue = value;
        this.mSource = source;
        mInstance = this.getClass().getSimpleName();
    }

    public IMessageSource getSource() {
        return mSource;
    }

    public T getValue()
    {
        return mValue;
    }
    public void setValue(T value)
    {
        mValue = value;
    }

    public String getMessageType(){
        return this.getClass().getSimpleName();
    }

    public String toJson() throws JSONException {
          return new JsonBuilder().Get().toJson(this);
    }

    public static <T extends IMessage<?>> T  fromJson(Class<T> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
    }
}
