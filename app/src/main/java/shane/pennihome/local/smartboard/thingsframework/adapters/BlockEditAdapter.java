package shane.pennihome.local.smartboard.thingsframework.adapters;

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
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.thingsframework.Blocks;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 15/01/18.
 */
@SuppressWarnings("ALL")
public class BlockEditAdapter extends RecyclerView.Adapter<IBlockUIHandler.BlockEditViewHolder>
        implements DraggableItemAdapter<IBlockUIHandler.BlockEditViewHolder> {

    private final SmartboardActivity mSmartboardActivity;
    private final Group mGroup;
    private Blocks mBlocks;

    public BlockEditAdapter(SmartboardActivity smartboardActivity, Group group, DashboardFragment.OnListFragmentInteractionListener listener) {
        mBlocks = new Blocks();
        DashboardFragment.OnListFragmentInteractionListener mListener = listener;
        mSmartboardActivity = smartboardActivity;
        mGroup = group;
        setHasStableIds(true);
    }

    private View reverseFindById(View view, int id) {
        if (view == null)
            return null;

        View v = view.findViewById(id);
        if (v != null)
            return v;
        else
            return reverseFindById((View) view.getParent(), id);
    }

    @Override
    public int getItemViewType(int position) {
        return IBlock.GetTypeID(mBlocks.get(position));
    }

    @Override
    public IBlockUIHandler.BlockEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IBlock block;
        try {
            block = IBlock.CreateByTypeID(viewType);
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(block.getUIHandler().getEditLayoutID(), parent, false);

            return block.getUIHandler().GetEditHolder(view);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final IBlockUIHandler.BlockEditViewHolder holder, final int position) {
        final int dragState = holder.getDragStateFlags();

        int bgResId = 0;

        if (((dragState & BlockEditAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & BlockEditAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            } else if ((dragState & BlockEditAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }
        }

        final IBlock block = mBlocks.get(position);
        final IBlockUIHandler handler = block.getUIHandler();
        holder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showBlockPropertyWindow(mSmartboardActivity, block, new OnBlockSetListener() {
                    @Override
                    public void OnSet(IBlock block) {
                        //mGroup.getGroupViewHandler().NotifyChanged();
                    }
                });
            }
        });

        handler.BindEditHolder(holder, bgResId);
    }

    @Override
    public long getItemId(int position) {
        return mBlocks.get(position).getPosition();
    }

    public Blocks getBlocks() {
        return this.mBlocks;
    }

    public void setBlocks(Blocks mValues) {
        this.mBlocks = mValues;
    }

    @Override
    public int getItemCount() {
        return mBlocks.size();
    }

    @Override
    public boolean onCheckCanStartDrag(IBlockUIHandler.BlockEditViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(IBlockUIHandler.BlockEditViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            getBlocks().moveItem(fromPosition, toPosition);
        } else {
            getBlocks().swapItem(fromPosition, toPosition);
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