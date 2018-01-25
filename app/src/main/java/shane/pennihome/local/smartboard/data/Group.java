package shane.pennihome.local.smartboard.data;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.ui.GroupViewHandler;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class Group extends IDatabaseObject {
    private final Things mThings = new Things();
    private boolean mDisplayName = false;
    private @ColorInt
    int mDefaultBlockForeColourOff;
    private @ColorInt
    int mDefaultBlockBackColourOff;
    private @ColorInt
    int mDefaultBlockForeColourOn;
    private @ColorInt
    int mDefaultBlockBackColourOn;
    private transient GroupViewHandler mGroupViewHandler;
    boolean mIsUIExpanded;

    public boolean isUIExpanded() {
        return mIsUIExpanded;
    }

    public void setUIExpanded(boolean UIExpanded) {
        mIsUIExpanded = UIExpanded;
    }

    public Group() {
    }

    public Group(String name) {
        this.setName(name);
    }

    public static Group Load(String json) {
        try {
            return IDatabaseObject.Load(Group.class, json);
        } catch (Exception e) {
            return new Group();
        }
    }

    public Things getThings() {
        return mThings;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Group;
    }

    public boolean getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(boolean displayName) {
        this.mDisplayName = displayName;
    }

    public IThing getThingsAt(int index) {
        return mThings.get(index);
    }

    public @ColorInt
    int getDefaultBlockForeColourOff() {
        return mDefaultBlockForeColourOff;
    }

    public void setDefaultBlockForeColourOff(@ColorInt int foreColour) {
        mDefaultBlockForeColourOff = foreColour;
    }

    public @ColorInt
    int getDefaultBlockBackgroundColourOff() {
        return mDefaultBlockBackColourOff;
    }

    public void setDefaultBlockBackgroundColourOff(@ColorInt int backgroundColour) {
        this.mDefaultBlockBackColourOff = backgroundColour;
    }

    public @ColorInt
    int getDefaultBlockForeColourOn() {
        return mDefaultBlockForeColourOn;
    }

    public void setDefaultBlockForeColourOn(@ColorInt int foreColour) {
        mDefaultBlockForeColourOn = foreColour;
    }

    public @ColorInt
    int getDefaultBlockBackgroundColourOn() {
        return mDefaultBlockBackColourOn;
    }

    public void setDefaultBlockBackgroundColourOn(@ColorInt int backgroundColour) {
        this.mDefaultBlockBackColourOn = backgroundColour;
    }

    public GroupViewHandler getGroupViewHandler() {
        return mGroupViewHandler;
    }

    public void setGroupViewHandler(GroupViewHandler groupViewHandler) {
        this.mGroupViewHandler = groupViewHandler;
    }

}
