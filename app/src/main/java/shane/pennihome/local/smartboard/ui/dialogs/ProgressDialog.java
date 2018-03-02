package shane.pennihome.local.smartboard.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Globals;

/**
 * Created by SPennicott on 06/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgressDialog extends DialogFragment {
    private TextView mMsgText;
    private String mMessage = "";
    private String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
        if(mMsgText != null)
            mMsgText.setText(message);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_progress, null);
        mMsgText = view.findViewById(R.id.pd_text);
        if(!TextUtils.isEmpty(getMessage()))
            setMessage(getMessage());
        return view;
    }

    public void show(Context context)
    {
        if(!isAdded()) {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            if (fragmentManager != null)
                this.show(fragmentManager, Globals.ACTIVITY);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return d;
    }
}
