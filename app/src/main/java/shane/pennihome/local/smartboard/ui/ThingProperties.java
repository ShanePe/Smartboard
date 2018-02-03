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
import android.widget.NumberPicker;
import android.widget.Spinner;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;

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
    private NumberPicker mNpWidth;
    private NumberPicker mNpHeight;

    private Thread mTextWatcherThread;

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
        mNpWidth = this.findViewById(R.id.prop_txt_blk_width);
        mNpHeight = this.findViewById(R.id.prop_txt_blk_height);

        mNpWidth.setMaxValue(4);
        mNpWidth.setMinValue(1);
        mNpHeight.setMaxValue(4);
        mNpHeight.setMinValue(1);
        mNpWidth.setWrapSelectorWheel(true);
        mNpHeight.setWrapSelectorWheel(true);

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
                if (mTextWatcherThread != null) {
                    mTextWatcherThread.interrupt();
                    mTextWatcherThread = null;
                }

                mTextWatcherThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            mName = editable.toString();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                mTextWatcherThread.start();
            }
        });

        mNpWidth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mBlockWidth = i1;
            }
        });

        mNpHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                mBlockHeight = i1;
            }
        });

        doSpinnerThings();
        doPropertyChange();
    }

    private void doSpinnerThings() {
        if (mSpThing != null && mThings != null) {
            SpinnerThingAdapter aptr = new SpinnerThingAdapter(getContext());
            aptr.setThings(mThings);
            mSpThing.setAdapter(aptr);
        }
    }

    private void doPropertyChange() {
        if (mThing != null)
            mSpThing.setSelection(mThings.GetIndex(mThing));

        mTxtName.setText(mName);
        mNpWidth.setValue(mBlockWidth);
        mNpHeight.setValue(mBlockHeight);

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

    public void populate(IBlock block, @SuppressWarnings("SameParameterValue") OnBlockSetListener onBlockSetListener) {
        block.setName(mName);
        block.setWidth(mBlockWidth);
        block.setHeight(mBlockHeight);
        block.setThing(mThing);
        block.setThingKey(mThing.getKey());
        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(block);
    }
}
