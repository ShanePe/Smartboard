package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.ui.dialogs.IconSelectDialog;
import shane.pennihome.local.smartboard.ui.listeners.OnIconActionListener;

/**
 * Created by SPennicott on 06/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class IconSelector extends LinearLayoutCompat {
    private OnIconActionListener mOnIconActionListener;
    private String mIconPath;
    private ImageView mIconPreview;
    private Spinner mIconSizeSpinner;

    private UIHelper.IconSizes mIconSize;


    public IconSelector(Context context) {
        super(context);
        initialiseView(context);
    }

    public IconSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public IconSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    private String getIconPath() {
        return mIconPath;
    }

    private void setIconPath(String iconpath) {
        this.mIconPath = iconpath;
        doPropertyChange();
    }

    private UIHelper.IconSizes getIconSize() {
        return mIconSize;
    }

    private void setIconSize(UIHelper.IconSizes iconSize) {
        mIconSize = iconSize;
        doPropertyChange();
    }

    public void setIcon(String iconPath, UIHelper.IconSizes iconSize) {
        mIconPath = iconPath;
        mIconSize = iconSize;
        doPropertyChange();
    }

    public OnIconActionListener getOnIconActionListener() {
        return mOnIconActionListener;
    }

    public void setOnIconActionListener(OnIconActionListener oniconactionlistener) {
        this.mOnIconActionListener = oniconactionlistener;
    }

    private void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_icon_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mIconPreview = findViewById(R.id.cis_preview);
        mIconSizeSpinner = findViewById(R.id.cis_icon_size);

        mIconPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IconSelectDialog iconSelectDialog = new IconSelectDialog();
                iconSelectDialog.setOnIconActionListener(new OnIconActionListener() {
                    @Override
                    public void OnIconSelected(String iconPath) {
                        if (mOnIconActionListener != null)
                            mOnIconActionListener.OnIconSelected(iconPath);

                        setIconPath(iconPath);
                    }

                    @Override
                    public void OnIconSizeChanged(UIHelper.IconSizes iconSize) {
                    }
                });
                iconSelectDialog.setIconPath(getIconPath());
                AppCompatActivity activity = (AppCompatActivity) getContext();
                iconSelectDialog.show(activity.getSupportFragmentManager(), Globals.ACTIVITY);
            }
        });

        mIconSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setIconSize(UIHelper.IconSizes.valueOf((String) adapterView.getItemAtPosition(i)));
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#424242"));

                if (mOnIconActionListener != null)
                    mOnIconActionListener.OnIconSizeChanged(getIconSize());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setIconSize(UIHelper.IconSizes.Medium);
            }
        });

        doPropertyChange();
    }

    private void doPropertyChange() {
        if (mIconPreview == null)
            return;

        try {
            if (TextUtils.isEmpty(mIconPath)) {
                mIconPreview.setImageResource(R.mipmap.icon_tap_to_add_mm_fg);
            } else {
                InputStream stream = getContext().getAssets().open(getIconPath());
                mIconPreview.setImageDrawable(Drawable.createFromStream(stream, getIconPath()));
            }

            mIconSizeSpinner.setSelection(mIconSize == null ? 1 : mIconSize.ordinal());

            invalidate();
            requestLayout();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyTemplate(Template template)
    {
        mIconPath = template.getBlock(IIconBlock.class).getIcon();
        mIconSize = template.getBlock(IIconBlock.class).getIconSize();
        doPropertyChange();
    }
}
