package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.thingsframework.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.listeners.OnSizeActionListener;

/**
 * Created by shane on 27/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ThingProperties extends LinearLayoutCompat {
    private IThing mThing;
    private String mName;
    private int mBlockWidth;
    private int mBlockHeight;
    private Things mThings;

    private Spinner mSpThing;
    private LabelTextbox mTxtName;
    private GroupTitle mDeviceGroupTitle;
    private SizeSelector mSizeSelector;

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

    public Things getThings() {
        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
        doSpinnerThings();
        doPropertyChange();
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

        mSpThing = this.findViewById(R.id.prop_sp_thing);
        mTxtName = this.findViewById(R.id.prop_txt_blk_name);
        mDeviceGroupTitle = this.findViewById(R.id.prop_group_device);
        mSizeSelector = this.findViewById(R.id.prop_size_selector);

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

        doSpinnerThings();
        doPropertyChange();
    }

    private void doSpinnerThings() {
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
        if (mThing != null)
            mSpThing.setSelection(mThings.getIndex(mThing));

        mTxtName.setText(mName);
        mSizeSelector.setSize(mBlockWidth, mBlockHeight);

        invalidate();
        requestLayout();
    }

    public void initialise(Things things, IBlock block) {
        mThings = things;
        mThing = block.getThing();
        mName = block.getName();
        mBlockWidth = block.getWidth();
        mBlockHeight = block.getHeight();

        doSpinnerThings();
        doPropertyChange();
    }

    public void applyTemplate(Template template)
    {
        mBlockWidth = template.getBlock().getWidth();
        mBlockHeight = template.getBlock().getHeight();
    }

    public void populate(IBlock block, @SuppressWarnings("SameParameterValue") OnBlockSetListener onBlockSetListener) throws Exception {
        if (TextUtils.isEmpty(mName))
            throw new Exception("Name required.");
        block.setName(mName);
        block.setWidth(mBlockWidth);
        block.setHeight(mBlockHeight);
        if (mThing != null) {
            block.setThing(mThing);
            block.setThingKey(mThing.getKey());
        }
        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(block);
    }
}
