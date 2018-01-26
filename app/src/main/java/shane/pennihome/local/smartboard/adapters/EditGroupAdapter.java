package shane.pennihome.local.smartboard.adapters;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.listeners.OnThingSelectListener;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.ui.GroupViewHandler;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 20/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class EditGroupAdapter extends RecyclerView.Adapter<EditGroupAdapter.ViewHolder>
        implements DraggableItemAdapter<EditGroupAdapter.ViewHolder> {

    private final SmartboardActivity mSmartboardActivity;

    public EditGroupAdapter(SmartboardActivity mSmartboardActivity) {
        this.mSmartboardActivity = mSmartboardActivity;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mSmartboardActivity.getDashboard().getGroupAt(position).getPosition();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_group_listitem, parent, false);
        return new EditGroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Group group = mSmartboardActivity.getDashboard().getGroupAt(position);
        ViewGroup parent = (ViewGroup) holder.mView;

        if(holder.mGroup == null)
            holder.mGroup = group;

        holder.mTxtName.setText(group.getName());
        holder.mGroupHandler = new GroupViewHandler(mSmartboardActivity, parent, holder.mView, group, holder.mRVBlocks);

        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.ShowConfirm(mSmartboardActivity, "Confirm", "Are you sure you want to remove this group?", new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (success) {
                            mSmartboardActivity.getDashboard().getGroups().remove(group);
                            mSmartboardActivity.DataChanged();
                        }
                    }
                });
            }
        });

        holder.mBtnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showPropertyWindow(mSmartboardActivity, "Group Properties", R.layout.prop_group, new OnPropertyWindowListener() {
                    @Override
                    public void onWindowShown(View view) {
                        EditText txtName = view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = view.findViewById(R.id.sw_row_dl_dispname);

                        txtName.setText(group.getName());
                        swDispName.setChecked(group.getDisplayName());
                    }

                    @Override
                    public void onOkSelected(View view) {
                        EditText txtName = view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = view.findViewById(R.id.sw_row_dl_dispname);

                        group.setName(txtName.getText().toString());
                        group.setDisplayName(swDispName.isChecked());
                        mSmartboardActivity.DataChanged();
                    }
                });
            }
        });

        holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.ShowThingsSelectionWindow(mSmartboardActivity, new OnThingSelectListener() {
                    @Override
                    public void ThingSelected(IThing thing) {
                        createBlockInstance(thing, group);
                        UIHelper.showThingPropertyWindow(mSmartboardActivity, thing.getFilteredView(Monitor.getThings()),
                                thing, group, new OnThingSetListener() {
                                    @Override
                                    public void OnSet(IThing thing) {
                                        group.getThings().add(thing);
                                        mSmartboardActivity.DataChanged();
                                        if (!holder.mExpanded)
                                            holder.showBlocks(true);
                                    }
                                });
                    }
                });
            }
        });

        holder.mBtnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.showBlocks(!holder.mExpanded);
            }
        });

        holder.showBlocks(group.isUIExpanded());

        final int dragState = holder.getDragStateFlags();

        int bgResId = R.drawable.btn_round;

        if (((dragState & EditGroupAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {

            if ((dragState & EditGroupAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.btn_round_accent;
            } else if ((dragState & EditGroupAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.btn_round_dark;
            } else {
                bgResId = R.drawable.btn_round;
            }
        }

        holder.mContainer.setBackgroundResource(bgResId);
    }

    private void createBlockInstance(IThing thing, Group group) {
        try {
            thing.CreateBlock();
            thing.setBlockDefaults(group);
        } catch (Exception ex) {
            Toast.makeText(mSmartboardActivity, "Error creating block instance", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mSmartboardActivity.getDashboard().getGroups().size();
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        Rect drawRect = new Rect();
        holder.mContainer.getDrawingRect(drawRect);

        return drawRect.contains(x, y);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
        return null;
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Group item = mSmartboardActivity.getDashboard().getGroups().remove(fromPosition);
        mSmartboardActivity.getDashboard().getGroups().add(toPosition, item);
        this.notifyDataSetChanged();
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {

    }

// --Commented out by Inspection START (25/01/2018 17:32):
//    private ViewHolder getViewHolderAtPosition(int position) {
//        if (mRecycleView == null)
//            return null;
//
//        View view = mRecycleView.getLayoutManager().findViewByPosition(position);
//        if (view == null)
//            return null;
//
//        RecyclerView.ViewHolder holder = mRecycleView.getChildViewHolder(view);
//        if (holder == null)
//            return null;
//
//        return (ViewHolder) holder;
//
//    }
// --Commented out by Inspection STOP (25/01/2018 17:32)

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        this.notifyDataSetChanged();
    }

// --Commented out by Inspection START (26/01/18 14:33):
//    private void scaleView(final View view, boolean expanded) {
//        if (expanded) {
//            view.setVisibility(View.VISIBLE);
//            view.setAlpha(0.0f);
//            //view.setTranslationY(0.0f);
//
//            view.animate()
//                    .alpha(1.0f)
//                    .setListener(null);
//        } else {
//            view.animate()
//                    .alpha(0.0f)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            view.setVisibility(View.GONE);
//                        }
//                    });
//        }
//
//    }
// --Commented out by Inspection STOP (26/01/18 14:33)

    private void rotateView(final View view, final boolean expanded) {
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (expanded)
                    view.setRotation(180);
                else
                    view.setRotation(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(rotate);
    }

    private interface Draggable extends DraggableItemConstants {
    }

    public class ViewHolder extends AbstractDraggableItemViewHolder {
        final ImageButton mBtnExpand;
        final TextView mTxtName;
        final ImageButton mBtnProps;
        final ImageButton mBtnAdd;
        final ImageButton mBtnDelete;
        final RecyclerView mRVBlocks;
        @SuppressWarnings("unused")
        final LinearLayout mContainer;
        final TextView mChildCount;
        // --Commented out by Inspection (26/01/18 14:33):ScrollView mScrollView;

        final View mView;
        boolean mExpanded = true;
        Group mGroup;
        @SuppressWarnings("unused")
        GroupViewHandler mGroupHandler;

        ViewHolder(View itemView) {
            super(itemView);

            mBtnExpand = itemView.findViewById(R.id.btn_add_expanded);
            mTxtName = itemView.findViewById(R.id.txt_row_name);
            mBtnProps = itemView.findViewById(R.id.btn_add_prop);
            mBtnAdd = itemView.findViewById(R.id.btn_add_block);
            mBtnDelete = itemView.findViewById(R.id.btn_delete_item);
            mRVBlocks = itemView.findViewById(R.id.list_blocks);
            mContainer = itemView.findViewById(R.id.group_container);
            mChildCount = itemView.findViewById(R.id.txt_row_child_count);
            mView = itemView;

            showBlocksAction(false);
        }

// --Commented out by Inspection START (25/01/2018 17:32):
//        void Toggle() {
//            showBlocks(!mExpanded);
//        }
// --Commented out by Inspection STOP (25/01/2018 17:32)

        void showBlocks(boolean show) {
            int itemCount = (mGroup == null ? mRVBlocks.getChildCount():mGroup.getThings().size());
            if (itemCount == 0 && show)
                show = false;

            showBlocksAction(show);
        }

        private void showBlocksAction(boolean show)
        {
            if(mExpanded == show)
                return;

            mExpanded = show;
            if(mGroup != null)
                mGroup.setUIExpanded(show);

            //scaleView(mRVBlocks, mExpanded);
            mRVBlocks.setVisibility(show? View.VISIBLE : View.GONE);
            rotateView(mBtnExpand, mExpanded);

            if(show)
                mChildCount.setVisibility(View.GONE);
            else
            {
                int itemCount = (mGroup == null ? mRVBlocks.getChildCount():mGroup.getThings().size());
                mChildCount.setText(String.format("| %s |",itemCount));
                mChildCount.setVisibility(itemCount != 0 ? View.VISIBLE : View.GONE);
            }
        }
    }
}
