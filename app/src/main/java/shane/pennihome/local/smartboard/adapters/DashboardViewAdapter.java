package shane.pennihome.local.smartboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Dashboard} and makes a call to the
 * specified {@link DashboardFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DashboardViewAdapter extends RecyclerView.Adapter<DashboardViewAdapter.ViewHolder>
        implements DraggableItemAdapter<DashboardViewAdapter.ViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private Dashboards dashboards;
    private DashboardFragment mDashFrag;

    public DashboardViewAdapter(DashboardFragment fragment, Dashboards items, DashboardFragment.OnListFragmentInteractionListener listener) {
        dashboards = items;
        mListener = listener;
        mDashFrag = fragment;
        setHasStableIds(true);
    }

    public void setDashboards(Dashboards values) {
        dashboards = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = dashboards.get(position);
        //holder.mIdView.setText(dashboards.get(position).id);
        holder.mNameView.setText(dashboards.get(position).getName());

        final int dragState = holder.getDragStateFlags();

        int bgResId = 0;

        if (((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                //DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }
        }

        holder.mView.setBackgroundResource(bgResId);

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return dashboards.get(position).getOrderId();
    }

    @Override
    public int getItemCount() {
        return dashboards.size();
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            dashboards.moveItem(fromPosition, toPosition);
        } else {
            dashboards.swapItem(fromPosition, toPosition);
        }
        mDashFrag.saveDashboards(dashboards);
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

    public class ViewHolder extends AbstractDraggableItemViewHolder {
        final View mView;
        final TextView mNameView;
        Dashboard mItem;
        FrameLayout mContainer;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.dash_list_name);
            mContainer = view.findViewById(R.id.dashboard_list_block);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    private interface Draggable extends DraggableItemConstants {
    }
}
