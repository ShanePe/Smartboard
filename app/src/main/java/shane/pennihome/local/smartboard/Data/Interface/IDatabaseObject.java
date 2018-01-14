package shane.pennihome.local.smartboard.Data.Interface;

import com.google.gson.Gson;

import java.util.UUID;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IDatabaseObject {
    public enum Types{Dashboard,Row,Block}

    private final String mId = UUID.randomUUID().toString();
    private String mName = "";

    public String getID() {return mId;}

    public String getName(){return mName;}
    public void setName(String name){mName = name;}

    @SuppressWarnings("SameReturnValue")
    public abstract Types getType();

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private static <V extends IDatabaseObject> V fromJson(Class<V> cls,String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, cls);
    }

    public static <V extends IDatabaseObject> V Load(Class<V> cls,  String objJson ) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);

        return inst;
    }

    public long getIdAsLong()
    {
        UUID uid = UUID.fromString(getID());
        return uid.getMostSignificantBits();
    }
}
