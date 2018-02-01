package shane.pennihome.local.smartboard.comms.interfaces;

import android.content.Intent;

import shane.pennihome.local.smartboard.data.JsonBuilder;

/**
 * Created by SPennicott on 01/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IMessage<T> {
    @SuppressWarnings("unused")
    private final String mInstance;
    private T mValue;
    private IMessageSource mSource;

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

    public static <T extends IMessage<?>> T fromJson(Class<T> cls, String json) {
        return JsonBuilder.get().fromJson(json, cls);
    }

    public static IMessage<?> fromIntent(Intent intent) {
        if (!intent.hasExtra("message"))
            return null;

        return fromJson(IMessage.class, intent.getStringExtra("message"));
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

    public String toJson() {
        return JsonBuilder.get().toJson(this);
    }
}
