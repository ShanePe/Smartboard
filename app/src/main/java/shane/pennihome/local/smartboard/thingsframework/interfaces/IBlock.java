package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
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

    public IThing getThing() {
        return mThing;
    }

    public void setThing(IThing thing) {
        mThing = thing;
    }

    public String getThingKey() {
        return mThingKey;
    }

    public void setThingKey(String thingKey) {
        mThingKey = thingKey;
    }

    public abstract int getDefaultIconResource();

    public abstract String getFriendlyName();

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

    public void renderBackground(final View view) {
        Handler safeRender = new Handler();
        safeRender.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (!TextUtils.isEmpty(mBackImage))
                    bitmap = BitmapFactory.decodeFile(mBackImage);

                final Drawable drawable = UIHelper.generateImage(
                        view.getContext(),
                        mBackColour, mBackTrans,
                        bitmap,
                        mBGImgTrans,
                        view.getMeasuredWidth(),
                        view.getMeasuredHeight(),
                        false,
                        mBGImageRenderType);

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackground(drawable);
                    }
                });
            }
        });
    }

    public void loadThing() {
        if (TextUtils.isEmpty(getThingKey()) || mThing != null)
            mThing = Monitor.getMonitor().getThings().getByKey(getThingKey());
    }
}
