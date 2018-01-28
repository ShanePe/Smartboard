package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.Color;
import android.util.Log;

import shane.pennihome.local.smartboard.comms.ThingToggler;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.listeners.onThingListener;

import static shane.pennihome.local.smartboard.thingsframework.interfaces.IThing.Types.values;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing extends IDatabaseObject {
    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    private transient onThingListener mOnThingListener;
    private String mId;
    private Sources mSources;
    @IgnoreOnCopy
    private IBlock mBlock;
    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    private static <V extends IThing> V fromJson(Class<V> cls, String json) {
        return JsonBuilder.Get().fromJson(json, cls);
    }

    public static int GetTypeID(IThing thing) {
        return thing.getThingType().ordinal();
    }

    public static IThing CreateByTypeID(int i) throws Exception {
        IThing.Types enumVal = values()[i];
        switch (enumVal) {
            case Switch:
                return new Switch();
            case Routine:
                return new Routine();
            default:
                throw new Exception("Invalid Type to create");
        }
    }

    public abstract String getFriendlyName();

    public abstract Things getFilteredView(Things source);

    public abstract Class getBlockType();

    public abstract Types getThingType();

    public abstract IThingUIHandler getUIHandler();

    protected abstract void successfulToggle(@SuppressWarnings("unused") IThing thing);

    public abstract int getDefaultIconResource();

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

        getBlock().setBackgroundColour(group.getDefaultBlockBackgroundColourOff() != 0 ?
                group.getDefaultBlockBackgroundColourOff() :
                Color.parseColor("#ff5a595b"));

        getBlock().setForegroundColour(group.getDefaultBlockForeColourOff() != 0 ?
                group.getDefaultBlockForeColourOff() :
                Color.WHITE);

        getBlock().setBackgroundColourTransparency(100);
        getBlock().setBackgroundImage(null);
        getBlock().setBackgroundImageTransparency(100);

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

    String getKey()
    {
        return String.format("%s%s%s%s", getId(), getName(), getSource(), getThingType());
    }

    protected onThingListener getOnThingListener()
    {
        return mOnThingListener;
    }

    public void setOnThingListener(onThingListener onThingListener) {
        mOnThingListener = onThingListener;
    }

    public IThing newInstanceFrom(IThing thing)
    {
        thing.setBlock(getBlock());
        return thing;
    }

    public enum Sources {SmartThings, PhilipsHue}

    public enum Types {Switch, Routine}
}
