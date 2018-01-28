package shane.pennihome.local.smartboard.things.switches;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.BackgroundSelector;
import shane.pennihome.local.smartboard.ui.ForegroundSelector;
import shane.pennihome.local.smartboard.ui.ViewSwiper;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;
import shane.pennihome.local.smartboard.ui.listeners.OnForegroundActionListener;

/**
 * Created by shane on 28/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SwitchPropertiesClrSelector extends LinearLayoutCompat {
    private String mBackgroundImageOff;
    private int mBackgroundImageTransparencyOff;
    @ColorInt
    private int mBackgroundColourOff;
    private int mBackgroundColourTransparencyOff;
    @ColorInt
    private int mForegroundColourOff;

    private BackgroundSelector mBackgroundSelectorOff;
    private ForegroundSelector mForgroundSelectorOff;

    private String mBackgroundImageOn;
    private int mBackgroundImageTransparencyOn;
    @ColorInt
    private int mBackgroundColourOn;
    private int mBackgroundColourTransparencyOn;
    @ColorInt
    private int mForegroundColourOn;

    private BackgroundSelector mBackgroundSelectorOn;
    private ForegroundSelector mForgroundSelectorOn;

    public SwitchPropertiesClrSelector(Context context) {
        super(context);
        initializeViews(context);
    }

    public SwitchPropertiesClrSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SwitchPropertiesClrSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public String getBackgroundImageOff() {
        return mBackgroundImageOff;
    }

    public void setBackgroundImageOff(String backgroundImageOff) {
        mBackgroundImageOff = backgroundImageOff;
    }

    public int getBackgroundImageTransparencyOff() {
        return mBackgroundImageTransparencyOff;
    }

    public void setBackgroundImageTransparencyOff(int backgroundImageTransparencyOff) {
        mBackgroundImageTransparencyOff = backgroundImageTransparencyOff;
    }

    public String getBackgroundImageOn() {
        return mBackgroundImageOn;
    }

    public void setBackgroundImageOn(String backgroundImageOn) {
        mBackgroundImageOn = backgroundImageOn;
    }

    public int getBackgroundImageTransparencyOn() {
        return mBackgroundImageTransparencyOn;
    }

    public void setBackgroundImageTransparencyOn(int backgroundImageTransparencyOn) {
        mBackgroundImageTransparencyOn = backgroundImageTransparencyOn;
    }

    public int getBackgroundColourOn() {
        return mBackgroundColourOn;
    }

    public void setBackgroundColourOn(int backgroundColourOn) {
        mBackgroundColourOn = backgroundColourOn;
    }

    public int getBackgroundColourTransparencyOn() {
        return mBackgroundColourTransparencyOn;
    }

    public void setBackgroundColourTransparencyOn(int backgroundColourTransparencyOn) {
        mBackgroundColourTransparencyOn = backgroundColourTransparencyOn;
    }

    public int getForegroundColourOn() {
        return mForegroundColourOn;
    }

    public void setForegroundColourOn(int foregroundColourOn) {
        mForegroundColourOn = foregroundColourOn;
    }

    public int getBackgroundColourOff() {
        return mBackgroundColourOff;
    }

    public void setBackgroundColourOff(int backgroundColourOff) {
        mBackgroundColourOff = backgroundColourOff;
    }

    public int getBackgroundColourTransparencyOff() {
        return mBackgroundColourTransparencyOff;
    }

    public void setBackgroundColourTransparencyOff(int backgroundColourTransparencyOff) {
        mBackgroundColourTransparencyOff = backgroundColourTransparencyOff;
    }

    public int getForegroundColourOff() {
        return mForegroundColourOff;
    }

    public void setForegroundColourOff(int foregroundColourOff) {
        mForegroundColourOff = foregroundColourOff;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_switch_bg_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewSwiper mSwiper = this.findViewById(R.id.prop_sw_bg_swiper);
        TabLayout mTablayout = this.findViewById(R.id.prop_sw_bg_tabs);
        mSwiper.setTabLayout(mTablayout);

        mSwiper.getViewAdapter().addView("Background Off", R.id.prop_sw_bg_tab_bgclr_off);
        mSwiper.getViewAdapter().addView("Foreground Off", R.id.prop_bg_sw_tab_fgclr_off);
        mSwiper.getViewAdapter().addView("Background On", R.id.prop_bg_sw_tab_bgclr_on);
        mSwiper.getViewAdapter().addView("Foreground On", R.id.prop_bg_sw_tab_fgclr_on);

        mBackgroundSelectorOff = this.findViewById(R.id.prop_sw_bg_clr_off);
        mForgroundSelectorOff = this.findViewById(R.id.prop_sw_fg_clr_off);

        mBackgroundSelectorOn = this.findViewById(R.id.prop_sw_bg_clr_on);
        mForgroundSelectorOn = this.findViewById(R.id.prop_sw_fg_clr_on);

        mBackgroundSelectorOff.setBackgroundActionListener(new OnBackgroundActionListener() {
            @Override
            public void OnColourSelected(int colour) {
                mBackgroundColourOff = colour;
            }

            @Override
            public void OnColourTransparencyChanged(int transparent) {
                mBackgroundColourTransparencyOff = transparent;
            }

            @Override
            public void OnImageTransparencyChanged(int transparent) {
                mBackgroundImageTransparencyOff = transparent;
            }

            @Override
            public void OnImageSelected(String imageFile) {
                mBackgroundImageOff = imageFile;
            }
        });

        mForgroundSelectorOff.setOnForegroundActionListener(new OnForegroundActionListener() {
            @Override
            public void OnColourChange(int colour) {
                mForegroundColourOff = colour;
            }
        });


        mBackgroundSelectorOn.setBackgroundActionListener(new OnBackgroundActionListener() {
            @Override
            public void OnColourSelected(int colour) {
                mBackgroundColourOn = colour;
            }

            @Override
            public void OnColourTransparencyChanged(int transparent) {
                mBackgroundColourTransparencyOn = transparent;
            }

            @Override
            public void OnImageTransparencyChanged(int transparent) {
                mBackgroundImageTransparencyOn = transparent;
            }

            @Override
            public void OnImageSelected(String imageFile) {
                mBackgroundImageOn = imageFile;
            }
        });

        mForgroundSelectorOff.setOnForegroundActionListener(new OnForegroundActionListener() {
            @Override
            public void OnColourChange(int colour) {
                mForegroundColourOn = colour;
            }
        });
    }

    private void doPropertyChange() {
        mBackgroundSelectorOff.setInitialValues(mBackgroundColourOff, mBackgroundColourTransparencyOff, mBackgroundImageOff, mBackgroundImageTransparencyOff);
        mForgroundSelectorOff.setColour(mForegroundColourOff);
        mBackgroundSelectorOn.setInitialValues(mBackgroundColourOn, mBackgroundColourTransparencyOn, mBackgroundImageOn, mBackgroundImageTransparencyOn);
        mForgroundSelectorOn.setColour(mForegroundColourOn);

        invalidate();
        requestLayout();
    }

    public void initialise(Switch thing) {
        mBackgroundColourOff = thing.getBlock().getBackgroundColour();
        mBackgroundColourTransparencyOff = thing.getBlock().getBackgroundColourTransparency();
        mBackgroundImageOff = thing.getBlock().getBackgroundImage();
        mBackgroundImageTransparencyOff = thing.getBlock().getBackgroundImageTransparency();

        mForegroundColourOff = thing.getBlock().getForegroundColour();

        mBackgroundColourOn = thing.getBlock(SwitchBlock.class).getBackgroundColourOn();
        mBackgroundColourTransparencyOn = thing.getBlock(SwitchBlock.class).getBackgroundColourTransparencyOn();
        mBackgroundImageOn = thing.getBlock(SwitchBlock.class).getBackgroundImageOn();
        mBackgroundImageTransparencyOn = thing.getBlock(SwitchBlock.class).getBackgroundImageTransparencyOn();

        mForegroundColourOn = thing.getBlock(SwitchBlock.class).getForegroundColourOn();

        doPropertyChange();
    }

    public void populate(IThing thing) {
        thing.getBlock().setBackgroundColour(mBackgroundColourOff);
        thing.getBlock().setBackgroundColourTransparency(mBackgroundColourTransparencyOff);
        thing.getBlock().setBackgroundImage(mBackgroundImageOff);
        thing.getBlock().setBackgroundImageTransparency(mBackgroundImageTransparencyOff);

        thing.getBlock().setForegroundColour(mForegroundColourOff);

        thing.getBlock(SwitchBlock.class).setBackgroundColourOn(mBackgroundColourOn);
        thing.getBlock(SwitchBlock.class).setBackgroundColourTransparencyOn(mBackgroundColourTransparencyOn);
        thing.getBlock(SwitchBlock.class).setBackgroundImageOn(mBackgroundImageOn);
        thing.getBlock(SwitchBlock.class).setBackgroundImageTransparencyOn(mBackgroundImageTransparencyOn);

        thing.getBlock(SwitchBlock.class).setForegroundColourOn(mForegroundColourOn);

    }
}

