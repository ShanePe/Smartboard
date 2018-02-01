package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import shane.pennihome.local.smartboard.comms.Messages.SwitchStateChangedMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;

import static shane.pennihome.local.smartboard.thingsframework.interfaces.IThing.Types.values;

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public abstract class IThing extends IDatabaseObject {
    /**
     * Created by shane on 29/12/17.
     */

    public enum Types {Switch, Routine}

    @SuppressWarnings("FieldCanBeLocal")
    private final String mInstance;
    //private transient OnSwitchActionListener mOnThingListener;
    private String mId;
    private IService.ServicesTypes mServicesTypes;
    @IgnoreOnCopy
    private IBlock mBlock;
    @IgnoreOnCopy
    private transient boolean mUnreachable;
    @IgnoreOnCopy
    private transient BroadcastReceiver mBroadcastReceiver;

    public IThing() {
        mInstance = this.getClass().getSimpleName();

        if(this instanceof IMessageSource) {
            final IThing me = this;

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    IMessage<?> message = IMessage.fromJson(IMessage.class, intent.getStringExtra("message"));
                    if (message.getSource() != null)
                        if (message.getSource().getClass().isInstance(me))
                            messageReceived(message);
                }
            };

            LocalBroadcastManager.getInstance(Globals.getContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(new SwitchStateChangedMessage().getMessageType()));
        }
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isUnreachable() {
        return mUnreachable;
    }

    public void setUnreachable(boolean unreachable) {
        mUnreachable = unreachable;
    }

    public abstract String getFriendlyName();

    public abstract Things getFilteredView(Things source);

    public abstract Class getBlockType();

    public abstract Types getThingType();

    public abstract IThingUIHandler getUIHandler();

    protected abstract void successfulToggle(@SuppressWarnings("unused") IThing thing);

    public abstract int getDefaultIconResource();

    public abstract void messageReceived(IMessage<?> message);

    public IService.ServicesTypes getService() {
        return mServicesTypes;
    }

    public void setService(IService.ServicesTypes servicesTypes) {
        this.mServicesTypes = servicesTypes;
    }

    public String getId() {
        return mId == null ? "" : mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

//    public void Toggle() {
//        final IThing me = this;
//        ThingToggler thingToggler = new ThingToggler(this, new OnProcessCompleteListener<ThingToggler>() {
//            @Override
//            public void complete(boolean success, ThingToggler source) {
//                if (success) {
//                    successfulToggle(me);
//                    if (mOnThingListener != null)
//                        mOnThingListener.Toggled();
//                }
//            }
//        });
//        thingToggler.execute();
//    }

    public String toJson() {
        return JsonBuilder.Get().toJson(this);
    }

    public void CreateBlock() throws Exception {
        mBlock = (IBlock) getBlockType().newInstance();
    }

    public void setBlockDefaults(Group group) {
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

    public <E extends IBlock> E getBlock(Class<E> cls) {
        //noinspection unchecked
        return (E) getBlock();
    }

    public IBlock getBlock() {
        if (mBlock == null)
            try {
                CreateBlock();
            } catch (Exception e) {
                Log.e("Smartboard", "Error creating block " + e.getMessage());
            }

        return mBlock;
    }

    public void setBlock(IBlock block) {
        mBlock = block;
    }

    String getKey() {
        return String.format("%s%s%s%s", getId(), getName(), getService(), getThingType());
    }

//    protected OnSwitchActionListener getOnThingListener() {
//        return mOnThingListener;
//    }
//
//    public void setOnThingListener(OnSwitchActionListener OnSwitchActionListener) {
//        mOnThingListener = OnSwitchActionListener;
//    }

    public IThing newInstanceFrom(IThing thing) {
        thing.setBlock(getBlock());
        return thing;
    }


    @Override
    protected void finalize() throws Throwable {
        if(mBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(Globals.getContext()).unregisterReceiver(mBroadcastReceiver);
        super.finalize();
    }
}
