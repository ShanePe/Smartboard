package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Switch;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;

/**
 * Created by shane on 27/01/18.
 */

public class ThingProperties extends LinearLayoutCompat {
    private ThingsSelector mThingSelector;
    private LabelTextbox mTxtName;
    private GroupTitle mDeviceGroupTitle;
    private SizeSelector mSizeSelector;
    private Switch mSwHideTitle;

    public ThingProperties(Context context) {
        super(context);
        initializeViews(context);
    }

    public ThingProperties(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ThingProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
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

    public boolean getHideTitle() {
        return mSwHideTitle.isChecked();
    }

    public void setHideTitle(boolean hide) {
        mSwHideTitle.setChecked(hide);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_thing_properties, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mThingSelector = this.findViewById(R.id.prop_things_selector);
        mTxtName = this.findViewById(R.id.prop_txt_blk_name);
        mDeviceGroupTitle = this.findViewById(R.id.prop_group_device);
        mSizeSelector = this.findViewById(R.id.prop_size_selector);
        mSwHideTitle = this.findViewById(R.id.prop_sw_title);

        mTxtName.SetAutoTextListener();
    }

    public void initialise(Services services, Things things, IBlock block) {
        mThingSelector.setData(services, things);
        setThing(block.getThing());
        setName(block.getName());
        setBlockWidth(block.getWidth());
        setBlockHeight(block.getHeight());
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
        setHideTitle(template.getBlock().isHideTitle());
    }

    public void populate(IBlock block, @SuppressWarnings("SameParameterValue") OnBlockSetListener onBlockSetListener) throws Exception {
        if (TextUtils.isEmpty(getName()))
            throw new Exception("Name required.");
        block.setName(getName());
        block.setWidth(getWidth());
        block.setHeight(getHeight());
        block.setHideTitle(getHideTitle());

        if (getThing() != null) {
            block.setThing(getThing());
            block.setThingKey(getThing().getKey());
        }
        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(block);
    }
}
