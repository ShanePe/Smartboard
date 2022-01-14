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
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings({"unused"})
public class Dashboard extends IDatabaseObject {
    private final List<Group> mGroups = new ArrayList<>();
    private String mBackgroundImage;
    private int mBackgroundImageTrans = 0;
    @ColorInt
    private int mBackgroundColour = Color.TRANSPARENT;
    private int mBackgroundClrTrans = 0;
    private transient Thread mBackgroundThread;
    private UIHelper.ImageRenderTypes mBackgroundImageRenderType;
    private int mBackgroundImagePadding = 0;

    public static Dashboard Load(String json) {
        Dashboard ret = new Dashboard();
        try {
            ret = JsonBuilder.get().fromJson(json, Dashboard.class);
        } catch (Exception e) {
            Log.e("Smartboard", "Error : " + e.getMessage());
        }

        return ret;
    }

    public UIHelper.ImageRenderTypes getBackgroundImageRenderType() {
        return mBackgroundImageRenderType;
    }

    public void setBackgroundImageRenderType(UIHelper.ImageRenderTypes backgroundImageRenderType) {
        mBackgroundImageRenderType = backgroundImageRenderType;
    }

    public Thread getBackgroundThread() {
        return mBackgroundThread;
    }

    public void executeBackgroundThread(Runnable runnable) {
        if (mBackgroundThread != null) {
            mBackgroundThread.interrupt();
            if (mBackgroundThread != null)
                try {
                    mBackgroundThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            mBackgroundThread = null;
        }
        mBackgroundThread = new Thread(runnable);
        mBackgroundThread.start();
    }

    public void loadThings() {
        for (Group g : getGroups())
            g.loadThings();
    }

    public List<IBlock> GetBlocks() {
        List<IBlock> ret = new ArrayList<>();
        for (Group g : getGroups())
            ret.addAll(g.getBlocks());
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

    public int getBackgroundImagePadding() {
        return mBackgroundImagePadding;
    }

    public void setBackgroundImagePadding(int backgroundImagePadding) {
        this.mBackgroundImagePadding = backgroundImagePadding;
    }

    public void renderBackgroundTo(View destination) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(getBackgroundImage()))
            bitmap = BitmapFactory.decodeFile(getBackgroundImage());

        destination.setBackground(UIHelper.generateImage(destination.getContext(),
                getBackgroundColour(),
                getBackgroundColourTransparency(),
                bitmap,
                getBackgroundImageTransparency(),
                destination.getMeasuredWidth(),
                destination.getMeasuredHeight(),
                getBackgroundImagePadding(),
                false,
                getBackgroundImageRenderType()));
    }

    public void clear() {
        for (int i = mGroups.size() - 1; i >= 0; i--) {
            mGroups.get(i).clear();
            mGroups.set(i, null);
            mGroups.remove(i);
        }
    }
}
