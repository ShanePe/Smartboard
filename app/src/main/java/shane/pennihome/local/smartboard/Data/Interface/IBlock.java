package shane.pennihome.local.smartboard.Data.Interface;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.SwitchBlock;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.UI.Interface.IBlockUI;

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

    public abstract IBlockUI getUIHandler();
    public abstract int GetViewResourceID();

    public IBlock() {
        mInstance = this.getClass().getSimpleName();
    }

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

    public static int GetTypeID(IBlock block)
    {
        if(block instanceof SwitchBlock)
            return 0;

        return -1;
    }

    public static IBlock CreateByTypeID(int i)
    {
        if(i == 0)
            return new SwitchBlock();

        return null;
    }

}
