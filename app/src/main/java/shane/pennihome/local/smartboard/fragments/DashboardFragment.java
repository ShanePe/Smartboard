package shane.pennihome.local.smartboard.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.adapters.DashboardViewAdapter;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;

public class DashboardFragment extends IFragment {
    private DashboardViewAdapter mDashboardAptr;

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
    public void saveDashboards(final Dashboards dashboards)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBEngine db = new DBEngine(getActivity());
                for(Dashboard d: dashboards)
                    db.WriteToDatabase(d);
            }
        });
    }

    private void LoadDashboard(Dashboard dashboard) {
        Intent dashAdd = new Intent(getActivity(), SmartboardActivity.class);

        if (dashboard == null)
            dashboard = new Dashboard();

        Bundle options = new Bundle();

        dashAdd.putExtra("dashboard", dashboard.toJson());
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        final Context context = view.getContext();
        MainActivity activity = (MainActivity) getActivity();



        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;

            RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

            dragMgr.setInitiateOnMove(false);
            dragMgr.setInitiateOnLongPress(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            recyclerView.setAdapter(dragMgr.createWrappedAdapter( mDashboardAptr = new DashboardViewAdapter(this, activity.getDashboards(), new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(Dashboard item) {
                    LoadDashboard(item);
                }
            })));

            dragMgr.attachRecyclerView(recyclerView);

            /*recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
            assert activity != null;

            // drag & drop manager
            RecyclerViewDragDropManager mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
            //noinspection ConstantConditions
            mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                    (NinePatchDrawable) ContextCompat.getDrawable(getActivity(), R.drawable.material_shadow_z3));
            // Start dragging after long press
            mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
            mRecyclerViewDragDropManager.setInitiateOnMove(false);
            mRecyclerViewDragDropManager.setLongPressTimeout(750);

            // setup dragging item effects (NOTE: DraggableItemAnimator is required)
            mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
            mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
            mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
            //mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);
            mRecyclerViewDragDropManager.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT);

            //adapter

            RecyclerView.Adapter mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mDashboardAptr);

            GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

            recyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
            recyclerView.setItemAnimator(animator);

            mRecyclerViewDragDropManager.attachRecyclerView(recyclerView);
            */
        }

        return view;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Dashboard item);
    }
}
