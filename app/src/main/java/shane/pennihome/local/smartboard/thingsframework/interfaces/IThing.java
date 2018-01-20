package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.Color;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import shane.pennihome.local.smartboard.comms.ThingToggler;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.listeners.onThingListener;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;

import static shane.pennihome.local.smartboard.thingsframework.interfaces.IThing.Types.*;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing extends IDatabaseObject {

    public enum Sources {SmartThings, PhilipsHue}
    public enum Types {Switch, Routine}

    private transient onThingListener mOnThingListener;
    private String mId;
    private Sources mSources;
    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    @Annotations.IgnoreOnCopy
    private
    IBlock mBlock;
    @Annotations.IgnoreOnCopy

    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    public abstract String getFriendlyName();
    public abstract Things getFilteredView(Things source);
    public abstract Class getBlockType();
    public abstract Types getThingType();
    public abstract IThingUIHandler getUIHandler();
    protected abstract void successfulToggle(@SuppressWarnings("unused") IThing thing);
    public abstract int getDefaultIconResource();

    private static <V extends IThing> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
    }

    public Sources getSource() {
        return mSources;
    }

    public void setSource(Sources sources) {
        this.mSources = sources;
    }

    public String getId() {
        return mId == null ? "" : mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

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

    public void CreateBlock() throws Exception{
        mBlock = (IBlock)getBlockType().newInstance();
    }

    public void setBlockDefaults(Group group)
    {
        getBlock().setWidth(1);
        getBlock().setHeight(1);
        getBlock().setBackgroundTransparency(100);

        getBlock().setBackgroundColour(group.getDefaultBlockBackgroundColourOff() != 0 ?
                group.getDefaultBlockBackgroundColourOff() :
                Color.parseColor("#ff5a595b"));

        getBlock().setForeColour(group.getDefaultBlockForeColourOff() != 0 ?
                group.getDefaultBlockForeColourOff() :
                Color.parseColor("white"));

    }

    public <E extends IBlock> E getBlock(Class<E> cls)
    {
        //noinspection unchecked
        return (E)getBlock();
    }

    public IBlock getBlock(){
        if(mBlock == null)
            try {
                CreateBlock();
            } catch (Exception e) {
                Log.e("Smartboard", "Error creating block " + e.getMessage());
            }

        return mBlock;
    }

    public void setBlock(IBlock block)
    {
        mBlock = block;
    }

    public static int GetTypeID(IThing thing) {
        return thing.getThingType().ordinal();
    }

    String getKey()
    {
        return String.format("%s%s%s%s", getId(), getName(), getSource(), getThingType());
    }

    public static IThing CreateByTypeID(int i) throws Exception {
        IThing.Types enumVal = values()[i];
        switch (enumVal)
        {
            case Switch:return new Switch();
            case Routine:return new Routine();
            default:throw new Exception("Invalid Type to create");
        }
    }

    protected onThingListener getOnThingListener()
    {
        return mOnThingListener;
    }

    public IThing newInstanceFrom(IThing thing)
    {
        thing.setBlock(getBlock());
        return thing;
    }

    public void copyValuesFrom(IThing from) {
        ArrayList<Field> fieldsTo = new ArrayList<>();
        ArrayList<Field> fieldsFrom = new ArrayList<>();
        fieldsTo = getAllFields(fieldsTo, this.getClass());
        fieldsFrom = getAllFields(fieldsFrom, from.getClass());

        for(int i =0;i<fieldsTo.size();i++){
            try {
                Field fieldTo = fieldsTo.get(i);
                Field fieldFrom = fieldsFrom.get(i);
                @SuppressWarnings("ReflectionForUnavailableAnnotation") Annotations.IgnoreOnCopy ignore = fieldFrom.getAnnotation(Annotations.IgnoreOnCopy.class);

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
}
