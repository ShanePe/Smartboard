package shane.pennihome.local.smartboard.ui;

import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.thingsframework.adapters.BlockEditAdapter;

/**
 * Created by shane on 15/01/18.
 */
@SuppressWarnings("ALL")
public class GroupViewHandler {
    private BlockEditAdapter mBlockEditAdapter;
    private SmartboardActivity mSmartboardActivity;

    public GroupViewHandler(SmartboardActivity smartboardActivity, ViewGroup parent, View view, Group group) {
        RecyclerView recyclerView = view.findViewById(R.id.dash_block_list_rv);
        handle(smartboardActivity, parent, view, group, recyclerView);
    }

    public GroupViewHandler(SmartboardActivity smartboardActivity, ViewGroup parent, View view, Group group, RecyclerView recyclerView) {
        handle(smartboardActivity, parent, view, group, recyclerView);
    }

    private void handle(SmartboardActivity smartboardActivity, final ViewGroup parent, final View view, final Group group, RecyclerView recyclerView)
    {
        mSmartboardActivity = smartboardActivity;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(smartboardActivity, Globals.BLOCK_COLUMNS);

        RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();
        dragDropManager.setDraggingItemShadowDrawable(
                (NinePatchDrawable) ContextCompat.getDrawable(smartboardActivity, R.drawable.material_shadow_z3));

        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setLongPressTimeout(750);

        dragDropManager.setDragStartItemAnimationDuration(250);
        dragDropManager.setDraggingItemAlpha(0.8f);
        dragDropManager.setDraggingItemScale(1.3f);

        dragDropManager.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT);

        //adapter
        mBlockEditAdapter = new BlockEditAdapter(smartboardActivity, group, new DashboardFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Dashboard item) {

            }
        });

        dragDropManager.setOnCustomOnMoveListener(new RecyclerViewDragDropManager.OnCustomOnMoveListener() {
            @Override
            public void OnStartDrag(RecyclerView rv) {
                ImageButton delBtn = ((View)parent).findViewById(R.id.btn_delete_item);
                delBtn.setBackgroundResource(R.drawable.btn_round_accent);
                Animation anim = AnimationUtils.loadAnimation(mSmartboardActivity, R.anim.shake_animate);
                delBtn.startAnimation(anim);
            }

            @Override
            public void OnEndDrag(RecyclerView rv) {}

            @Override
            public void OnClick(RecyclerView rv, MotionEvent e) {}

            @Override
            public void OnMove(RecyclerView rv, MotionEvent e) {}

            @Override
            public void OnUpOrCancel(final RecyclerView rv, MotionEvent e) {
                ImageButton delBtn = ((View)parent).findViewById(R.id.btn_delete_item);
                delBtn.setBackgroundResource(R.drawable.btn_round_dark);

                final View item = rv.findChildViewUnder(e.getX(),e.getY());

                if(item == null)
                    return;

                Rect delRect = getViewRect(delBtn);
                Rect itemRect  = getViewRect(item);

                if(itemRect.contains(delRect.centerX(), delRect.centerY())) {
                    item.setVisibility(View.GONE);
                    UIHelper.showConfirm(mSmartboardActivity, "Confirm", "Are you sure you wnat to remove this block?", new OnProcessCompleteListener() {
                        @Override
                        public void complete(boolean success, Object source) {
                            item.setVisibility(View.VISIBLE);
                            if(success) {
                                int at = rv.getChildAdapterPosition(item);
                                mBlockEditAdapter.getBlocks().remove(at);
                                mSmartboardActivity.DataChanged();
                            }
                        }
                    });
                }
            }
        });

        mBlockEditAdapter.setBlocks(group.getBlocks());
        GeneralItemAnimator animator = new DraggableItemAnimator(); // DraggableItemAnimator is required to make item animations properly.

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(dragDropManager.createWrappedAdapter(mBlockEditAdapter));  // requires *wrapped* adapter
        recyclerView.setItemAnimator(animator);

        dragDropManager.attachRecyclerView(recyclerView);
    }

    private Rect getViewRect(View view)
    {
        int[] l = new int[2];
        view.getLocationOnScreen(l);
        return new Rect(l[0], l[1], l[0] + view.getWidth(), l[1] + view.getHeight());

    }

    public void NotifyChanged() {
        getDashboardBlockAdapter().notifyDataSetChanged();
        mSmartboardActivity.DataChanged();
    }

    public BlockEditAdapter getDashboardBlockAdapter() {
        return mBlockEditAdapter;
    }
}

