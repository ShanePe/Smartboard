package shane.pennihome.local.smartboard.blocks.interfaces;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.blocks.switchblock.SwitchBlock;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.ui.interfaces.IBlockUI;

/**
 * Created by SPennicott on 17/01/2018.
 */

public abstract class IBlock extends IDatabaseObject {
    private IThing mThing;
    private int mHeight;
    private int mWidth;
    private @ColorInt
    int mForeColourOff;
    private @ColorInt
    int mBackColourOff;
    private @ColorInt
    int mForeColourOn;
    private @ColorInt
    int mBackColourOn;
    private long mGroupId;
    private String mInstance;

    public IBlock() {
        mInstance = this.getClass().getSimpleName();
    }

    public static int GetTypeID(IBlock block) {
        if (block instanceof SwitchBlock)
            return 0;

        return -1;
    }

    public static IBlock CreateByTypeID(int i) {
        if (i == 0)
            return new SwitchBlock();

        return null;
    }

    public abstract IBlockUI getUIHandler();

    public abstract int GetViewResourceID();

    @Override
    public IDatabaseObject.Types getType() {
        return IDatabaseObject.Types.Dashboard;
    }

    public IThing getThing() {
        return mThing;
    }

    public void setThing(IThing thing) {
        mThing = thing;
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
    int getForeColourOff() {
        return mForeColourOff;
    }

    public void setForeColourOff(@ColorInt int foreColour) {
        mForeColourOff = foreColour;
    }

    public @ColorInt
    int getBackgroundColourOff() {
        return mBackColourOff;
    }

    public void setBackgroundColourOff(@ColorInt int backgroundColour) {
        this.mBackColourOff = backgroundColour;
    }

    public @ColorInt
    int getForeColourOn() {
        return mForeColourOn;
    }

    public void setForeColourOn(@ColorInt int foreColour) {
        mForeColourOn = foreColour;
    }

    public @ColorInt
    int getBackgroundColourOn() {
        return mBackColourOn;
    }

    public void setBackgroundColourOn(@ColorInt int backgroundColour) {
        this.mBackColourOn = backgroundColour;
    }

    public long getGroupId() {
        return mGroupId;
    }

    public void setGroupId(long groupId) {
        this.mGroupId = groupId;
    }

}
