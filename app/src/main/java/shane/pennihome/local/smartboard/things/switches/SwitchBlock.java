package shane.pennihome.local.smartboard.things.switches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

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
    public int getForegroundColourOn() {
        return mForeColourOn;
    }

    void setForegroundColourOn(@ColorInt int foreColour) {
        mForeColourOn = foreColour;
    }

    @ColorInt
    int getBackgroundColourOn() {
        try {
            Switch s = getThing(Switch.class);

            if (s.SupportsColour())
                return mBackColourOn;//s.getCurrentColour();
        }
        catch(Exception ignore) {
            return mBackColourOn;
        }
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
    public int getBackgroundColourWithAlphaOn() {
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
        return R.mipmap.icon_def_switch_mm_fg;
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
        return new SwitchBlockUIHandler(this);
    }

    public void renderForegroundColourToTextView(final TextView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                boolean isOn = false;
                if (s != null)
                    isOn = s.isOn();
                destination.setTextColor(isOn ? getForegroundColourOn() : getForegroundColour());
            }
        });
    }

    public void renderBackgroundTo(final View destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Switch s = getThing(Switch.class);
                boolean isOn = false;
                if (s != null)
                    isOn = s.isOn();

                Bitmap bitmap = null;
                if (!TextUtils.isEmpty(isOn ? getBackgroundImageOn() : getBackgroundImage()))
                    bitmap = BitmapFactory.decodeFile(isOn ? getBackgroundImageOn() : getBackgroundImage());

                final Drawable drawable = UIHelper.generateImage(
                        destination.getContext(),
                        isOn ? getBackgroundColourOn() : getBackgroundColour(),
                        isOn ? getBackgroundColourTransparencyOn() : getBackgroundColourTransparency(),
                        bitmap,
                        isOn ? getBackgroundImageTransparencyOn() : getBackgroundColourTransparency(),
                        destination.getMeasuredWidth(),
                        destination.getMeasuredHeight(),
                        false,
                        isOn ? getBackgroundImageRenderTypeOn() : getBackgroundImageRenderType());

                destination.setBackground(drawable);
                destination.invalidate();
            }
        });
    }

    public void renderTemplateBackgroundTo(final View destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap1 = null;
                Bitmap bitmap2 = null;

                if (!TextUtils.isEmpty(getBackgroundImage()))
                    bitmap1 = BitmapFactory.decodeFile(getBackgroundImage());

                if (!TextUtils.isEmpty(getBackgroundImageOn()))
                    bitmap2 = BitmapFactory.decodeFile(getBackgroundImageOn());

                Drawable drawable1 = UIHelper.generateImage(
                        destination.getContext(),
                        getBackgroundColour(),
                        getBackgroundColourTransparency(),
                        bitmap1,
                        getBackgroundColourTransparency(),
                        destination.getMeasuredWidth(),
                        destination.getMeasuredHeight(),
                        false,
                        getBackgroundImageRenderType());

                Drawable drawable2 = UIHelper.generateImage(
                        destination.getContext(),
                        getBackgroundColourOn(),
                        getBackgroundColourTransparencyOn(),
                        bitmap2,
                        getBackgroundImageTransparencyOn(),
                        destination.getMeasuredWidth(),
                        destination.getMeasuredHeight(),
                        false,
                        getBackgroundImageRenderTypeOn());

                Drawable background = UIHelper.combineDrawables(destination.getContext(), drawable1, drawable2, destination.getMeasuredWidth(), destination.getMeasuredHeight());

                Bitmap bitmap = ((BitmapDrawable)background).getBitmap();
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setColor(getForegroundColour());
                canvas.drawRect(new Rect(bitmap.getWidth()-20,(bitmap.getHeight()/2)-20,bitmap.getWidth()-10,(bitmap.getHeight()/2)-10 ), paint);
                paint.setColor(getForegroundColourOn());
                canvas.drawRect(new Rect(bitmap.getWidth()-20,bitmap.getHeight()-20,bitmap.getWidth()-10,bitmap.getHeight()-10 ), paint);

                destination.setBackground(new BitmapDrawable(destination.getResources(), bitmap));
                destination.invalidate();
            }
        });
    }

    public void renderTemplateBlip(final ImageView destination)
    {
        destination.post(new Runnable() {
            @Override
            public void run() {
                destination.setImageDrawable(UIHelper.createColourBlocks(destination.getContext(),
                        new int[]{getBackgroundColourWithAlpha(), getForegroundColour(), getBackgroundColourWithAlphaOn(), getForegroundColourOn()},20,20));
                destination.invalidate();
            }
        });
    }

    @Override
    public int getIconColour() {
        if(getThing() == null)
            return getForegroundColour();
        else
            return getThing(Switch.class).isOn() ? getForegroundColourOn() : getForegroundColour();
    }
}
