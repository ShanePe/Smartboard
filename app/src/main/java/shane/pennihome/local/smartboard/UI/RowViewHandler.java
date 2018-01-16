package shane.pennihome.local.smartboard.UI;

import android.content.Context;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.List;

import shane.pennihome.local.smartboard.Adapters.DashboardBlockAdapter;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Fragments.DashboardFragment;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 15/01/18.
 */
public class RowViewHandler {
    private RecyclerView mRecyclerView;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private DashboardBlockAdapter mDashboardBlockAdapter;

    public RowViewHandler(Context context, View view, List<Block> blocks) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.dash_block_list_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 8);

        // drag & drop manager
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(true);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);

        // setup dragging item effects (NOTE: DraggableItemAnimator is required)
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);

        //adapter
        mDashboardBlockAdapter = new DashboardBlockAdapter(new DashboardFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Dashboard item) {

            }
        });

        mDashboardBlockAdapter.setValues(blocks);
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
