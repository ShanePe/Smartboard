package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
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

    public int getBackgroundColourTransparency() {
        return mBackTrans;
    }

    public void setBackgroundColourTransparency(int backgroundTransparency) {
        this.mBackTrans = backgroundTransparency;
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
                        false);

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackground(drawable);
                    }
                });
            }
        });
    }
}
