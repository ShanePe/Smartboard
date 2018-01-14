package shane.pennihome.local.smartboard.Data;

import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import shane.pennihome.local.smartboard.Data.Interface.IDatabaseObject;
import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Block extends IDatabaseObject {
    private Thing mThing;
    private int mHeight;
    private int mWidth;
    private @ColorInt int mForeColourOff;
    private @ColorInt int mBackColourOff;
    private @ColorInt int mForeColourOn;
    private @ColorInt int mBackColourOn;

    @Override
    public Types getType() {
        return Types.Dashboard;
    }
    public Thing getThing() {
        return mThing;
    }

    public void setThing(Thing thing) {
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

    public @ColorInt int getForeColourOff() {
        return mForeColourOff;
    }

    public void setForeColourOff(@ColorInt int foreColour) {
        mForeColourOff = foreColour;
    }

    public @ColorInt int getBackgroundColourOff() {
        return mBackColourOff;
    }

    public void setBackgroundColourOff(@ColorInt int backgroundColour) {
        this.mBackColourOff = backgroundColour;
    }

    public @ColorInt int getForeColourOn() {
        return mForeColourOn;
    }

    public void setForeColourOn(@ColorInt int foreColour) {
        mForeColourOn = foreColour;
    }

    public @ColorInt int getBackgroundColourOn() {
        return mBackColourOn;
    }

    public void setBackgroundColourOn(@ColorInt int backgroundColour) {
        this.mBackColourOn = backgroundColour;
    }

    public Block(){}

    public static Block Load(String json)
    {
        try {
            return IDatabaseObject.Load(Block.class, json);
        } catch (Exception e) {
            return new Block();
        }
    }


}
