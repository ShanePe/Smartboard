package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class BackgroundSelector extends LinearLayoutCompat {
    private Button mBtnBGImg = null;
    private SeekBar msbBGImg = null;
    private Button mBtnBGClr = null;
    private SeekBar msbBGClr = null;
    private ViewSwiper mViewSwiper = null;
    private OnBackgroundActionListener mBackgroundActionListener;
    private Thread mRenderThread;
//    private int mBtnHeight;

    @ColorInt
    private
    int mColour;
    private int mTransparency;
    private String mImage;
    private int mImageTransparency;

    public BackgroundSelector(Context context) {
        super(context);
        initializeViews(context);
    }

    public BackgroundSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public BackgroundSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public void setBackgroundActionListener(OnBackgroundActionListener backgroundActionListener) {
        mBackgroundActionListener = backgroundActionListener;
    }

    public int getColour() {
        return mColour;
    }

    public void setColour(int colour) {
        mColour = colour;
        doPropertyChange();
    }

    public int getTransparency() {
        return mTransparency;
    }

    private void setTransparency(int transparency) {
        mTransparency = transparency;
        doPropertyChange();
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
        doPropertyChange();
    }

    public int getImageTransparency() {
        return mImageTransparency;
    }

    private void setImageTransparency(int imageTransparency) {
        mImageTransparency = imageTransparency;
        doPropertyChange();
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_background_selector, this);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BackgroundSelector, 0, 0);
        //      mBtnHeight = a.getInt(R.styleable.BackgroundSelector_blockheight,100);
        a.recycle();
    }

    public void setInitialValues(@ColorInt int colour, int transparency, String image, int imageTransparency) {
        mColour = colour;
        mTransparency = transparency;
        mImage = image;
        mImageTransparency = imageTransparency;

        doPropertyChange();
    }

    private void setColourValues(@ColorInt int colour, @SuppressWarnings("SameParameterValue") int transparency) {
        mColour = colour;
        mTransparency = transparency;

        doPropertyChange();
    }

    public void setImageValues(String image, @SuppressWarnings("SameParameterValue") int imageTransparency) {
        mImage = image;
        mImageTransparency = imageTransparency;

        doPropertyChange();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewSwiper swiper = this.findViewById(R.id.cbv_switcher);
        TabLayout tabs = this.findViewById(R.id.cbv_tab);
        swiper.setTabLayout(tabs);
        swiper.getViewAdapter().addView("Colour", R.id.cbv_tab_clr);
        swiper.getViewAdapter().addView("Image", R.id.cbv_tab_img);

        mBtnBGClr = this.findViewById(R.id.cbv_btn_bg);
        msbBGClr = this.findViewById(R.id.cbv_sb_bg);

        mBtnBGImg = this.findViewById(R.id.cbv_btn_img);
        msbBGImg = this.findViewById(R.id.cbv_sb_img);

        //    mBtnBGClr.getLayoutParams().height = mBtnHeight;
//        mBtnBGImg.getLayoutParams().height = mBtnHeight;

        final Context context = this.getContext();
        mBtnBGClr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showColourPicker(context, mColour, new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (success) {
                            @ColorInt int clr = (int) source;
                            if (mBackgroundActionListener != null)
                                mBackgroundActionListener.OnColourSelected(clr);
                            setColourValues(clr, 100);
                        }
                    }
                });
            }
        });

        msbBGClr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mBackgroundActionListener != null)
                    mBackgroundActionListener.OnColourTransparencyChanged(i);
                setTransparency(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        msbBGImg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mBackgroundActionListener != null)
                    mBackgroundActionListener.OnImageTransparencyChanged(i);
                setImageTransparency(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        final AppCompatActivity activity = (AppCompatActivity) this.getContext();

        mBtnBGImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showImageImport(activity.getSupportFragmentManager(), new OnProcessCompleteListener<String>() {
                    @Override
                    public void complete(boolean success, String source) {
                        if (mBackgroundActionListener != null)
                            mBackgroundActionListener.OnImageSelected(source);
                        setImageValues(source, 100);
                    }
                });
            }
        });

        doPropertyChange();
    }

    private void doPropertyChange() {
        if (mImage != null) {
            if (mRenderThread != null) {
                mRenderThread.interrupt();
                mRenderThread = null;
            }

            mRenderThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        renderPreview();
                    } catch (InterruptedException ignored) {
                    }
                }
            });

            mRenderThread.start();
        }

        mBtnBGClr.setBackground(UIHelper.getButtonShape(UIHelper.getColorWithAlpha(mColour, mTransparency / 100f)));

        msbBGImg.setProgress(mImageTransparency);
        msbBGClr.setProgress(mTransparency);

        invalidate();
        requestLayout();
    }

    private void renderPreview() {
        if (mBtnBGImg == null)
            return;

        if (mBtnBGImg.getLayout() == null)
            return;

        final int width = mBtnBGImg.getLayout().getWidth();
        final int height = mBtnBGImg.getLayout().getHeight();

        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(mImage))
            bitmap = BitmapFactory.decodeFile(mImage);

        final Drawable drawable = UIHelper.generateImage(getContext(),
                mColour,
                mTransparency,
                bitmap,
                mImageTransparency,
                width,
                height,
                true);

        mBtnBGImg.post(new Runnable() {
            @Override
            public void run() {
                mBtnBGImg.setBackground(drawable);
            }
        });
    }

    public class ViewAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return false;
        }
    }
}
