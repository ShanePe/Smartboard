package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;

/**
 * Created by SPennicott on 17/01/2018.
 */

public abstract class IBlock extends IDatabaseObject {
    //private IThing mThing;
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
    private String mInstance;

    public IBlock() {
        mInstance = this.getClass().getSimpleName();
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
}
