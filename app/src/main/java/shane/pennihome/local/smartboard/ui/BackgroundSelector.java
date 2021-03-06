package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings({"unused", "EmptyMethod"})
public class BackgroundSelector extends LinearLayoutCompat {
    private SeekBar msbBGImg = null;
    private SeekBar msbBGClr = null;
    private ImageButton mBtnBGClr;
    private ImageView mPreview;
    private Spinner mRenderStyle;
    private EditText mTextPadding;
    private OnBackgroundActionListener mBackgroundActionListener;
    private Thread mRenderThread;

    @ColorInt
    private
    int mColour;
    private int mTransparency;
    private String mImage;
    private int mImageTransparency;
    private UIHelper.ImageRenderTypes mImageRenderType;
    private int mPadding;

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
        doPropertyChange(false);
    }

    public int getTransparency() {
        return mTransparency;
    }

    private void setTransparency(int transparency) {
        mTransparency = transparency;
        doPropertyChange(true);
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
        doPropertyChange(false);
    }

    public int getImageTransparency() {
        return mImageTransparency;
    }

    private void setImageTransparency(int imageTransparency) {
        mImageTransparency = imageTransparency;
        doPropertyChange(true);
    }

    public UIHelper.ImageRenderTypes getImageRenderType() {
        return mImageRenderType;
    }

    public void setImageRenderType(UIHelper.ImageRenderTypes imageRenderTypes) {
        mImageRenderType = imageRenderTypes;
        doPropertyChange(false);
    }

    public int getPadding() {
        return mPadding;
    }

    public void setPadding(int mPadding) {
        this.mPadding = mPadding;
        doPropertyChange(false);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_background_selector, this);
    }

    public void setInitialValues(@ColorInt int colour, int transparency, String image, int imageTransparency, int padding, UIHelper.ImageRenderTypes backgroundImageRenderType) {
        mColour = colour;
        mTransparency = transparency;
        mImage = image;
        mImageTransparency = imageTransparency;
        mImageRenderType = backgroundImageRenderType;
        mPadding = padding;
        mTextPadding.setText(String.valueOf(padding));
        doPropertyChange(false);
    }

    private void setColourValues(@ColorInt int colour, @SuppressWarnings("SameParameterValue") int transparency) {
        mColour = colour;
        mTransparency = transparency;

        doPropertyChange(false);
    }

    public void setImageValues(String image, @SuppressWarnings("SameParameterValue") int imageTransparency) {
        mImage = image;
        mImageTransparency = imageTransparency;

        doPropertyChange(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPreview = this.findViewById(R.id.cbv_preview);
        final ImageButton mBtnBGImg = this.findViewById(R.id.cbv_image);
        msbBGClr = this.findViewById(R.id.cbv_colour_trans);
        msbBGImg = this.findViewById(R.id.cbv_image_trans);
        mBtnBGClr = this.findViewById(R.id.cbv_colour);
        mRenderStyle = this.findViewById(R.id.cbv_render_style);
        mTextPadding = this.findViewById(R.id.cbv_pad);
        ImageButton mBtnReset = this.findViewById(R.id.cbv_reset);

        final Context context = this.getContext();
        mBtnBGClr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection rawtypes
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

        //noinspection EmptyMethod
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mBackgroundActionListener != null)
                    mBackgroundActionListener.OnImageTransparencyChanged(msbBGImg.getProgress());
                setImageTransparency(msbBGImg.getProgress());
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

        mBtnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setInitialValues(Color.TRANSPARENT, 100, null, 100, 0, UIHelper.ImageRenderTypes.Center);
                if (mBackgroundActionListener != null) {
                    mBackgroundActionListener.OnColourSelected(Color.TRANSPARENT);
                    mBackgroundActionListener.OnColourTransparencyChanged(100);
                    mBackgroundActionListener.OnImageRenderTypeChanged(UIHelper.ImageRenderTypes.Center);
                    mBackgroundActionListener.OnImageSelected(null);
                    mBackgroundActionListener.OnImageTransparencyChanged(100);
                    mBackgroundActionListener.OnPaddingChanged(0);
                }
            }
        });

        mRenderStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setImageRenderType(UIHelper.ImageRenderTypes.valueOf((String) adapterView.getItemAtPosition(i)));

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#424242"));
                if (mBackgroundActionListener != null)
                    mBackgroundActionListener.OnImageRenderTypeChanged(getImageRenderType());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setImageRenderType(UIHelper.ImageRenderTypes.Center);
            }
        });

        mPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                handleRender(true);
            }
        });

        mTextPadding.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int p = TextUtils.isEmpty(s.toString()) ? 0 : Integer.parseInt(s.toString());
                setPadding(p);
                if (mBackgroundActionListener != null)
                    mBackgroundActionListener.OnPaddingChanged(p);
            }
        });

        doPropertyChange(false);
    }

    private void doPropertyChange(final boolean delayPreview) {
        handleRender(delayPreview);

        if (mColour == 0)
            mBtnBGClr.setBackgroundResource(R.drawable.btn_round_dark);
        else
            mBtnBGClr.setBackground(UIHelper.getButtonShape(UIHelper.getColorWithAlpha(mColour, mTransparency / 100f)));

        msbBGImg.setProgress(mImageTransparency);
        msbBGClr.setProgress(mTransparency);

        mRenderStyle.setSelection(mImageRenderType == null ? 0 : mImageRenderType.ordinal());

        invalidate();
        requestLayout();
    }

    private void handleRender(final boolean delayPreview) {
        if (mRenderThread != null) {
            mRenderThread.interrupt();
            if (mRenderThread != null)
                try {
                    mRenderThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        mRenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (delayPreview)
                        Thread.sleep(1000);
                    renderPreview();
                } catch (InterruptedException ignored) {
                } finally {
                    mRenderThread = null;
                }
            }
        });

        mRenderThread.start();
    }

    private void renderPreview() {
        final int width = mPreview.getWidth();
        final int height = mPreview.getHeight();

        if (width == 0 || height == 0)
            return;

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
                mPadding,
                true,
                mImageRenderType);

        mPreview.post(new Runnable() {
            @Override
            public void run() {
                Log.i(Globals.ACTIVITY, "Render background image");
                mPreview.setImageDrawable(drawable);
            }
        });
    }

    public static class ViewAdapter extends PagerAdapter {

        @SuppressWarnings("SameReturnValue")
        @Override
        public int getCount() {
            return 0;
        }

        @SuppressWarnings("SameReturnValue")
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return false;
        }
    }
}
