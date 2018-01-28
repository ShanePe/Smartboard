package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;
import shane.pennihome.local.smartboard.ui.listeners.OnForegroundActionListener;

/**
 * Created by shane on 27/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ThingBackground extends LinearLayoutCompat {
    private String mBackgroundImage;
    private int mBackgroundImageTransparency;
    @ColorInt
    private int mBackgroundColour;
    private int mBackgroundColourTransparency;
    @ColorInt
    private int mForegroundColour;

    private BackgroundSelector mBackgroundSelector;
    private ForegroundSelector mForgroundSelector;

    public ThingBackground(Context context) {
        super(context);
        initializeViews(context);
    }

    public ThingBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ThingBackground(Context context, AttributeSet attrs, int defStyleAttr) {
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

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_thing_background, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewSwiper mSwiper = this.findViewById(R.id.prop_bg_swiper);
        TabLayout mTablayout = this.findViewById(R.id.prop_bg_tabs);

        mSwiper.setTabLayout(mTablayout);
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
        mBackgroundSelector.setInitialValues(mBackgroundColour, mBackgroundColourTransparency, mBackgroundImage, mBackgroundImageTransparency);
        mForgroundSelector.setColour(mForegroundColour);
        invalidate();
        requestLayout();
    }

    public void initialise(IThing thing) {
        mBackgroundColour = thing.getBlock().getBackgroundColour();
        mBackgroundColourTransparency = thing.getBlock().getBackgroundColourTransparency();
        mBackgroundImage = thing.getBlock().getBackgroundImage();
        mBackgroundImageTransparency = thing.getBlock().getBackgroundImageTransparency();

        mForegroundColour = thing.getBlock().getForeColour();

        doPropertyChange();
    }

    public void populate(IThing thing) {
        thing.getBlock().setBackgroundColour(mBackgroundColour);
        thing.getBlock().setBackgroundColourTransparency(mBackgroundColourTransparency);
        thing.getBlock().setBackgroundImage(mBackgroundImage);
        thing.getBlock().setBackgroundImageTransparency(mBackgroundImageTransparency);

        thing.getBlock().setForeColour(mForegroundColour);
    }
}
