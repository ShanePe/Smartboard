package shane.pennihome.local.smartboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Dashboard} and makes a call to the
 * specified {@link DashboardFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DashboardViewAdapter extends RecyclerView.Adapter<DashboardViewAdapter.ViewHolder>
        implements DraggableItemAdapter<DashboardViewAdapter.ViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private Dashboards mDashboards;

    public DashboardViewAdapter(Dashboards items, DashboardFragment.OnListFragmentInteractionListener listener) {
        mDashboards = items;
        mListener = listener;
        setHasStableIds(true);
    }

    public void setDashboards(Dashboards values) {
        mDashboards = values;
    }
    public Dashboards getDashboards(){ return mDashboards; }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mDashboards.get(position);
        holder.mNameView.setText(mDashboards.get(position).getName());

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        final DashboardViewAdapter aptr = this;

        holder.mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                UIHelper.ShowConfirm(view.getContext(), "Confirm", "Are you sure you eant to delete this dashboard", new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if(success)
                        {
                            DBEngine db = new DBEngine(view.getContext());
                            db.deleteFromDatabase(holder.mItem);
                            mDashboards.remove(holder.mItem);
                            aptr.notifyItemRemoved(position);
                        }
                    }
                });
            }
        });



        final int dragState = holder.getDragStateFlags();

        int bgResId = R.drawable.btn_round;

        if (((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.btn_round_accent;
            } else if ((dragState & DashboardViewAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.btn_round_dark;
            } else {
                bgResId = R.drawable.btn_round;
            }
        }

        holder.mView.setBackgroundResource(bgResId);
    }

    @Override
    public long getItemId(int position) {
        return mDashboards.get(position).getPosition();
    }

    @Override
    public int getItemCount() {
        return mDashboards.size();
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
        mDashboards.moveItem(fromPosition, toPosition);
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
        final FrameLayout mContainer;
        final ImageButton mDelBtn;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.dash_list_name);
            mContainer = view.findViewById(R.id.dashboard_list_block);
            mDelBtn = view.findViewById(R.id.btn_delete_dash);
        }
        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    private interface Draggable extends DraggableItemConstants {
    }
}
