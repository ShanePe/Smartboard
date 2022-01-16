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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.routinegroup.RoutineGroupBlock;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingModeBlock;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.things.switchgroup.SwitchGroupBlock;
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
    @IgnoreOnCopy
    public static final int EXECUTE_DELAY = 30;
    @IgnoreOnCopy
    protected BroadcastReceiver mBroadcastReceiver;
    int mBackImgPadding;
    boolean mHideTitle;
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
    private Thread mDelay;

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
            case SwitchGroup:
                return new SwitchGroupBlock();
            case RoutineGroup:
                return new RoutineGroupBlock();
            default:
                throw new Exception("Invalid Type to create");
        }
    }

    protected ThingChangedMessage getThingChangeMessage(Intent intent) {
        ThingChangedMessage thingChangedMessage = null;

        if (mThing != null && mOnThingActionListener != null) {
            IMessage<?> message = IMessage.fromIntent(intent);
            if (message instanceof ThingChangedMessage)
                thingChangedMessage = (ThingChangedMessage) message;
        }

        return thingChangedMessage;
    }

    protected boolean handleThingChangeMessage(ThingChangedMessage thingChangedMessage, String thingKey) {
        if (thingChangedMessage == null || TextUtils.isEmpty(thingKey))
            return false;

        if (thingChangedMessage.getValue().equals(thingKey) || "all".equals(thingChangedMessage.getValue())) {
            if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.State) {
                mOnThingActionListener.OnStateChanged(getThing());
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Unreachable) {
                mOnThingActionListener.OnReachableStateChanged(getThing());
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Level) {
                mOnThingActionListener.OnDimmerLevelChanged(getThing());
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.SupportColour) {
                mOnThingActionListener.OnSupportColourFlagChanged(getThing());
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.SupportColourChange) {
                mOnThingActionListener.OnSupportColourChanged(getThing());
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Disable) {
                mOnThingActionListener.OnDisabledChanged(getThing(), true);
                return true;
            } else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Enable) {
                mOnThingActionListener.OnDisabledChanged(getThing(), getThing().isUnreachable() || false);
                return true;
            }
        }
        return false;
    }

    public void startListeningForChanges() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    handleThingChangeMessage(getThingChangeMessage(intent), getThingKey());
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
        setBackgroundImagePadding(0);
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

    public int getBackgroundImagePadding() {
        return mBackImgPadding;
    }

    public void setBackgroundImagePadding(int mBackImgPadding) {
        this.mBackImgPadding = mBackImgPadding;
    }

    public boolean isHideTitle() {
        return mHideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.mHideTitle = hideTitle;
    }

    public abstract IThing.Types getThingType();

    public abstract IBlockUIHandler getUIHandler();

    @ColorInt
    public int getBackgroundColourWithAlpha() {
        return UIHelper.getColorWithAlpha(getBackgroundColour(), getBackgroundColourTransparency() / 100f);
    }

    public void renderForegroundColourTo(final TextView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                destination.setTextColor(getForegroundColour());
            }
        });
    }

    public void renderForegroundColourTo(final SeekBar destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                if (destination.getThumb() != null)
                    destination.getThumb().setColorFilter(getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    public void renderForegroundColourTo(final ProgressBar destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                if (destination.getProgressDrawable() != null)
                    destination.getProgressDrawable().setColorFilter(getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
                else if (destination.getIndeterminateDrawable() != null)
                    destination.getIndeterminateDrawable().setColorFilter(getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
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

    private Drawable getBackgroundDrawable(View destination) {
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
                getBackgroundImagePadding(),
                false,
                getBackgroundImageRenderType());
    }

    public void renderTemplateBlip(final ImageView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                destination.setImageDrawable(UIHelper.createColourBlocks(destination.getContext(),
                        new int[]{getBackgroundColourWithAlpha(), getForegroundColour()}, 20, 20));
                destination.invalidate();
            }
        });
    }

    public void renderTemplateBackgroundTo(final View destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {

                Drawable background = getBackgroundDrawable(destination);
                Bitmap bitmap = ((BitmapDrawable) background).getBitmap();
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(getForegroundColour());
                canvas.drawRect(new Rect(bitmap.getWidth() - 20, bitmap.getHeight() - 20, bitmap.getWidth() - 10, bitmap.getHeight() - 10), paint);
                destination.setBackground(new BitmapDrawable(destination.getResources(), bitmap));
                destination.invalidate();
            }
        });
    }

    /*public void doUnreachable(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setAlpha(0.5f);
                view.setEnabled(false);
                view.setBackgroundColor(Color.parseColor("#424242"));
            }
        });
    }*/

    public void doEnabled(final View view, final boolean enabled) {
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setAlpha(enabled ? 1.0f : 0.5f);
                view.setEnabled(enabled);
            }
        });
    }

    public void renderUnreachableBackground(final View view) {
        if (getThing() != null) {
            if (!getThing().isUnreachable()) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        doEnabled(view, true);
                        view.getBackground().setColorFilter(UIHelper.getDefaultForegroundColour(), PorterDuff.Mode.DARKEN);
                    }
                });
            } else
                doEnabled(view, false);
        } else
            doEnabled(view, false);
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

    public void execute(View indicator, boolean delay, OnProcessCompleteListener<String> onProcessCompleteListener) {
        execute(indicator, delay, getExecutor(), onProcessCompleteListener);
    }

    public void execute(View indicator, boolean delay, IExecutor<?> executor, OnProcessCompleteListener<String> onProcessCompleteListener) {
        try {
            BlockExecutor blockExecutor = new BlockExecutor(indicator, executor, onProcessCompleteListener);
            if (delay)
                delayExecute(indicator.getContext(), blockExecutor);
            else
                blockExecutor.execute(getThing());
        } catch (Exception ex) {
            Toast.makeText(indicator.getContext(), String.format("Could not execute block command: %s", ex.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    private void delayExecute(Context context, final BlockExecutor blockExecutor) {
        Toast.makeText(context, String.format("Executing in %s seconds", EXECUTE_DELAY), Toast.LENGTH_LONG).show();

        final OnProcessCompleteListener completeListener = new OnProcessCompleteListener() {
            @Override
            public void complete(boolean success, Object source) {
                blockExecutor.mProgressIndicator = null;
                blockExecutor.execute(getThing());
            }
        };

        if (mDelay != null) {
            mDelay.interrupt();
            mDelay = null;
        }

        mDelay = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(EXECUTE_DELAY * 1000);
                    completeListener.complete(true, null);
                    mDelay = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        mDelay.start();
    }

    public void clear() {
        if (mDelay != null) {
            mDelay.interrupt();
            mDelay = null;
        }
        if (mThing != null) {
            mThing.clear();
            mThing = null;
        }
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

    public IBlock clone() throws CloneNotSupportedException {
        IBlock block = (IBlock) super.clone();
        if (getThing() != null)
            block.setThing(getThing().clone());
        return block;
    }

    private static class BlockExecutor extends AsyncTask<IThing, Void, JsonExecutorResult> {
        View mProgressIndicator;
        OnProcessCompleteListener<String> mOnProcessCompleteListener;
        IExecutor<?> mExecutor;
        IThing mThing;

        public BlockExecutor(View progressindicator, IExecutor<?> executor, OnProcessCompleteListener<String> onProcessCompleteListener) {
            this.mProgressIndicator = progressindicator;
            this.mOnProcessCompleteListener = onProcessCompleteListener;
            this.mExecutor = executor;
        }

        @Override
        protected void onPreExecute() {
            if (mProgressIndicator != null)
                mProgressIndicator.setVisibility(View.VISIBLE);
            Monitor.getMonitor().stop();
            Broadcaster.broadcastMessage(new ThingChangedMessage("all", ThingChangedMessage.What.Disable));
        }

        @Override
        protected void onPostExecute(JsonExecutorResult jsonExecutorResult) {
            if (mProgressIndicator != null)
                mProgressIndicator.setVisibility(View.INVISIBLE);

            if (mExecutor.doVerification(mThing))
                Monitor.getMonitor().verifyDashboardThings(mExecutor.delayVerification());

            if (!jsonExecutorResult.isSuccess())
                if (mProgressIndicator != null)
                    Toast.makeText(mProgressIndicator.getContext(), "Error executing : " + jsonExecutorResult.getError().getMessage(), Toast.LENGTH_LONG).show();

            if (mOnProcessCompleteListener != null)
                mOnProcessCompleteListener.complete(jsonExecutorResult.isSuccess(), jsonExecutorResult.isSuccess() ?
                        jsonExecutorResult.getResult() :
                        jsonExecutorResult.getError().getMessage());

            Broadcaster.broadcastMessage(new ThingChangedMessage("all", ThingChangedMessage.What.Enable));
            Monitor.getMonitor().start();
        }

        @Override
        protected JsonExecutorResult doInBackground(IThing... iThings) {
            int wait = 0;
            while (Monitor.getMonitor().isBusy()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignore) {
                }
                if (wait > 100)
                    return new JsonExecutorResult(new Exception("Could not get lock for execute"));
                wait++;
            }

            mThing = iThings[0];
            return mThing.execute(mExecutor);
        }
    }
}
