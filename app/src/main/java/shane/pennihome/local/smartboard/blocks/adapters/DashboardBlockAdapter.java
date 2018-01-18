package shane.pennihome.local.smartboard.blocks.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockClickListener;
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlocks;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.interfaces.IBlockUI;

/**
 * Created by shane on 15/01/18.
 */
public class DashboardBlockAdapter extends RecyclerView.Adapter<IBlockUI.BaseEditorViewHolder>
        implements DraggableItemAdapter<IBlockUI.BaseEditorViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private IBlocks mIBlocks;
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
    private SmartboardActivity mSmartboardActivity;
    private Group mGroup;

    public DashboardBlockAdapter(SmartboardActivity smartboardActivity, Group group, DashboardFragment.OnListFragmentInteractionListener listener) {
        mIBlocks = new IBlocks();
        mListener = listener;
        mSmartboardActivity = smartboardActivity;
        mGroup = group;
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        IBlock block = mIBlocks.get(position);
        return IBlock.GetTypeID(block);
    }

    @Override
    public IBlockUI.BaseEditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IBlock block = IBlock.CreateByTypeID(viewType);
        if (block == null)
            return null;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(block.getViewResourceID(), parent, false);
        return block.getUIHandler().GetEditorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IBlockUI.BaseEditorViewHolder holder, int position) {
        final int dragState = holder.getDragStateFlags();

        int bgResId = 0;

        if (((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                //DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }
        }

        IBlock block = mIBlocks.get(position);
        IBlockUI handler = block.getUIHandler();

        handler.BindViewHolder(holder, bgResId, new OnBlockClickListener() {
            @Override
            public void OnEdit(IBlock block) {
                UIHelper.showBlockPropertyWindow(mSmartboardActivity, mSmartboardActivity.getThings(), block, new OnBlockSetListener() {
                    @Override
                    public void OnSet(IBlock block) {
                        mGroup.getGroupViewHandler().NotifyChanged();
                    }
                });
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mIBlocks.get(position).getGroupId();
    }

    public void setIBlocks(IBlocks mValues) {
        this.mIBlocks = mValues;
    }

    public IBlocks getIBlocks() {
        return this.mIBlocks;
    }

    @Override
    public int getItemCount() {
        return mIBlocks.size();
    }

    @Override
    public boolean onCheckCanStartDrag(IBlockUI.BaseEditorViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(IBlockUI.BaseEditorViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
           getIBlocks().moveItem(fromPosition, toPosition);
        } else {
            getIBlocks().swapItem(fromPosition, toPosition);
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    private interface Draggable extends DraggableItemConstants {
    }
}