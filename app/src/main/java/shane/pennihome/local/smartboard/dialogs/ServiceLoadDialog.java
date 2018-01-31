package shane.pennihome.local.smartboard.dialogs;

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
import shane.pennihome.local.smartboard.adapters.ServiceLoadAdapter;
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
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setRetainInstance(true);
        setCancelable(false);
    }

    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//
//
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(view);
//        return builder.create();
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_startup_list, null);
        view.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Context context = view.getContext();

        mRecycleView = (RecyclerView) view;
        ServiceLoadAdapter loadAdapter = new ServiceLoadAdapter();
        loadAdapter.setServices(getServices());
        mRecycleView.setAdapter(loadAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(context));
        return view;
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

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.service_startup_list, container, false);
//        mRecycleView = view.findViewById(R.id.sl_list);
//        ServiceLoadAdapter loadAdapter = new ServiceLoadAdapter();
//        loadAdapter.setServices(getServices());
//        return view;
//    }

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
            if (vh.getThingGetter().getClass().equals(getter.getClass()))
                return vh;
        }

        return null;
    }
}
