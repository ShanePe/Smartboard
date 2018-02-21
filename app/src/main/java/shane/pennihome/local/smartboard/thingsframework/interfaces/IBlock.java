package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.dimmergroup.DimmerGroupBlock;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingModeBlock;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.things.temperature.TemperatureBlock;
import shane.pennihome.local.smartboard.things.time.TimeBlock;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
@IDatabaseObject.IgnoreOnCopy
public abstract class IBlock extends IDatabaseObject implements Cloneable {
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
    private UIHelper.ImageRenderTypes mBGImageRenderType;
    @IgnoreOnCopy
    private String mThingKey;
    @IgnoreOnCopy
    private transient IThing mThing;
    @IgnoreOnCopy
    private OnThingActionListener mOnThingActionListener;
    @IgnoreOnCopy
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
            case Temperature:
                return new TemperatureBlock();
            case SmartThingMode:
                return new SmartThingModeBlock();
            case Time:
                return new TimeBlock();
            case DimmerGroup:
                return new DimmerGroupBlock();
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
                                    mOnThingActionListener.OnStateChanged(getThing());
                                else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Unreachable)
                                    mOnThingActionListener.OnReachableStateChanged(getThing());
                                else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Level)
                                    mOnThingActionListener.OnDimmerLevelChanged(getThing());
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
            destination.setBackground(getBackgroundDrawable(destination));
            destination.invalidate();
            }
        });
    }

    private Drawable getBackgroundDrawable(View destination)
    {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(getBackgroundImage()))
            bitmap = BitmapFactory.decodeFile(getBackgroundImage());

       return UIHelper.generateImage(
                destination.getContext(),
                getBackgroundColour(),
                getBackgroundColourTransparency(),
                bitmap,
                getBackgroundImageTransparency(),
                destination.getMeasuredWidth(),
                destination.getMeasuredHeight(),
                false,
                getBackgroundImageRenderType());
    }

    public void renderTemplateBlip(final ImageView destination)
    {
        destination.post(new Runnable() {
            @Override
            public void run() {
                destination.setImageDrawable(UIHelper.createColourBlocks(destination.getContext(),
                        new int[]{getBackgroundColourWithAlpha(), getForegroundColour()},20,20));
                destination.invalidate();
            }
        });
    }

    public void renderTemplateBackgroundTo(final View destination)
    {
        destination.post(new Runnable() {
            @Override
            public void run() {

                Drawable background = getBackgroundDrawable(destination);
                Bitmap bitmap = ((BitmapDrawable)background).getBitmap();
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(getForegroundColour());
                canvas.drawRect(new Rect(bitmap.getWidth()-20,bitmap.getHeight()-20,bitmap.getWidth()-10,bitmap.getHeight()-10 ), paint);
                destination.setBackground(new BitmapDrawable(destination.getResources(), bitmap));
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

    public boolean IsServiceLess() {
        return false;
    }

    public void loadThing() {
        if ((TextUtils.isEmpty(getThingKey()) || mThing == null) && !IsServiceLess())
            mThing = Monitor.getMonitor().getThings().getByKey(getThingKey());
        else if (mThing == null && IsServiceLess()) {
            try {
                mThing = IThing.CreateFromType(getThingType());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this instanceof IGroupBlock)
            ((IGroupBlock) this).loadChildThings();
    }

    public void execute(View indicator, OnProcessCompleteListener<String> onProcessCompleteListener) {
        execute(indicator, getExecutor(), onProcessCompleteListener);
    }

    public void execute(View indicator, IExecutor<?> executor, OnProcessCompleteListener<String> onProcessCompleteListener) {
        BlockExecutor blockExecutor = new BlockExecutor(indicator, executor, onProcessCompleteListener);
        blockExecutor.execute(getThing());
    }

    public IExecutor<?> getExecutor() {
        return getExecutor("");
    }

    public IExecutor<?> getExecutor(String id) {
        return getThing().getExecutor(id);
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
        IExecutor<?> mExecutor;

        public BlockExecutor(View progressindicator, IExecutor<?> executor, OnProcessCompleteListener<String> onProcessCompleteListener) {
            this.mProgressIndicator = progressindicator;
            this.mOnProcessCompleteListener = onProcessCompleteListener;
            this.mExecutor = executor;
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
            return iThings[0].execute(mExecutor);
        }
    }
}
