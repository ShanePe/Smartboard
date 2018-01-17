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

import shane.pennihome.local.smartboard.Data.Interface.IBlock;
import shane.pennihome.local.smartboard.Data.SwitchBlock;
import shane.pennihome.local.smartboard.Data.Switch;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Fragments.DashboardFragment;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.UI.Interface.IBlockUI;
import shane.pennihome.local.smartboard.UI.SwitchBlockHandler;
import shane.pennihome.local.smartboard.UI.UIHelper;

/**
 * Created by shane on 15/01/18.
 */
public class DashboardBlockAdapter extends RecyclerView.Adapter<IBlockUI.BaseEditorViewHolder>
        implements DraggableItemAdapter<IBlockUI.BaseEditorViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private List<IBlock> mValues;
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
    private SmartboardActivity mSmartboardActivity;
    private Group mGroup;

    public DashboardBlockAdapter(SmartboardActivity smartboardActivity, Group group, DashboardFragment.OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        mSmartboardActivity = smartboardActivity;
        mGroup = group;
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        IBlock block = mValues.get(position);
        return IBlock.GetTypeID(block);
    }



    @Override
    public IBlockUI.BaseEditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IBlock block = IBlock.CreateByTypeID(viewType);
        if(block == null)
            return null;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(block.GetViewResourceID(), parent, false);
        return block.getUIHandler().GetEditorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IBlockUI.BaseEditorViewHolder holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        return mValues.get(position).getGroupId();
    }

    public void setValues(List<IBlock> mValues) {
        this.mValues = mValues;
    }

    public List<IBlock> setValues() {
        return this.mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public boolean onCheckCanStartDrag(IBlockUI.BaseEditorViewHolder holder, int position, int x, int y) {
        return false;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(IBlockUI.BaseEditorViewHolder holder, int position) {
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
        public SwitchBlock mItem;
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