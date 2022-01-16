package shane.pennihome.local.smartboard.ui;

import android.arch.core.util.Function;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.thingsframework.adapters.BlockEditAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.listeners.OnDialogWindowListener;

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

    private void handle(final SmartboardActivity smartboardActivity, final ViewGroup parent, final View view, final Group group, RecyclerView recyclerView) {
        mSmartboardActivity = smartboardActivity;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(smartboardActivity, Globals.getColumnCount());

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
            public void onListFragmentInteraction(Dashboard item) { }
        });

        final ArrayList<AnimationButton> animationButtons = new ArrayList<>();

        animationButtons.add(new AnimationButton((ImageButton) ((View) parent).findViewById(R.id.btn_delete_item), new OnProcessCompleteListener<View>() {
            @Override
            public void complete(boolean success, final View item) {
                UIHelper.showConfirm(mSmartboardActivity, "Confirm", "Are you sure you wnat to remove this block?", new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (success) {
                            item.setVisibility(View.GONE);
                            int at = ((RecyclerView) item.getParent()).getChildAdapterPosition(item);
                            mBlockEditAdapter.getBlocks().remove(at);
                            mSmartboardActivity.DataChanged();
                        }
                    }
                });
            }
        }));

        animationButtons.add(new AnimationButton((ImageButton) ((View) parent).findViewById(R.id.btn_copy_item), new OnProcessCompleteListener<View>() {
            @Override
            public void complete(boolean success, View item) {
                UIHelper.showDialogWindow(smartboardActivity, "Copy to group", R.layout.dialog_group_select, getBlockAction(group, item, new Function<Pair<Group, IBlock>, Void>() {

                    @Override
                    public Void apply(Pair<Group, IBlock> groupIBlockPair) {
                        try {
                            groupIBlockPair.first.getBlocks().add(groupIBlockPair.second.clone());
                            groupIBlockPair.first.getGroupViewHandler().mBlockEditAdapter.notifyDataSetChanged();
                            mSmartboardActivity.DataChanged();
                        } catch (CloneNotSupportedException e) {
                            Toast.makeText(mSmartboardActivity, "Could not copy block: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        return null;
                    }
                }));
            }
        }));

        animationButtons.add(new AnimationButton((ImageButton) ((View) parent).findViewById(R.id.btn_move_item), new OnProcessCompleteListener<View>() {
            @Override
            public void complete(boolean success, final View item) {
                UIHelper.showDialogWindow(smartboardActivity, "Move to group", R.layout.dialog_group_select, getBlockAction(group, item, new Function<Pair<Group, IBlock>, Void>() {

                    @Override
                    public Void apply(Pair<Group, IBlock> groupIBlockPair) {
                        groupIBlockPair.first.getBlocks().add(groupIBlockPair.second);
                        int at = ((RecyclerView) item.getParent()).getChildAdapterPosition(item);
                        mBlockEditAdapter.getBlocks().remove(at);
                        mBlockEditAdapter.notifyDataSetChanged();
                        groupIBlockPair.first.getGroupViewHandler().mBlockEditAdapter.notifyDataSetChanged();
                        mSmartboardActivity.DataChanged();
                        return null;
                    }
                }));
            }
        }, new Function<View, Void>() {
            @Override
            public Void apply(View view) {
                view.setVisibility(View.VISIBLE);
                return null;
            }
        }, new Function<View, Void>() {
            @Override
            public Void apply(View view) {
                view.setVisibility(View.GONE);
                return null;
            }
        }));

        dragDropManager.setOnCustomOnMoveListener(new RecyclerViewDragDropManager.OnCustomOnMoveListener() {
            @Override
            public void OnStartDrag(RecyclerView rv) {
                for (AnimationButton a : animationButtons) {
                    if (a.getOnInitialise() != null)
                        a.getOnInitialise().apply(a.getImageButton());

                    a.getImageButton().setBackgroundResource(R.drawable.btn_round_accent);
                    Animation anim = AnimationUtils.loadAnimation(mSmartboardActivity, R.anim.shake_animate);
                    a.getImageButton().startAnimation(anim);
                }
            }

            @Override
            public void OnEndDrag(RecyclerView rv) {
            }

            @Override
            public void OnClick(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void OnMove(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void OnUpOrCancel(final RecyclerView rv, MotionEvent e) {
                final View item = rv.findChildViewUnder(e.getX(), e.getY());
                if (item == null)
                    return;
                Rect itemRect = getViewRect(item);

                for (AnimationButton a : animationButtons) {
                    a.getImageButton().setBackgroundResource(R.drawable.btn_round_dark);
                    Rect vRect = getViewRect(a.getImageButton());
                    if (itemRect.contains(vRect)) {
                        item.setVisibility(View.VISIBLE);
                        a.getOnProcessCompleteListener().complete(true, item);
                    }
                    if(a.getOnEnd()!=null)
                        a.getOnEnd().apply(a.getImageButton());
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

    public OnDialogWindowListener<Group> getBlockAction(final Group group, final View item, final Function<Pair<Group, IBlock>, Void> funct) {
        return new OnDialogWindowListener<Group>() {
            Spinner mGroup;

            @Override
            public void onWindowShown(View view) {
                mGroup = view.findViewById(R.id.sp_grp_sel);
                mGroup.setAdapter(new CurrentGroupAdapter());
                mGroup.setSelection(mSmartboardActivity.getDashboard().getGroupIndex(group));
            }

            @Override
            public Group Populate(View view) {
                return (Group) mGroup.getSelectedItem();
            }

            @Override
            public void OnComplete(Group data) {
                int at = ((RecyclerView) item.getParent()).getChildAdapterPosition(item);
                funct.apply(new Pair<Group, IBlock>(data, mBlockEditAdapter.getBlocks().get(at)));
                data.setUIExpanded(true);
            }
        };
    }

    private Rect getViewRect(View view) {
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

    public static class AnimationButton {
        OnProcessCompleteListener<View> mOnProcessCompleteListener;
        Function<View, Void> mOnInitialise;
        Function<View, Void> mOnEnd;
        ImageButton mImageButton;

        public Function<View, Void> getOnEnd() {
            return mOnEnd;
        }

        public void setOnEnd(Function<View, Void> end) {
            this.mOnEnd = end;
        }

        public AnimationButton(ImageButton btn, OnProcessCompleteListener<View> onProcessCompleteListener) {
            this(btn, onProcessCompleteListener, null, null);
        }

        public AnimationButton(ImageButton btn, OnProcessCompleteListener<View> onProcessCompleteListener, Function<View, Void> init,Function<View,Void> end) {
            setOnInitialise(init);
            setOnEnd(end);
            setImageButton(btn);
            setOnProcessCompleteListener(onProcessCompleteListener);
        }

        public ImageButton getImageButton() {
            return mImageButton;
        }

        public void setImageButton(ImageButton imageButton) {
            this.mImageButton = imageButton;
        }

        public OnProcessCompleteListener<View> getOnProcessCompleteListener() {
            return mOnProcessCompleteListener;
        }

        public void setOnProcessCompleteListener(OnProcessCompleteListener<View> onProcessCompleteListener) {
            this.mOnProcessCompleteListener = onProcessCompleteListener;
        }

        public Function<View, Void> getOnInitialise() {
            return mOnInitialise;
        }

        public void setOnInitialise(Function<View, Void> initialise) {
            this.mOnInitialise = initialise;
        }
    }

    private class CurrentGroupAdapter extends BaseAdapter implements SpinnerAdapter {

        @Override
        public int getCount() {
            return mSmartboardActivity.getDashboard().getGroups().size();
        }

        @Override
        public Object getItem(int position) {
            return mSmartboardActivity.getDashboard().getGroupAt(position);
        }

        @Override
        public long getItemId(int position) {
            return mSmartboardActivity.getDashboard().getGroupAt(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(mSmartboardActivity).inflate(R.layout.spinner_group, parent, false);

            TextView txt = convertView.findViewById(R.id.spin_grp_name);
            txt.setText(((Group) getItem(position)).getName());

            return convertView;
        }
    }
}

