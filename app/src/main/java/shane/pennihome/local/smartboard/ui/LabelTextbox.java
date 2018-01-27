package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class LabelTextbox extends LinearLayoutCompat {
    private TextView mLabelTextView;
    private EditText mTextBoxEditView;
    private String mLabel;
    private String mText;

    public LabelTextbox(Context context) {
        super(context);
        initializeViews(context);
    }

    public LabelTextbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public LabelTextbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
        doPropertyChange();
    }

    public String getText() {
        return mText;
    }

    public void setText(String value) {
        this.mText = value;
        doPropertyChange();
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_labeltext, this);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelTextbox, 0, 0);
        mLabel = a.getString(R.styleable.LabelTextbox_label);
        mText = a.getString(R.styleable.LabelTextbox_text);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mLabelTextView = this.findViewById(R.id.def_tb_lbl);
        mTextBoxEditView = this.findViewById(R.id.def_tb_txt);

        if (mLabel != null)
            mLabelTextView.setText(mLabel);
        if (mText != null)
            mTextBoxEditView.setText(mText);
    }

    private void doPropertyChange() {
        if (mLabel != null)
            mLabelTextView.setText(mLabel);
        if (mText != null)
            mTextBoxEditView.setText(mText);
        invalidate();
        requestLayout();
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mTextBoxEditView.addTextChangedListener(textWatcher);
    }
}
