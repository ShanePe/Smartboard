package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.ui.listeners.OnForegroundActionListener;

/**
 * Created by shane on 27/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ForegroundSelector extends LinearLayoutCompat {
    private int mBtnHeight;
    private Button mColourBtn;
    @ColorInt
    private
    int mColour;
    private OnForegroundActionListener mOnForegroundActionListener;

    public ForegroundSelector(Context context) {
        super(context);
        initializeViews(context);
    }

    public ForegroundSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public ForegroundSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public void setOnForegroundActionListener(OnForegroundActionListener onForegroundActionListener) {
        mOnForegroundActionListener = onForegroundActionListener;
    }

    public int getColour() {
        return mColour;
    }

    public void setColour(int colour) {
        this.mColour = colour;
        doPropertyChange();
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_foreground_selector, this);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundSelector, 0, 0);
        mBtnHeight = a.getInt(R.styleable.ForegroundSelector_blockheight, 100);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mColourBtn = this.findViewById(R.id.cfv_btn_fg);
        mColourBtn.getLayoutParams().height = mBtnHeight;

        final Context context = getContext();

        mColourBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showColourPicker(context, mColour, new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (success) {
                            mColour = (int) source;
                            if (mOnForegroundActionListener != null)
                                mOnForegroundActionListener.OnColourChange(mColour);
                            setColour(mColour);
                        }
                    }
                });
            }
        });

        doPropertyChange();
    }

    private void doPropertyChange() {
        mColourBtn.setBackground(UIHelper.getButtonShape(mColour));
        invalidate();
        requestLayout();
    }
}
