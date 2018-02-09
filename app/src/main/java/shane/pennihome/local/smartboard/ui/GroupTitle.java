package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class GroupTitle extends LinearLayoutCompat {
    private TextView mTitleTextView;
    private String mTitle;
    private LinearLayoutCompat mContainer;

    public GroupTitle(Context context) {
        super(context);
        initializeViews(context);
    }

    public GroupTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public GroupTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
        handleAttrs(context, attrs);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        mTitleTextView.setText(title);
        invalidate();
        requestLayout();
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_group_title, this);
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GroupTitle, 0, 0);
        mTitle = a.getString(R.styleable.GroupTitle_title);
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleTextView = this.findViewById(R.id.txt_group_heading);
        if (mTitle != null)
            mTitleTextView.setText(mTitle);
        mContainer = this.findViewById(R.id.txt_group_container);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        mContainer.setVisibility(visibility);
    }
}
