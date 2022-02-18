package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;

/**
 * Created by shane on 27/01/18.
 */

public class ThingPropertiesIcon extends LinearLayoutCompat {
    private ThingsSelector mThingSelector;
    private LabelTextbox mTxtName;
    private SizeSelector mSizeSelector;
    private IconSelector mIconSelector;
    private GroupTitle mDeviceGroupTitle;
    private Switch mSwHideTitle;

    private boolean mHideDevice;

    public ThingPropertiesIcon(Context context) {
        super(context);
        initializeViews(context);
    }

    public ThingPropertiesIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public ThingPropertiesIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public boolean isHideDevice() {
        return mHideDevice;
    }

    public void setHideDevice(boolean hideDevice) {
        mHideDevice = hideDevice;
    }

    private IThing getThing() {
        return mThingSelector.getThing();
    }

    public void setThing(IThing thing) {
        mThingSelector.setThing(thing);
    }

    public String getName() {
        return mTxtName.getText();
    }

    public void setName(String name) {
        mTxtName.setText(name);
    }

    public int getBlockWidth() {
        return mSizeSelector.getBlockWidth();
    }

    public void setBlockWidth(int width) {
        mSizeSelector.setBlockWidth(width);
    }

    public int getBlockHeight() {
        return mSizeSelector.getBlockHeight();
    }

    public void setBlockHeight(int height) {
        mSizeSelector.setBlockHeight(height);
    }

    public String getIconPath() {
        return mIconSelector.getIconPath();
    }

    public void setIconPath(String iconPath) {
        mIconSelector.setIconPath(iconPath);
    }

    public UIHelper.IconSizes getIconSize() {
        return mIconSelector.getIconSize();
    }

    public void setIconSize(UIHelper.IconSizes iconSize) {
        mIconSelector.setIconSize(iconSize);
    }

    public boolean getHideTitle() {
        return mSwHideTitle.isChecked();
    }

    public void setHideTitle(boolean hideTitle) {
        mSwHideTitle.setChecked(hideTitle);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_thing_properties_icon, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mThingSelector = this.findViewById(R.id.prop_things_selector_icon);
        mTxtName = this.findViewById(R.id.prop_txt_blk_name_icon);
        mSizeSelector = this.findViewById(R.id.prop_size_selector_icon);
        mDeviceGroupTitle = this.findViewById(R.id.prop_group_device_icon);
        mIconSelector = this.findViewById(R.id.prop_icon_icon);
        mSwHideTitle = this.findViewById(R.id.prop_sw_title_icon);

        mTxtName.setAutoTextListener();

        if (mHideDevice) {
            if (mThingSelector != null)
                mThingSelector.setVisibility(View.GONE);
            if (mDeviceGroupTitle != null)
                mDeviceGroupTitle.setVisibility(View.GONE);
        }
    }

    public void initialise(Services services, Things things, IIconBlock block) {
        mThingSelector.setData(services, things);
        setThing(block.getThing());
        setName(block.getName());
        setBlockWidth(block.getWidth());
        setBlockHeight(block.getHeight());
        setIconPath(block.getIcon());
        setIconSize(block.getIconSize());
        setHideTitle(block.isHideTitle());

        mThingSelector.setOnThingsSelectedListener(new ThingsSelector.OnThingsSelectedListener() {
            @Override
            public void OnSelected(IThing thing) {
                if (thing != null) {
                    setName(thing.getName());
                }
            }
        });
    }

    public void applyTemplate(Template template) {
        setBlockWidth(template.getBlock().getWidth());
        setBlockHeight(template.getBlock().getHeight());
        setIconPath(template.getBlock(IIconBlock.class).getIcon());
        setIconSize(template.getBlock(IIconBlock.class).getIconSize());
        setHideTitle(template.getBlock().isHideTitle());
    }

    public void populate(IIconBlock block, @SuppressWarnings("SameParameterValue") OnBlockSetListener onBlockSetListener) throws Exception {
        if (TextUtils.isEmpty(getName()))
            throw new Exception("Name required.");
        block.setName(getName());
        block.setWidth(getBlockWidth());
        block.setHeight(getBlockHeight());
        block.setIcon(getIconPath());
        block.setIconSize(getIconSize());
        block.setHideTitle(getHideTitle());

        if (getThing() != null) {
            block.setThing(getThing());
            block.setThingKey(getThing().getKey());
        }
        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(block);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThingPropertiesIcon, 0, 0);
        mHideDevice = a.getBoolean(R.styleable.ThingPropertiesIcon_hide_device, false);
        if (mHideDevice) {
            if (mThingSelector != null)
                mThingSelector.setVisibility(View.GONE);
            if (mDeviceGroupTitle != null)
                mDeviceGroupTitle.setVisibility(View.GONE);
        }
        a.recycle();
    }
}
