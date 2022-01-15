package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 07/02/2018.
 */

public abstract class IIconBlock extends IBlock {
    private String mIcon;
    private UIHelper.IconSizes mIconSize;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public UIHelper.IconSizes getIconSize() {
        return mIconSize;
    }

    public void setIconSize(UIHelper.IconSizes iconSize) {
        mIconSize = iconSize;
    }

    protected abstract @ColorInt
    int getIconColour();

    public void setIconColour(final ImageView destination) {
        destination.post(new Runnable() {
            @Override
            public void run() {
                Drawable img = destination.getDrawable();
                img.setColorFilter(getIconColour(), PorterDuff.Mode.SRC_ATOP);
                destination.setImageDrawable(img);
            }
        });
    }

    public void renderIconTo(final ImageView destination)
    {
        if(TextUtils.isEmpty(getIcon())) {
            destination.setVisibility(View.GONE);
            return;
        }

        destination.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = destination.getContext().getAssets().open(getIcon());
                    Drawable img = Drawable.createFromStream(stream, getIcon());

                    if (getIconSize() != null) {
                        int s = 40;
                        if (getIconSize() == UIHelper.IconSizes.Medium)
                            s = 80;
                        else if (getIconSize() == UIHelper.IconSizes.Large)
                            s = 160;

                        if (destination.getLayoutParams().height != s)
                            destination.getLayoutParams().height = s;
                        if (destination.getLayoutParams().width != s)
                            destination.getLayoutParams().width = s;
                    }

                    img.setColorFilter(getIconColour(),  PorterDuff.Mode.SRC_ATOP);
                    destination.setImageDrawable(img);
                    destination.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
