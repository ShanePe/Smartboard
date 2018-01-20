package shane.pennihome.local.smartboard.data.interfaces;

import android.text.TextUtils;

import java.util.UUID;

import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.thingsframework.interfaces.Annotations;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IDatabaseObject {
    @Annotations.IgnoreOnCopy
    private String mDataId;
    private String mName = "";
    @Annotations.IgnoreOnCopy
    private long mPosition = 0;

    private static <V extends IDatabaseObject> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
    }

    public static <V extends IDatabaseObject> V Load(Class<V> cls, String objJson) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);

        return inst;
    }

    public String getDataID() {
        if(TextUtils.isEmpty(mDataId))
            mDataId = UUID.randomUUID().toString();
        return mDataId;
    }

    public long getPosition()
    {
        if(mPosition==0)
            mPosition = Globals.GetNextLongId();
        return mPosition;
    }

    public void setPosition(long position)
    {
        mPosition = position;
    }

    public String getName() {
        return mName == null ? "" : mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @SuppressWarnings("SameReturnValue")
    public abstract Types getDatabaseType();

    public String toJson() {
        return JsonBuilder.Get().toJson(this);
    }

    public long getIdAsLong() {
        UUID uid = UUID.fromString(getDataID());
        return uid.getMostSignificantBits();
    }

    public enum Types {Dashboard, Group, Thing, Block}
}
