package shane.pennihome.local.smartboard.services.PhilipsHue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;

/**
 * Created by SPennicott on 02/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class HueBridgeLinkDialog extends DialogFragment {
    private TextView mTxtDescription;
    private String mPreText;
    private OnProcessCompleteListener<TextView> mOnLoadCompleteListener;
    private View.OnClickListener mOnCancelClickListener;

    public void setOnLoadCompleteListener(OnProcessCompleteListener<TextView> onloadcompletelistener) {
        this.mOnLoadCompleteListener = onloadcompletelistener;
    }

    private View.OnClickListener getOnCancelClickListener() {
        return mOnCancelClickListener;
    }

    public void setOnCancelClickListener(View.OnClickListener oncancelclicklistener) {
        this.mOnCancelClickListener = oncancelclicklistener;
    }

    public TextView getDescription() {
        return mTxtDescription;
    }
    public void setDescription(String msg) {
        if(mTxtDescription == null)
            mPreText = msg;
        else
        this.mTxtDescription.setText(msg);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.philip_hue_link, container, false);
        mTxtDescription = view.findViewById(R.id.ph_link_desc);
        if(!TextUtils.isEmpty(mPreText))
            mTxtDescription.setText(mPreText);

        ImageView img =  view.findViewById(R.id.ph_link_btn);
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce_animate);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);

        img.startAnimation(animation);

        Button btnCancel = view.findViewById(R.id.ph_cnl_btn);
        btnCancel.setOnClickListener(getOnCancelClickListener());

        if(mOnLoadCompleteListener !=null)
            mOnLoadCompleteListener.complete(true, mTxtDescription);
        return view;
    }
}
