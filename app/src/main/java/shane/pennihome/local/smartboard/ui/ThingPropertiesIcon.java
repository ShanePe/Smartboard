package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.thingsframework.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.listeners.OnIconActionListener;
import shane.pennihome.local.smartboard.ui.listeners.OnSizeActionListener;

/**
 * Created by shane on 27/01/18.
 */

public class ThingPropertiesIcon extends LinearLayoutCompat {
    private IThing mThing;
    private String mName;
    private int mBlockWidth;
    private int mBlockHeight;
    private Things mThings;
    private String mIconPath;
    private UIHelper.IconSizes mIconSize;
    private boolean mHideTitle;

    private Spinner mSpThing;
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
        return mThing;
    }

    public void setThing(IThing thing) {
        mThing = thing;
        doPropertyChange();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        doPropertyChange();
    }

    public int getBlockWidth() {
        return mBlockWidth;
    }

    public void setBlockWidth(int width) {
        mBlockWidth = width;
        doPropertyChange();
    }

    public int getBlockHeight() {
        return mBlockHeight;
    }

    public void setBlockHeight(int height) {
        mBlockHeight = height;
        doPropertyChange();
    }

    public String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconPath) {
        mIconPath = iconPath;
    }

    public UIHelper.IconSizes getIconSize() {
        return mIconSize;
    }

    public void setIconSize(UIHelper.IconSizes iconSize) {
        mIconSize = iconSize;
    }

    public Things getThings() {
        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
        doSpinnerThings();
        doPropertyChange();
    }

    public boolean isHideTitle() {
        return mHideTitle;
    }

    public void setHideTitle(boolean hideTitle) {
        this.mHideTitle = hideTitle;
        doPropertyChange();
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

        mSpThing = this.findViewById(R.id.prop_sp_thing_icon);
        mTxtName = this.findViewById(R.id.prop_txt_blk_name_icon);
        mSizeSelector = this.findViewById(R.id.prop_size_selector_icon);
        mDeviceGroupTitle = this.findViewById(R.id.prop_group_device_icon);
        mIconSelector = this.findViewById(R.id.prop_icon_icon);
        mSwHideTitle = this.findViewById(R.id.prop_sw_title_icon);

        mSpThing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IThing thing = (IThing) adapterView.getItemAtPosition(i);
                if (TextUtils.isEmpty(mName))
                    mName = thing.getName();
                else if (mThing == null)
                    mName = thing.getName();
                else if (TextUtils.isEmpty(mName) || mThing.getName().equals(mName))
                    mName = thing.getName();

                mThing = thing;
                doPropertyChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mThing = null;
                doPropertyChange();
            }
        });

        mTxtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                mName = editable.toString();
            }
        });

        mIconSelector.setOnIconActionListener(new OnIconActionListener() {
            @Override
            public void OnIconSelected(String iconPath) {
                mIconPath = iconPath;
            }

            @Override
            public void OnIconSizeChanged(UIHelper.IconSizes iconSize) {
                mIconSize = iconSize;
            }
        });

        mSizeSelector.setOnSizeActionListener(new OnSizeActionListener() {
            @Override
            public void onWidthChanged(int width) {
                mBlockWidth = width;
            }

            @Override
            public void onHeightChanged(int height) {
                mBlockHeight = height;
            }
        });

        mSwHideTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHideTitle = isChecked;
            }
        });

        doSpinnerThings();
        doPropertyChange();
    }

    private void doSpinnerThings() {
        if (mHideDevice)
            return;

        if (mSpThing != null && mThings != null) {
            mSpThing.setVisibility(View.VISIBLE);
            mDeviceGroupTitle.setVisibility(View.VISIBLE);
            SpinnerThingAdapter aptr = new SpinnerThingAdapter(getContext());
            aptr.setThings(mThings);
            mSpThing.setAdapter(aptr);
        } else //noinspection ConstantConditions
            if (mSpThing != null && mThings == null) {
                mSpThing.setVisibility(View.GONE);
                mDeviceGroupTitle.setVisibility(View.GONE);
            }
    }

    private void doPropertyChange() {
        if (mHideDevice) {
            if (mSpThing != null)
                mSpThing.setVisibility(View.GONE);
            if (mDeviceGroupTitle != null)
                mDeviceGroupTitle.setVisibility(View.GONE);
        } else if (mThing != null)
            mSpThing.setSelection(mThings.getIndex(mThing));


        mTxtName.setText(mName);
        mSizeSelector.setSize(mBlockWidth, mBlockHeight);
        mIconSelector.setIcon(mIconPath, mIconSize);
        mSwHideTitle.setChecked(mHideTitle);

        invalidate();
        requestLayout();
    }

    public void initialise(Things things, IIconBlock block) {
        mThings = things;
        mThing = block.getThing();
        mName = block.getName();
        mBlockWidth = block.getWidth();
        mBlockHeight = block.getHeight();
        mIconPath = block.getIcon();
        mIconSize = block.getIconSize();
        mHideTitle = block.isHideTitle();

        doSpinnerThings();
        doPropertyChange();
    }

    public void applyTemplate(Template template) {
        mBlockWidth = template.getBlock().getWidth();
        mBlockHeight = template.getBlock().getHeight();
        mIconPath = template.getBlock(IIconBlock.class).getIcon();
        mIconSize = template.getBlock(IIconBlock.class).getIconSize();
        mHideTitle = template.getBlock().isHideTitle();
    }

    public void populate(IIconBlock block, @SuppressWarnings("SameParameterValue") OnBlockSetListener onBlockSetListener) throws Exception {
        if (TextUtils.isEmpty(mName))
            throw new Exception("Name required.");
        block.setName(mName);
        block.setWidth(mBlockWidth);
        block.setHeight(mBlockHeight);
        block.setIcon(mIconPath);
        block.setIconSize(mIconSize);
        block.setHideTitle(mHideTitle);

        if (mThing != null) {
            block.setThing(mThing);
            block.setThingKey(mThing.getKey());
        }
        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(block);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThingPropertiesIcon, 0, 0);
        mHideDevice = a.getBoolean(R.styleable.ThingPropertiesIcon_hide_device, false);
        if (mHideDevice) {
            if (mSpThing != null)
                mSpThing.setVisibility(View.GONE);
            if (mDeviceGroupTitle != null)
                mDeviceGroupTitle.setVisibility(View.GONE);
        }
        a.recycle();
    }
}
