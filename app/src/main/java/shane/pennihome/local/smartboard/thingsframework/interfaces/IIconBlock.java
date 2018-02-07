package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SPennicott on 07/02/2018.
 */

public abstract class IIconBlock extends IBlock {
    private String mIcon;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public abstract @ColorInt int getIconColour();

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
