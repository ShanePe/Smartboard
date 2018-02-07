package shane.pennihome.local.smartboard.things.switches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchBlock extends IIconBlock {
    private @ColorInt
    int mForeColourOn;
    private @ColorInt
    int mBackColourOn;
    private int mBackTransOn;
    private String mBackgroundImageOn;
    private int mBackgroundImageTransparencyOn;
    private UIHelper.ImageRenderTypes mBackgroundImageRenderTypeOn;

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

    UIHelper.ImageRenderTypes getBackgroundImageRenderTypeOn() {
        return mBackgroundImageRenderTypeOn;
    }

    void setBackgroundImageRenderTypeOn(UIHelper.ImageRenderTypes backgroundImageRenderTypeOn) {
        mBackgroundImageRenderTypeOn = backgroundImageRenderTypeOn;
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_switch;
    }

    @Override
    public String getFriendlyName() {
        return "Switch";
    }

    @Override
    public void setBlockDefaults(Group group) {
        super.setBlockDefaults(group);

        setBackgroundColourTransparencyOn(100);
        setBackgroundColourOn(group.getDefaultBlockBackgroundColourOn() != 0 ?
                group.getDefaultBlockBackgroundColourOn() :
                Color.parseColor("#FF4081"));

        setForegroundColourOn(group.getDefaultBlockForeColourOn() != 0 ?
                group.getDefaultBlockForeColourOn() :
                Color.parseColor("black"));
    }

    @Override
    public IThing.Types getThingType() {
        return IThing.Types.Switch;
    }

    @Override
    public IBlockUIHandler getUIHandler() {
        return new SwitchBlockHandler(this);
    }

    public void renderForegroundColourToTextView(final TextView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                destination.setTextColor(s.isOn() ? getForegroundColourOn() : getForegroundColour());
            }
        });
    }

    public void renderBackgroundTo(final View destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
            Switch s = getThing(Switch.class);

            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(s.isOn() ? getBackgroundImageOn() : getBackgroundImage()))
                bitmap = BitmapFactory.decodeFile(s.isOn() ? getBackgroundImageOn() : getBackgroundImage());

            final Drawable drawable = UIHelper.generateImage(
                    destination.getContext(),
                    s.isOn() ? getBackgroundColourOn() : getBackgroundColour(),
                    s.isOn() ? getBackgroundColourTransparencyOn() : getBackgroundColourTransparency(),
                    bitmap,
                    s.isOn() ? getBackgroundImageTransparencyOn() : getBackgroundColourTransparency(),
                    destination.getMeasuredWidth(),
                    destination.getMeasuredHeight(),
                    false,
                    s.isOn() ? getBackgroundImageRenderTypeOn() : getBackgroundImageRenderType());

            destination.setBackground(drawable);
            destination.invalidate();
            }
        });
    }

    @Override
    public int getIconColour() {
        return getThing(Switch.class).isOn() ? getForegroundColourOn() : getForegroundColour();
    }
}
