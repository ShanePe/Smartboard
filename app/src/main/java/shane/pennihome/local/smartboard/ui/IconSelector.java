package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Globals;
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

    public String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconpath) {
        this.mIconPath = iconpath;
        doPropertyChange();
    }

    public OnIconActionListener getOnIconActionListener() {
        return mOnIconActionListener;
    }

    public void setOnIconActionListener(OnIconActionListener oniconactionlistener) {
        this.mOnIconActionListener = oniconactionlistener;
    }

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

    private void initialiseView(Context context)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_icon_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mIconPreview = findViewById(R.id.cis_preview);
        //ImageButton btnIcon = findViewById(R.id.cis_icon);
        //ImageButton btnReset = findViewById(R.id.cis_reset);

        mIconPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IconSelectDialog iconSelectDialog = new IconSelectDialog();
                iconSelectDialog.setOnIconActionListener(new OnIconActionListener() {
                    @Override
                    public void OnIconSelected(String iconPath) {
                    if(mOnIconActionListener!=null)
                        mOnIconActionListener.OnIconSelected(iconPath);

                    setIconPath(iconPath);

                    }
                });
                iconSelectDialog.setIconPath(getIconPath());
                AppCompatActivity activity = (AppCompatActivity)getContext();
                iconSelectDialog.show(activity.getSupportFragmentManager(), Globals.ACTIVITY);
            }
        });

        doPropertyChange();
    }

    private void doPropertyChange()
    {
        if(mIconPreview == null)
            return;

        try {
            if(TextUtils.isEmpty(mIconPath))
            {
                mIconPreview.setImageResource(R.drawable.icon_tap_to_add);
            }
            else {
                InputStream stream = getContext().getAssets().open(getIconPath());
                mIconPreview.setImageDrawable(Drawable.createFromStream(stream, getIconPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
