package shane.pennihome.local.smartboard.things.switches;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchBlock extends IBlock {
    private @ColorInt
    int mForeColourOn;
    private @ColorInt
    int mBackColourOn;
    private int mBackTransOn;

    public static SwitchBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SwitchBlock.class, json);
        } catch (Exception e) {
            return new SwitchBlock();
        }
    }

    @ColorInt
    int getForeColourOn() {
        return mForeColourOn;
    }

    void setForeColourOn(@ColorInt int foreColour) {
        mForeColourOn = foreColour;
    }

    @ColorInt
    int getBackgroundColourOn() {
        return mBackColourOn;
    }

    void setBackgroundColourOn(@ColorInt int backgroundColour) {
        this.mBackColourOn = backgroundColour;
    }

    int getBackgroundTransparencyOn() {
        return mBackTransOn;
    }

    void setBackgroundTransparencyOn(int backgroundTransparency) {
        this.mBackTransOn = backgroundTransparency;
    }

    @ColorInt
    int getBackgroundColourWithAlphaOn()
    {
        return UIHelper.getColorWithAlpha(getBackgroundColourOn(), getBackgroundTransparencyOn() / 100f);
    }


}
