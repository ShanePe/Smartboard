package shane.pennihome.local.smartboard.data.interfaces;

import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class IDatabaseObject {
    @IgnoreOnCopy
    private String mDataId;
    private String mName = "";
    @IgnoreOnCopy
    private long mPosition = 0;

    private static <V extends IDatabaseObject> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.get().fromJson(json, cls);
    }

    public static <V extends IDatabaseObject> V Load(Class<V> cls, String objJson) throws IllegalAccessException, InstantiationException {
        V inst = cls.newInstance();
        if (!objJson.equals(""))
            inst = fromJson(cls, objJson);
        inst.initialise();
        return inst;
    }

    protected void initialise() {
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
        return JsonBuilder.get().toJson(this);
    }

    public void copyValuesFrom(IDatabaseObject from) {
        ArrayList<Field> fieldsTo = new ArrayList<>();
        ArrayList<Field> fieldsFrom = new ArrayList<>();
        fieldsTo = getAllFields(fieldsTo, this.getClass());
        fieldsFrom = getAllFields(fieldsFrom, from.getClass());

        for(int i =0;i<fieldsTo.size();i++){
            try {
                Field fieldTo = fieldsTo.get(i);
                Field fieldFrom = fieldsFrom.get(i);
                IgnoreOnCopy ignore = fieldFrom.getAnnotation(IgnoreOnCopy.class);
                if(ignore == null)
                    ignore = fieldTo.getAnnotation(IgnoreOnCopy.class);

                if(ignore == null && !Modifier.isTransient(fieldTo.getModifiers()) ) {
                    if(!fieldTo.isAccessible())
                        fieldTo.setAccessible(true);
                    if(!fieldFrom.isAccessible())
                        fieldFrom.setAccessible(true);

                    fieldTo.set(this, fieldFrom.get(from));
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<Field> getAllFields(ArrayList<Field> fields, Class<?> type) {
        if(fields==null)
            fields = new ArrayList<>();
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public enum Types {Dashboard, Group, Thing, Block, Service}

    @SuppressWarnings("WeakerAccess")
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IgnoreOnCopy {
    }
}
