package shane.pennihome.local.smartboard.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class Dashboard extends IDatabaseObject {
    private final List<Group> mGroups = new ArrayList<>();
    private String mBackgroundImage;
    private int mBackgroundImageTrans = 0;
    @ColorInt
    private int mBackgroundColour = Color.TRANSPARENT;
    private int mBackgroundClrTrans = 0;

    public static Dashboard Load(String json) {
        Dashboard ret = new Dashboard();
        try {
            ret = JsonBuilder.Get().fromJson(json, Dashboard.class);
        } catch (Exception e) {
            Log.e("Smartboard", "Error : " + e.getMessage());
        }

        return ret;
    }

    public int getBackgroundImageTransparency() {
        return mBackgroundImageTrans;
    }

    public void setBackgroundImageTransparency(int backgroundimagetrans) {
        this.mBackgroundImageTrans = backgroundimagetrans;
    }

    @ColorInt
    public int getBackgroundColour() {
        return mBackgroundColour;
    }

    public void setBackgroundColour(@ColorInt int backgroundColour) {
        this.mBackgroundColour = backgroundColour;
    }

    public int getBackgroundColourTransparency() {
        return mBackgroundClrTrans;
    }

    public void setBackgroundColourTransparency(int backgroundClrTrans) {
        this.mBackgroundClrTrans = backgroundClrTrans;
    }

    public String getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Dashboard;
    }

    public Group getGroupAt(int index) {
        return mGroups.get(index);
    }

    @ColorInt
    public int getBackgroundColourWithAlpha() {
        return UIHelper.getColorWithAlpha(getBackgroundColour(), getBackgroundColourTransparency() / 100f);
    }

    public Drawable getBackgroundImageWithAlpha(Context context) {
        return UIHelper.getImageFromFile(context, getBackgroundImage(), getBackgroundImageTransparency());
    }

    public Drawable getBackgroundImageWithoutAlpha(Context context) {
        return UIHelper.getImageFromFile(context, getBackgroundImage());
    }

    public void createBackground(Context context, View destination) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(getBackgroundImage()))
            bitmap = BitmapFactory.decodeFile(getBackgroundImage());

        destination.setBackground(UIHelper.generateImage(context,
                getBackgroundColour(),
                getBackgroundColourTransparency(),
                bitmap,
                getBackgroundImageTransparency(),
                destination.getMeasuredWidth(),
                destination.getMeasuredHeight(),
                false));
    }
}
