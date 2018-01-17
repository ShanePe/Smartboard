package shane.pennihome.local.smartboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.adapters.DashboardViewAdapter;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.things.Routine.Routine;
import shane.pennihome.local.smartboard.things.Switch.Switch;

public class DashboardFragment extends IFragment {
    DashboardViewAdapter mDashboardAptr;

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

    public void LoadDashboard(Dashboard dashboard) {
        final MainActivity activity = (MainActivity) getActivity();
        Intent dashAdd = new Intent(getActivity(), SmartboardActivity.class);

        ArrayList<String> switches = new ArrayList<>();
        ArrayList<String> routines = new ArrayList<>();

        for (Switch s : activity.getMonitor().getDevices())
            switches.add(s.toJson());

        for (Routine r : activity.getMonitor().getRoutines())
            routines.add(r.toJson());

        if (dashboard == null)
            dashboard = new Dashboard();

        Bundle options = new Bundle();

        dashAdd.putStringArrayListExtra("devices", switches);
        dashAdd.putStringArrayListExtra("routines", routines);
        dashAdd.putExtra("dashboard", dashboard.toJson());
        startActivityForResult(dashAdd, 0, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity main = (MainActivity) getActivity();
        main.populateDashbboards();
        if (mDashboardAptr != null) {
            mDashboardAptr.setValues(main.getDashboards());
            mDashboardAptr.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    /*public List<Dashboard> getDashboards() {
        return mDashboards;
    }

    public void setDashboard(List<Dashboard> dashboards) {
        this.mDashboards = dashboards;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        final Context context = view.getContext();
        MainActivity activity = (MainActivity) getActivity();

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;
            int mColumnCount = 1;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mDashboardAptr = new DashboardViewAdapter(activity.getDashboards(), new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Dashboard item) {
                    LoadDashboard(item);
                }
            });

            recyclerView.setAdapter(mDashboardAptr);
        }

        return view;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Dashboard item);
    }
}
