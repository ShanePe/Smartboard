package shane.pennihome.local.smartboard.things.switches;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.ui.BackgroundSelector;
import shane.pennihome.local.smartboard.ui.ForegroundSelector;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;
import shane.pennihome.local.smartboard.ui.listeners.OnForegroundActionListener;

/**
 * Created by shane on 28/01/18.
 */

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

    private UIHelper.ImageRenderTypes mBackgroundImageRenderTypeOff;
    private UIHelper.ImageRenderTypes mBackgroundImageRenderTypeOn;

    private BackgroundSelector mBackgroundSelectorOn;
    private ForegroundSelector mForgroundSelectorOn;

    private int mBackgroundImagePaddingOff;
    private int mBackgroundImagePaddingOn;

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

    public UIHelper.ImageRenderTypes getBackgroundImageRenderTypeOff() {
        return mBackgroundImageRenderTypeOff;
    }

    public void setBackgroundImageRenderTypeOff(UIHelper.ImageRenderTypes backgroundImageRenderType) {
        mBackgroundImageRenderTypeOff = backgroundImageRenderType;
    }

    public UIHelper.ImageRenderTypes getBackgroundImageRenderTypeOn() {
        return mBackgroundImageRenderTypeOn;
    }

    public void setBackgroundImageRenderTypeOn(UIHelper.ImageRenderTypes backgroundImageRenderTypeOn) {
        mBackgroundImageRenderTypeOn = backgroundImageRenderTypeOn;
    }

    public int getBackgroundImagePaddingOff() {
        return mBackgroundImagePaddingOff;
    }

    public void setBackgroundImagePaddingOff(int mPadding) {
        this.mBackgroundImagePaddingOff = mPadding;
    }

    public int getForegroundImagePaddingOn() {
        return mBackgroundImagePaddingOn;
    }

    public void setForegroundImagePaddingOn(int mForegroundImagePadding) {
        this.mBackgroundImagePaddingOn = mForegroundImagePadding;
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

        mSwiper.addView("Background Off", R.id.prop_sw_bg_tab_bgclr_off);
        mSwiper.addView("Foreground Off", R.id.prop_bg_sw_tab_fgclr_off);
        mSwiper.addView("Background On", R.id.prop_bg_sw_tab_bgclr_on);
        mSwiper.addView("Foreground On", R.id.prop_bg_sw_tab_fgclr_on);

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

            @Override
            public void OnImageRenderTypeChanged(UIHelper.ImageRenderTypes imageRenderType) {
                mBackgroundImageRenderTypeOff = imageRenderType;
            }

            @Override
            public void OnPaddingChanged(int padding) {
                mBackgroundImagePaddingOff = padding;
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

            @Override
            public void OnImageRenderTypeChanged(UIHelper.ImageRenderTypes imageRenderType) {
                mBackgroundImageRenderTypeOn = imageRenderType;
            }

            @Override
            public void OnPaddingChanged(int padding) {
                mBackgroundImagePaddingOn = padding;
            }
        });

        mForgroundSelectorOn.setOnForegroundActionListener(new OnForegroundActionListener() {
            @Override
            public void OnColourChange(int colour) {
                mForegroundColourOn = colour;
            }
        });
    }

    private void doPropertyChange() {
        mBackgroundSelectorOff.setInitialValues(mBackgroundColourOff, mBackgroundColourTransparencyOff, mBackgroundImageOff, mBackgroundImageTransparencyOff, mBackgroundImagePaddingOff, mBackgroundImageRenderTypeOff);
        mForgroundSelectorOff.setColour(mForegroundColourOff);
        mBackgroundSelectorOn.setInitialValues(mBackgroundColourOn, mBackgroundColourTransparencyOn, mBackgroundImageOn, mBackgroundImageTransparencyOn, mBackgroundImagePaddingOn, mBackgroundImageRenderTypeOn);
        mForgroundSelectorOn.setColour(mForegroundColourOn);

        invalidate();
        requestLayout();
    }

    public void initialise(SwitchBlock block) {
        mBackgroundColourOff = block.getBackgroundColour();
        mBackgroundColourTransparencyOff = block.getBackgroundColourTransparency();
        mBackgroundImageOff = block.getBackgroundImage();
        mBackgroundImageTransparencyOff = block.getBackgroundImageTransparency();
        mBackgroundImageRenderTypeOff = block.getBackgroundImageRenderType();

        mForegroundColourOff = block.getForegroundColour();

        mBackgroundColourOn = block.getBackgroundColourOn();
        mBackgroundColourTransparencyOn = block.getBackgroundColourTransparencyOn();
        mBackgroundImageOn = block.getBackgroundImageOn();
        mBackgroundImageTransparencyOn = block.getBackgroundImageTransparencyOn();
        mBackgroundImageRenderTypeOn = block.getBackgroundImageRenderTypeOn();

        mForegroundColourOn = block.getForegroundColourOn();

        mBackgroundImagePaddingOff = block.getBackgroundImagePadding();
        mBackgroundImagePaddingOn = block.getBackgroundImagePaddingOn();

        doPropertyChange();
    }

    public void applyTemplate(Template template)
    {
        initialise(template.getBlock(SwitchBlock.class));
    }

    public void populate(SwitchBlock block) {
        block.setBackgroundColour(mBackgroundColourOff);
        block.setBackgroundColourTransparency(mBackgroundColourTransparencyOff);
        block.setBackgroundImage(mBackgroundImageOff);
        block.setBackgroundImageTransparency(mBackgroundImageTransparencyOff);
        block.setBackgroundImageRenderType(mBackgroundImageRenderTypeOff);

        block.setForegroundColour(mForegroundColourOff);

        block.setBackgroundColourOn(mBackgroundColourOn);
        block.setBackgroundColourTransparencyOn(mBackgroundColourTransparencyOn);
        block.setBackgroundImageOn(mBackgroundImageOn);
        block.setBackgroundImageTransparencyOn(mBackgroundImageTransparencyOn);
        block.setBackgroundImageRenderTypeOn(mBackgroundImageRenderTypeOn);

        block.setForegroundColourOn(mForegroundColourOn);

        block.setBackgroundImagePadding(mBackgroundImagePaddingOff);
        block.setBackgroundImagePaddingOn(mBackgroundImagePaddingOn);

    }
}

