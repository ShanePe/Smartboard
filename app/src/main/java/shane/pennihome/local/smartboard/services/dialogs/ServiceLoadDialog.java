package shane.pennihome.local.smartboard.services.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.adapters.ServiceLoadAdapter;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;

/**
 * Created by shane on 30/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ServiceLoadDialog extends Dialog {
    private Services mServices;
    private RecyclerView mRecycleView;

    public ServiceLoadDialog(@NonNull Context context) {
        super(context, R.style.transparentDialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        ServiceLoadAdapter loadAdapter = new ServiceLoadAdapter();
        loadAdapter.setServices(getServices());

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.service_startup_list, null);
        mRecycleView = (RecyclerView) view;
        mRecycleView.setAdapter(loadAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        setContentView(mRecycleView);
    }

    private Services getServices() {
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
