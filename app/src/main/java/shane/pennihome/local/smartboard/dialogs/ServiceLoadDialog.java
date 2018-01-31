package shane.pennihome.local.smartboard.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

    public Services getServices() {
        return mServices;
    }

    public void setServices(Services services) {
        mServices = services;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_startup_list, container, false);
        mRecycleView = view.findViewById(R.id.sl_list);
        ServiceLoadAdapter loadAdapter = new ServiceLoadAdapter();
        loadAdapter.setServices(getServices());
        return view;
    }

    public void setGetterSuccess(IThingsGetter getter) {
        ServiceLoadAdapter.ViewHolder viewHolder = findViewHolder(getter);
        if (viewHolder == null)
            return;
        viewHolder.slideOut();

    }

    private ServiceLoadAdapter.ViewHolder findViewHolder(IThingsGetter getter) {
        for (int i = 0; i < mRecycleView.getChildCount(); i++) {
            ServiceLoadAdapter.ViewHolder vh = (ServiceLoadAdapter.ViewHolder) mRecycleView.findViewHolderForAdapterPosition(i);
            if (vh.getThingGetter().equals(getter))
                return vh;
        }

        return null;
    }
}
