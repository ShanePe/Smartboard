package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBackgroundActionListener;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class BackgroundSelector extends LinearLayoutCompat {
    private Button mBtnBGImg = null;
    private SeekBar msbBGImg = null;
    private Button mBtnBGClr = null;
    private SeekBar msbBGClr = null;
    private OnBackgroundActionListener mBackgroundActionListener;
    private Fragment mImageCallbackFragment;
    private Thread mRenderThread;
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
    }

    public BackgroundSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
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

    public void setImageCallbackFragment(Fragment imageCallbackFragment) {
        mImageCallbackFragment = imageCallbackFragment;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_background_selector, this);
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
        mBtnBGClr = this.findViewById(R.id.cbv_btn_bg);
        msbBGClr = this.findViewById(R.id.cbv_sb_bg);

        mBtnBGImg = this.findViewById(R.id.cbv_btn_img);
        msbBGImg = this.findViewById(R.id.cbv_sb_img);

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


        mBtnBGImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageCallbackFragment != null)
                    UIHelper.showImageImport(mImageCallbackFragment);
            }
        });

        doPropertyChange();
    }

    private void doPropertyChange() {
        //final int width = mBtnBGImg.getWidth() < 1 ? 100: (mBtnBGImg.getWidth() / 2) - 10;
        //final int height = mBtnBGImg.getHeight() < 1 ? 50: mBtnBGImg.getHeight();

        final int width = 100;
        final int height = 50;
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
                        Bitmap bitmap = null;
                        if (!TextUtils.isEmpty(mImage))
                            bitmap = BitmapFactory.decodeFile(mImage);

                        final Drawable drawable;
                        drawable = UIHelper.generateImage(getContext(),
                                mColour,
                                mTransparency,
                                bitmap,
                                mImageTransparency,
                                width,
                                height);

                        mBtnBGImg.post(new Runnable() {
                            @Override
                            public void run() {
                                mBtnBGImg.setBackground(drawable);
                            }
                        });
                    } catch (InterruptedException ignored) {
                    }
                }
            });

            mRenderThread.start();
        }

        mBtnBGClr.setBackgroundColor(UIHelper.getColorWithAlpha(mColour, mTransparency / 100f));

        msbBGImg.setProgress(mImageTransparency);
        msbBGClr.setProgress(mTransparency);

        invalidate();
        requestLayout();
    }
}
