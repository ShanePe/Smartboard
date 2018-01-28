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
    private String mBackgroundImageOn;
    private int mBackgroundImageTransparencyOn;


    public static SwitchBlock Load(String json) {
        try {
            return IDatabaseObject.Load(SwitchBlock.class, json);
        } catch (Exception e) {
            return new SwitchBlock();
        }
    }

    @ColorInt
    int getForegroundColourOn() {
        return mForeColourOn;
    }

    void setForegroundColourOn(@ColorInt int foreColour) {
        mForeColourOn = foreColour;
    }

    @ColorInt
    int getBackgroundColourOn() {
        return mBackColourOn;
    }

    void setBackgroundColourOn(@ColorInt int backgroundColour) {
        this.mBackColourOn = backgroundColour;
    }

    int getBackgroundColourTransparencyOn() {
        return mBackTransOn;
    }

    void setBackgroundColourTransparencyOn(int backgroundTransparency) {
        this.mBackTransOn = backgroundTransparency;
    }

    String getBackgroundImageOn() {
        return mBackgroundImageOn;
    }

    void setBackgroundImageOn(String backgroundImageOn) {
        mBackgroundImageOn = backgroundImageOn;
    }

    int getBackgroundImageTransparencyOn() {
        return mBackgroundImageTransparencyOn;
    }

    void setBackgroundImageTransparencyOn(int backgroundImageTransparencyOn) {
        mBackgroundImageTransparencyOn = backgroundImageTransparencyOn;
    }

    @ColorInt
    int getBackgroundColourWithAlphaOn()
    {
        return UIHelper.getColorWithAlpha(getBackgroundColourOn(), getBackgroundColourTransparencyOn() / 100f);
    }


}
