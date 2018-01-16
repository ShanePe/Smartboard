package shane.pennihome.local.smartboard.Adapters;

import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Fragments.DashboardFragment;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 15/01/18.
 */
public class DashboardBlockAdapter extends RecyclerView.Adapter<DashboardBlockAdapter.ViewHolder>
        implements DraggableItemAdapter<DashboardBlockAdapter.ViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private List<Block> mValues;
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;

    public DashboardBlockAdapter(DashboardFragment.OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).getGroupId();
    }

    public void setValues(List<Block> mValues) {
        this.mValues = mValues;
    }

    public List<Block> setValues() {
        return this.mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mBaName.setText(holder.mItem.getName());
        if (holder.mItem.getThing().getSource() == Thing.Source.SmartThings) {
            holder.mBaImg.setImageResource(R.drawable.icon_switch);
        } else if (holder.mItem.getThing().getSource() == Thing.Source.PhilipsHue) {
            holder.mBaImg.setImageResource(R.drawable.icon_phlogo);
        }

        holder.mBaDevice.setText(holder.mItem.getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s", holder.mItem.getWidth(), holder.mItem.getHeight()));
        if (holder.mItem.getThing() instanceof Device)
            holder.mBaType.setText(R.string.lbl_device);
        else if (holder.mItem.getThing() instanceof Routine)
            holder.mBaType.setText(R.string.lbl_routine);

        @ColorInt int bgClr = getThingColour(holder.mItem.getThing(), holder.mItem.getBackgroundColourOff(), holder.mItem.getBackgroundColourOn());
        @ColorInt int fgClr = getThingColour(holder.mItem.getThing(), holder.mItem.getForeColourOff(), holder.mItem.getForeColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);
        holder.mBaType.setTextColor(fgClr);

        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                //DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    private @ColorInt
    int getThingColour(Thing thing, int Off, int On) {
        if (thing instanceof Routine)
            return Off;
        if (thing instanceof Device)
            switch (((Device) thing).getState()) {
                case On:
                    return On;
                default:
                    return Off;
            }
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setItemMoveMode(int itemMoveMode) {
        mItemMoveMode = itemMoveMode;
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
        //if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
        //   mProvider.moveItem(fromPosition, toPosition);

        //} else {
        //    mProvider.swapItem(fromPosition, toPosition);
        //}
        Collections.swap(mValues, fromPosition, toPosition);
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

    public class ViewHolder extends AbstractDraggableItemViewHolder {
        public final View mView;
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public final TextView mBaType;
        public Block mItem;
        public FrameLayout mContainer;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContainer = view.findViewById(R.id.cv_dashboard_block);

            mLayout = view.findViewById(R.id.block_area);
            mBaName = view.findViewById(R.id.ba_name);
            mBaImg = view.findViewById(R.id.ba_image);
            mBaDevice = view.findViewById(R.id.ba_device);
            mBaSize = view.findViewById(R.id.ba_size);
            mBaType = view.findViewById(R.id.ba_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }
}