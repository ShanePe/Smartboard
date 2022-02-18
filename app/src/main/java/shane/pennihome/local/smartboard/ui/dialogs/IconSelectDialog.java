package shane.pennihome.local.smartboard.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Objects;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.adapters.IconAdapter;
import shane.pennihome.local.smartboard.ui.listeners.OnIconActionListener;

/**
 * Created by SPennicott on 06/02/2018.
 */

public class IconSelectDialog extends DialogFragment {
    private OnIconActionListener mOnIconActionListener;
    private String mIconPath;

    private String getIconPath() {
        return mIconPath;
    }

    public void setIconPath(String iconpath) {
        this.mIconPath = iconpath;
        if (mOnIconActionListener != null)
            mOnIconActionListener.OnIconSelected(mIconPath);
    }

    public OnIconActionListener getOnIconActionListener() {
        return mOnIconActionListener;
    }

    public void setOnIconActionListener(OnIconActionListener oniconactionlistener) {
        this.mOnIconActionListener = oniconactionlistener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressWarnings("ConstantConditions")
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_icon_list, null);
        final RecyclerView recyclerView = (RecyclerView) view;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setSelected(true);

        IconAdapter iconAdapter = new IconAdapter(Objects.requireNonNull(getContext()));
        iconAdapter.setSelected(getIconPath());
        recyclerView.setAdapter(iconAdapter);

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Select Icon")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setIconPath("");
                        getDialog().dismiss();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setIconPath(((IconAdapter) recyclerView.getAdapter()).getSelected());
                        getDialog().dismiss();
                    }
                })
                .create();
    }


}
