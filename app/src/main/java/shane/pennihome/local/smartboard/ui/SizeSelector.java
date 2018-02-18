package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.listeners.OnSizeActionListener;

/**
 * Created by shane on 18/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SizeSelector extends GridLayout {
    private ImageView m1x1;
    private ImageView m2x1;
    private ImageView m1x2;
    private ImageView m2x2;
    private TextView mDescription;

    private int mBlockWidth = 1;
    private int mBlockHeight = 1;
    private OnSizeActionListener mOnSizeActionListener;

    public SizeSelector(Context context) {
        super(context);
        initialiseViews(context);
    }

    public SizeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseViews(context);
    }

    public SizeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseViews(context);
    }

    public OnSizeActionListener getOnSizeActionListener() {
        return mOnSizeActionListener;
    }

    public void setOnSizeActionListener(OnSizeActionListener onSizeActionListener) {
        mOnSizeActionListener = onSizeActionListener;
    }

    private int getBlockWidth() {
        return mBlockWidth;
    }

    public void setBlockWidth(int blockWidth) {
        mBlockWidth = blockWidth;
        doPropertyChange();
    }

    private int getBlockHeight() {
        return mBlockHeight;
    }

    public void setBlockHeight(int blockHeight) {
        mBlockHeight = blockHeight;
        doPropertyChange();
    }

    public void setSize(int blockWidth, int blockHeight) {
        mBlockWidth = blockWidth;
        mBlockHeight = blockHeight;
        doPropertyChange();
    }

    private void initialiseViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_size_picker, this);
    }

    public void initialise(IBlock block) {
        mBlockWidth = block.getWidth();
        mBlockHeight = block.getHeight();
        doPropertyChange();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        m1x1 = this.findViewById(R.id.css_1x1);
        m1x2 = this.findViewById(R.id.css_1x2);
        m2x1 = this.findViewById(R.id.css_2x1);
        m2x2 = this.findViewById(R.id.css_2x2);
        mDescription = this.findViewById(R.id.css_desc);

        m1x1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSize(1, 1);
                if (mOnSizeActionListener != null) {
                    mOnSizeActionListener.onWidthChanged(getBlockWidth());
                    mOnSizeActionListener.onHeightChanged(getBlockHeight());
                }
            }
        });

        m1x2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSize(1, 2);
                if (mOnSizeActionListener != null) {
                    mOnSizeActionListener.onWidthChanged(getBlockWidth());
                    mOnSizeActionListener.onHeightChanged(getBlockHeight());
                }
            }
        });

        m2x1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSize(2, 1);
                if (mOnSizeActionListener != null) {
                    mOnSizeActionListener.onWidthChanged(getBlockWidth());
                    mOnSizeActionListener.onHeightChanged(getBlockHeight());
                }
            }
        });

        m2x2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSize(2, 2);
                if (mOnSizeActionListener != null) {
                    mOnSizeActionListener.onWidthChanged(getBlockWidth());
                    mOnSizeActionListener.onHeightChanged(getBlockHeight());
                }
            }
        });
        doPropertyChange();
    }

    private void doPropertyChange() {
        if (m1x1 == null)
            return;

        m1x1.setSelected(false);
        m1x2.setSelected(false);
        m2x1.setSelected(false);
        m2x2.setSelected(false);

        if (getBlockWidth() == 1 && getBlockHeight() == 1) {
            m1x1.setSelected(true);
        } else if (getBlockWidth() == 1 && getBlockHeight() == 2) {
            m1x1.setSelected(true);
            m1x2.setSelected(true);
        } else if (getBlockWidth() == 2 && getBlockHeight() == 1) {
            m1x1.setSelected(true);
            m2x1.setSelected(true);
        } else if (getBlockWidth() == 2 && getBlockHeight() == 2) {
            m1x1.setSelected(true);
            m2x1.setSelected(true);
            m1x2.setSelected(true);
            m2x2.setSelected(true);
        }

        mDescription.setText(String.format("%s by %s", getBlockWidth(), getBlockHeight()));
    }

    public void applyTemplate(Template template) {
        setSize(template.getBlock().getWidth(), template.getBlock().getHeight());
    }

    public void populate(IBlock block) {
        block.setWidth(getBlockWidth());
        block.setHeight(getBlockHeight());
    }
}
