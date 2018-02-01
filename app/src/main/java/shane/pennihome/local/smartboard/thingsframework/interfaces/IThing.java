package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.Messages.SwitchStateChangedMessage;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.IMessageSource;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
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
    protected transient boolean mUnreachable;
    @IgnoreOnCopy
    private transient BroadcastReceiver mBroadcastReceiver;

    public IThing() {
        mInstance = this.getClass().getSimpleName();
    }

    public void initialise()
    {
        if(this instanceof IMessageSource) {
            final IThing me = this;

            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    IMessage<?> message = IMessage.fromJson(IMessage.class, intent.getStringExtra("message"));
                    if (message.getSource() != null)
                        if (message.getSource().getClass().isInstance(me) && me.getKey().equals(message.getSource().getKey()) && !me.equals(message.getSource()))
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
        boolean pre = mUnreachable;
        mUnreachable = unreachable;
        if (this instanceof IMessageSource && pre != mUnreachable)
            Broadcaster.broadcastMessage(new SwitchStateChangedMessage((IMessageSource) this, SwitchStateChangedMessage.SwitchStates.Unreachable));
    }

    public abstract String getFriendlyName();

    public abstract Things getFilteredView(Things source);

    public abstract Class getBlockType();

    public abstract Types getThingType();

    public abstract IThingUIHandler getUIHandler();

    public abstract int getDefaultIconResource();

    public abstract void messageReceived(IMessage<?> message);

    public ExecutorResult execute()
    {
        IService service = Monitor.getMonitor().getServices().getByType(getServiceType());
        if(service!=null)
            return service.executeThing(this);
        return null;
    }

    public IService.ServicesTypes getServiceType() {
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

    public String getKey() {
        return String.format("%s%s%s%s", getId(), getName(), getServiceType(), getThingType());
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
