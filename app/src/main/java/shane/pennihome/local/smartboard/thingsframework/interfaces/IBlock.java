package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
@IDatabaseObject.IgnoreOnCopy
public abstract class IBlock extends IDatabaseObject {
    private int mHeight;
    private int mWidth;
    private @ColorInt
    int mForeColour;
    private @ColorInt
    int mBackColour;
    private String mInstance;
    private int mBackTrans;
    private String mBackImage;
    private int mBGImgTrans;
    private String mThingKey;
    private transient IThing mThing;
    private UIHelper.ImageRenderTypes mBGImageRenderType;
    private OnThingActionListener mOnThingActionListener;
    private BroadcastReceiver mBroadcastReceiver;

    public IBlock() {
        mInstance = this.getClass().getSimpleName();
    }

    public static int GetTypeID(IBlock block) {
        return block.getThingType().ordinal();
    }

    public static IBlock CreateByTypeID(int i) throws Exception {
        IThing.Types enumVal = IThing.Types.values()[i];
        switch (enumVal) {
            case Switch:
                return new SwitchBlock();
            case Routine:
                return new RoutineBlock();
            default:
                throw new Exception("Invalid Type to create");
        }
    }

    public void startListeningForChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (mThing != null && mOnThingActionListener != null) {
                        IMessage<?> message = IMessage.fromIntent(intent);
                        if (message instanceof ThingChangedMessage) {
                            ThingChangedMessage thingChangedMessage = (ThingChangedMessage) message;
                            if (thingChangedMessage.getValue().equals(getThingKey())) {
                                if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.State)
                                    mOnThingActionListener.OnStateChanged();
                                else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Unreachable)
                                    mOnThingActionListener.OnReachableStateChanged(getThing().isUnreachable());
                            }
                        }
                    }
                }
            };
            LocalBroadcastManager.getInstance(Globals.getContext()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(new ThingChangedMessage().getMessageType()));
        }
    }

    public void stopListeningForChanges() {
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(Globals.getContext()).unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    public IThing getThing() {
        return mThing;
    }

    public void setThing(IThing thing) {
        mThing = thing;
    }

    public <T extends IThing> T getThing(Class<T> c) {
        return (T) mThing;
    }

    public String getThingKey() {
        return mThingKey;
    }

    public void setThingKey(String thingKey) {
        mThingKey = thingKey;
    }

    public abstract int getDefaultIconResource();

    public abstract String getFriendlyName();

    public OnThingActionListener getOnThingActionListener() {
        return mOnThingActionListener;
    }

    public void setOnThingActionListener(OnThingActionListener onThingActionListener) {
        mOnThingActionListener = onThingActionListener;
    }

    public void setBlockDefaults(Group group) {
        setWidth(1);
        setHeight(1);

        setBackgroundColour(group.getDefaultBlockBackgroundColourOff() != 0 ?
                group.getDefaultBlockBackgroundColourOff() :
                Color.parseColor("#ff5a595b"));

        setForegroundColour(group.getDefaultBlockForeColourOff() != 0 ?
                group.getDefaultBlockForeColourOff() :
                Color.WHITE);

        setBackgroundColourTransparency(100);
        setBackgroundImage(null);
        setBackgroundImageTransparency(100);

    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return Types.Block;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public @ColorInt
    int getForegroundColour() {
        return mForeColour;
    }

    public void setForegroundColour(@ColorInt int foreColour) {
        mForeColour = foreColour;
    }

    public @ColorInt
    int getBackgroundColour() {
        return mBackColour;
    }

    public void setBackgroundColour(@ColorInt int backgroundColour) {
        this.mBackColour = backgroundColour;
    }

    public int getBackgroundColourTransparency() {
        return mBackTrans;
    }

    public void setBackgroundColourTransparency(int backgroundTransparency) {
        this.mBackTrans = backgroundTransparency;
    }

    public UIHelper.ImageRenderTypes getBackgroundImageRenderType() {
        return mBGImageRenderType;
    }

    public void setBackgroundImageRenderType(UIHelper.ImageRenderTypes BGImageRenderType) {
        mBGImageRenderType = BGImageRenderType;
    }

    public String getBackgroundImage() {
        return mBackImage;
    }

    public void setBackgroundImage(String backImage) {
        this.mBackImage = backImage;
    }

    public int getBackgroundImageTransparency() {
        return mBGImgTrans;
    }

    public void setBackgroundImageTransparency(int bgImgTrans) {
        this.mBGImgTrans = bgImgTrans;
    }

    public abstract IThing.Types getThingType();

    public abstract IBlockUIHandler getUIHandler();

    @ColorInt
    public int getBackgroundColourWithAlpha()
    {
        return UIHelper.getColorWithAlpha(getBackgroundColour(), getBackgroundColourTransparency() / 100f);
    }

    public void renderForegroundColourToTextView(final TextView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                destination.setTextColor(getForegroundColour());
            }
        });
    }

    public void renderBackgroundTo(final View destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (!TextUtils.isEmpty(getBackgroundImage()))
                    bitmap = BitmapFactory.decodeFile(getBackgroundImage());

                final Drawable drawable = UIHelper.generateImage(
                        destination.getContext(),
                        getBackgroundColour(),
                        getBackgroundColourTransparency(),
                        bitmap,
                        getBackgroundImageTransparency(),
                        destination.getMeasuredWidth(),
                        destination.getMeasuredHeight(),
                        false,
                        getBackgroundImageRenderType());

               destination.setBackground(drawable);
               destination.invalidate();
            }
        });
    }

    public void renderUnreachableBackground(final View view) {
        if(getThing()!=null)
             if(!getThing().isUnreachable())
                 view.post(new Runnable() {
                     @Override
                     public void run() {
                         view.getBackground().setColorFilter(UIHelper.getDefaultForegroundColour(), PorterDuff.Mode.DARKEN);
                     }
                 });

    }

    public void loadThing() {
        if (TextUtils.isEmpty(getThingKey()) || mThing == null)
            mThing = Monitor.getMonitor().getThings().getByKey(getThingKey());
    }

    public void execute(View indicator, OnProcessCompleteListener<String> onProcessCompleteListener)
    {
        BlockExecutor blockExecutor = new BlockExecutor(indicator, onProcessCompleteListener);
        blockExecutor.execute(getThing());
    }

    @Override
    protected void finalize() throws Throwable {
        stopListeningForChanges();
        super.finalize();
    }

    private static class BlockExecutor extends AsyncTask<IThing, Void, JsonExecutorResult>
    {
        View mProgressIndicator;
        OnProcessCompleteListener<String> mOnProcessCompleteListener;

        public BlockExecutor(View mProgressIndicator, OnProcessCompleteListener<String> mOnProcessCompleteListener) {
            this.mProgressIndicator = mProgressIndicator;
            this.mOnProcessCompleteListener = mOnProcessCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            if(mProgressIndicator != null)
                mProgressIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(JsonExecutorResult jsonExecutorResult) {
            if(mProgressIndicator != null)
                mProgressIndicator.setVisibility(View.INVISIBLE);

            if(!jsonExecutorResult.isSuccess())
                if (mProgressIndicator != null)
                    Toast.makeText(mProgressIndicator.getContext(), "Error executing : " + jsonExecutorResult.getError().getMessage(), Toast.LENGTH_LONG).show();

            if(mOnProcessCompleteListener != null)
                mOnProcessCompleteListener.complete(jsonExecutorResult.isSuccess(), jsonExecutorResult.isSuccess() ?
                        jsonExecutorResult.getResult() :
                        jsonExecutorResult.getError().getMessage());
        }

        @Override
        protected JsonExecutorResult doInBackground(IThing... iThings) {
            return iThings[0].execute();
        }
    }
}
