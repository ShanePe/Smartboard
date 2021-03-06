package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;
import shane.pennihome.local.smartboard.ui.listeners.OnForegroundActionListener;

/**
 * Created by shane on 27/01/18.
 */

public class ThingPropertiesClrSelector extends LinearLayoutCompat {
    private String mBackgroundImage;
    private int mBackgroundImageTransparency;
    @ColorInt
    private int mBackgroundColour;
    private int mBackgroundColourTransparency;
    @ColorInt
    private int mForegroundColour;
    private UIHelper.ImageRenderTypes mBackgroundImageRenderType;

    private BackgroundSelector mBackgroundSelector;
    private ForegroundSelector mForgroundSelector;

    private int mBackgroundImagePadding;

    public ThingPropertiesClrSelector(Context context) {
        super(context);
        initializeViews(context);
    }

    public ThingPropertiesClrSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ThingPropertiesClrSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public String getBackgroundImage() {
        return mBackgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    public int getBackgroundImageTransparency() {
        return mBackgroundImageTransparency;
    }

    public void setBackgroundImageTransparency(int backgroundImageTransparency) {
        mBackgroundImageTransparency = backgroundImageTransparency;
    }

    @ColorInt
    public int getBackgroundColour() {
        return mBackgroundColour;
    }

    public void setBackgroundColour(@ColorInt int backgroundColour) {
        mBackgroundColour = backgroundColour;
    }

    @ColorInt
    public int getForegroundColour() {
        return mForegroundColour;
    }

    public void setForegroundColour(@ColorInt int foregroundColour) {
        mForegroundColour = foregroundColour;
    }

    public int getBackgroundColourTransparency() {
        return mBackgroundColourTransparency;
    }

    public void setBackgroundColourTransparency(int backgroundColourTransparency) {
        mBackgroundColourTransparency = backgroundColourTransparency;
    }

    public UIHelper.ImageRenderTypes getBackgroundImageRenderType() {
        return mBackgroundImageRenderType;
    }

    public void setBackgroundImageRenderType(UIHelper.ImageRenderTypes backgroundImageRenderType) {
        mBackgroundImageRenderType = backgroundImageRenderType;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_thing_bg_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewSwiper mSwiper = this.findViewById(R.id.prop_bg_swiper);
        TabLayout mTabLayout = this.findViewById(R.id.prop_bg_tabs);

        mSwiper.setTabLayout(mTabLayout);
        mSwiper.getViewAdapter().addView("Background", R.id.prop_bg_tab_bgclr);
        mSwiper.getViewAdapter().addView("Foreground", R.id.prop_bg_tab_fgclr);

        mBackgroundSelector = this.findViewById(R.id.prop_bg_clr);
        mForgroundSelector = this.findViewById(R.id.prop_fg_clr);

        mBackgroundSelector.setBackgroundActionListener(new OnBackgroundActionListener() {
            @Override
            public void OnColourSelected(int colour) {
                mBackgroundColour = colour;
            }

            @Override
            public void OnColourTransparencyChanged(int transparent) {
                mBackgroundColourTransparency = transparent;
            }

            @Override
            public void OnImageTransparencyChanged(int transparent) {
                mBackgroundImageTransparency = transparent;
            }

            @Override
            public void OnImageSelected(String imageFile) {
                mBackgroundImage = imageFile;
            }

            @Override
            public void OnImageRenderTypeChanged(UIHelper.ImageRenderTypes imageRenderType) {
                mBackgroundImageRenderType = imageRenderType;
            }

            @Override
            public void OnPaddingChanged(int padding) {
                mBackgroundImagePadding = padding;
            }
        });

        mForgroundSelector.setOnForegroundActionListener(new OnForegroundActionListener() {
            @Override
            public void OnColourChange(int colour) {
                mForegroundColour = colour;
            }
        });


        doPropertyChange();
    }

    private void doPropertyChange() {
        mBackgroundSelector.setInitialValues(mBackgroundColour, mBackgroundColourTransparency, mBackgroundImage, mBackgroundImageTransparency, mBackgroundImagePadding, mBackgroundImageRenderType);
        mForgroundSelector.setColour(mForegroundColour);
        invalidate();
        requestLayout();
    }

    public void initialise(IBlock block) {
        mBackgroundColour = block.getBackgroundColour();
        mBackgroundColourTransparency = block.getBackgroundColourTransparency();
        mBackgroundImage = block.getBackgroundImage();
        mBackgroundImageTransparency = block.getBackgroundImageTransparency();

        mForegroundColour = block.getForegroundColour();
        mBackgroundImageRenderType = block.getBackgroundImageRenderType();

        mBackgroundImagePadding = block.getBackgroundImagePadding();
        doPropertyChange();
    }

    public void populate(IBlock block) {
        block.setBackgroundColour(mBackgroundColour);
        block.setBackgroundColourTransparency(mBackgroundColourTransparency);
        block.setBackgroundImage(mBackgroundImage);
        block.setBackgroundImageTransparency(mBackgroundImageTransparency);
        block.setBackgroundImageRenderType(mBackgroundImageRenderType);
        block.setBackgroundImagePadding(mBackgroundImagePadding);
        block.setForegroundColour(mForegroundColour);
    }

    public void applyTemplate(Template template) {
        initialise(template.getBlock());
    }
}
