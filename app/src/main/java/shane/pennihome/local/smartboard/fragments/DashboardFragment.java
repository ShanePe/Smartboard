package shane.pennihome.local.smartboard.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.adapters.DashboardEditAdapter;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;

public class DashboardFragment extends IFragment {
    private DashboardEditAdapter mDashboardAptr;

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

    @SuppressWarnings("unused")
    public void saveDashboardsPosition(final Dashboards dashboards) {
        DBEngine db = new DBEngine(getActivity());
        for (int i = 0; i < dashboards.size(); i++) {
            Dashboard d = dashboards.get(i);
            d.setPosition(i + 1);
            db.updatePosition(d);
        }
    }

    private void LoadDashboard(Dashboard dashboard) {
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.stopScreenFadeOutMonitor();

        Intent dashAdd = new Intent(getActivity(), SmartboardActivity.class);

        if (dashboard == null)
            dashboard = new Dashboard();

        Bundle options = new Bundle();

        saveDashboardsPosition(mDashboardAptr.getDashboards());
        dashAdd.putExtra("dashboard_view_group_list", dashboard.toJson());
        startActivityForResult(dashAdd, 0, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        main.populateDashbboards();
        if (mDashboardAptr != null) {
            mDashboardAptr.setDashboards(main.getDashboards());
            mDashboardAptr.notifyDataSetChanged();
        }

        main.startScreenFadeOutMonitor();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        MainActivity activity = (MainActivity) getActivity();

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;

            RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

            dragMgr.setInitiateOnMove(false);
            dragMgr.setInitiateOnLongPress(true);
            dragMgr.setLongPressTimeout(750);

            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

            assert activity != null;
            recyclerView.setAdapter(dragMgr.createWrappedAdapter(mDashboardAptr = new DashboardEditAdapter(activity.getDashboards(), new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Dashboard item) {
                    LoadDashboard(item);
                }
            })));

            dragMgr.attachRecyclerView(recyclerView);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        saveDashboardsPosition(mDashboardAptr.getDashboards());
        super.onDestroyView();
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Dashboard item);
    }
}
