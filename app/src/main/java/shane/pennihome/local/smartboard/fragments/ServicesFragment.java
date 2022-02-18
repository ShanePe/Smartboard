package shane.pennihome.local.smartboard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.services.adapters.ServiceAdapter;

/**
 * Created by shane on 31/01/18.
 */

public class ServicesFragment extends IFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.services_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));

            recyclerView.setAdapter(new ServiceAdapter());
        }
        return view;
    }
}
