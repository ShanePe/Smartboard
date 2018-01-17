package shane.pennihome.local.smartboard.UI;

import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.Adapters.DashboardBlockAdapter;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Fragments.DashboardFragment;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

/**
 * Created by shane on 15/01/18.
 */
public class GroupViewHandler {
    private RecyclerView mRecyclerView;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private DashboardBlockAdapter mDashboardBlockAdapter;

    public GroupViewHandler(SmartboardActivity smartboardActivity, View view, Group group) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.dash_block_list_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(smartboardActivity, 8);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(smartboardActivity, R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        //mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);
        mRecyclerViewDragDropManager.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP);
        //adapter
        mDashboardBlockAdapter = new DashboardBlockAdapter(smartboardActivity, group, new DashboardFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Dashboard item) {

            }
        });

        mDashboardBlockAdapter.setValues(group.getBlocks());
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(mDashboardBlockAdapter);      // wrap for dragging

        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    public DashboardBlockAdapter getDashboardBlockAdapter() {
        return mDashboardBlockAdapter;
    }
}
