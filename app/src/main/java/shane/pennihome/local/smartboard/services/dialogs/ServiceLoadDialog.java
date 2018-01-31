package shane.pennihome.local.smartboard.services.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.adapters.ServiceLoadAdapter;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;

/**
 * Created by shane on 30/01/18.
 */

public class ServiceLoadDialog extends DialogFragment {
    Services mServices;
    RecyclerView mRecycleView;

   @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.service_startup_list, null);

        mRecycleView = (RecyclerView) view;
        ServiceLoadAdapter loadAdapter = new ServiceLoadAdapter();
        loadAdapter.setServices(getServices());
        mRecycleView.setAdapter(loadAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        return new AlertDialog.Builder(getContext(),R.style.transparentDialog)
                .setView(view)
                .create();
    }

    public static ServiceLoadDialog newInstance(Services services) {
        ServiceLoadDialog frag = new ServiceLoadDialog();
        frag.setServices(services);
        return frag;
    }

    public Services getServices() {
        return mServices;
    }

    public void setServices(Services services) {
        mServices = services;
    }

    public void setGetterSuccess(IThingsGetter getter) {
        ServiceLoadAdapter.ViewHolder viewHolder = findViewHolder(getter);
        if (viewHolder == null)
            return;
        viewHolder.slideOut();
    }

    private ServiceLoadAdapter.ViewHolder findViewHolder(IThingsGetter getter) {
        if(mRecycleView == null)
            return null;

        for (int i = 0; i < mRecycleView.getChildCount(); i++) {
            ServiceLoadAdapter.ViewHolder vh = (ServiceLoadAdapter.ViewHolder) mRecycleView.findViewHolderForAdapterPosition(i);
            if (vh.getThingGetter().getUniqueId() == getter.getUniqueId())
                return vh;
        }

        return null;
    }
}
