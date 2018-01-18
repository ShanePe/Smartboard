package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThingUIHandler;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 15/01/18.
 */
public class EditThingAdapter extends RecyclerView.Adapter<IThingUIHandler.BaseEditorViewHolder>
        implements DraggableItemAdapter<IThingUIHandler.BaseEditorViewHolder> {

    private final DashboardFragment.OnListFragmentInteractionListener mListener;
    private Things mThings;
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
    private SmartboardActivity mSmartboardActivity;
    private Group mGroup;
    private AppCompatImageButton mDeleteBtn;

    public EditThingAdapter(SmartboardActivity smartboardActivity, Group group, DashboardFragment.OnListFragmentInteractionListener listener) {
        mThings = new Things();
        mListener = listener;
        mSmartboardActivity = smartboardActivity;
        mGroup = group;
        setHasStableIds(true);
    }

    private View reverseFindById(View view, int id)
    {
        if(view == null)
            return null;

        View v = view.findViewById(id);
        if(v!=null)
            return v;
        else
            return reverseFindById((View)view.getParent(), id);
    }

    @Override
    public int getItemViewType(int position) {
        return IThing.GetTypeID(mThings.get(position));
    }

    @Override
    public IThingUIHandler.BaseEditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        IThing thing = null;
        try {
            thing = IThing.CreateByTypeID(viewType);
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(thing.getUIHandler().getViewResourceID(), parent, false);

            mDeleteBtn = (AppCompatImageButton)reverseFindById((View)parent, R.id.btn_delete_item);

            return thing.getUIHandler().GetEditorViewHolder(view);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final IThingUIHandler.BaseEditorViewHolder holder, final int position) {
        final int dragState = holder.getDragStateFlags();

        int bgResId = 0;

        if (((dragState & EditThingAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & EditThingAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                //DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & EditThingAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }
        }

        final IThing thing = mThings.get(position);
        final IThingUIHandler handler = thing.getUIHandler();

        holder.getContainer().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showThingPropertyWindow(mSmartboardActivity,thing.getFilteredView(mSmartboardActivity.getThings()) , thing, new OnThingSetListener() {
                    @Override
                    public void OnSet(IThing thing) {
                        mGroup.getGroupViewHandler().NotifyChanged();
                    }
                });
            }
        });

        handler.BindViewHolder(holder, bgResId);
    }

    @Override
    public long getItemId(int position) {
        return mThings.get(position).getGroupId();
    }

    public void setThings(Things mValues) {
        this.mThings = mValues;
    }

    public Things getThings() {
        return this.mThings;
    }

    @Override
    public int getItemCount() {
        return mThings.size();
    }

    @Override
    public boolean onCheckCanStartDrag(IThingUIHandler.BaseEditorViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(IThingUIHandler.BaseEditorViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
           getThings().moveItem(fromPosition, toPosition);
        } else {
            getThings().swapItem(fromPosition, toPosition);
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