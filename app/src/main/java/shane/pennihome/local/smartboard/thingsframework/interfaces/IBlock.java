package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
@IDatabaseObject.IgnoreOnCopy
public abstract class IBlock extends IDatabaseObject {
    //private IThing mThing;
    private int mHeight;
    private int mWidth;
    private @ColorInt
    int mForeColour;
    private @ColorInt
    int mBackColour;
    private String mInstance;
    private float mBackTrans;

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
    int getForeColour() {
        return mForeColour;
    }

    public void setForeColour(@ColorInt int foreColour) {
        mForeColour = foreColour;
    }

    public @ColorInt
    int getBackgroundColour() {
        return mBackColour;
    }

    public void setBackgroundColour(@ColorInt int backgroundColour) {
        this.mBackColour = backgroundColour;
    }

    public float getBackgroundTransparency() {
        return mBackTrans;
    }

    public void setBackgroundTransparency(float backgroundTransparency) {
        this.mBackTrans = backgroundTransparency;
    }

    @ColorInt
    public int getBackgroundColourWithAlpha()
    {
        return UIHelper.getColorWithAlpha(getBackgroundColour(), getBackgroundTransparency() / 100);
    }
}
