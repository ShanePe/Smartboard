package shane.pennihome.local.smartboard.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Adapters.DashboardViewAdapter;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

public class DashboardFragment extends Fragment {

    private List<Dashboard> mDashboard = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DashboardFragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_add_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add)
            LoadDashboard(null);

        return super.onOptionsItemSelected(item);
    }

    public void LoadDashboard(Dashboard dashboard)
    {
        final MainActivity activity = (MainActivity) getActivity();
        Intent dashAdd = new Intent(getActivity(), SmartboardActivity.class);

        ArrayList<String> devices = new ArrayList<>();
        ArrayList<String> routines = new ArrayList<>();

        for (Device d : activity.getMonitor().getDevices())
            devices.add(d.toJson());

        for (Routine r : activity.getMonitor().getRoutines())
            routines.add(r.toJson());

        if(dashboard == null)
            dashboard = new Dashboard();

        dashAdd.putStringArrayListExtra("devices", devices);
        dashAdd.putStringArrayListExtra("routines", routines);
        dashAdd.putExtra("dashboard", dashboard.toJson());
        startActivity(dashAdd);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null)
                actionBar.show();
        }
        setHasOptionsMenu(true);
    }

    public List<Dashboard> getDashboards() {
        return mDashboard;
    }

    public void setDashboard(List<Dashboard> dashboards) {
        this.mDashboard = dashboards;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        final Context context = view.getContext();
        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;
            int mColumnCount = 1;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new DashboardViewAdapter(mDashboard, new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Dashboard item) {
                    LoadDashboard(item);
                }
            }));
        }

        return view;
    }

     public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Dashboard item);
    }
}
