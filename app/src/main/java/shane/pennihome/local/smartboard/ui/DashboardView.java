package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;

/**
 * Created by shane on 03/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DashboardView extends LinearLayoutCompat {
    private Dashboard mDashboard;
    private LinearLayoutCompat mContainer;
    private GroupViewAdapter mGroupViewAdapter;
    private Thread mBGDrawer;

    public DashboardView(Context context) {
        super(context);
        initialiseView(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    private Dashboard getDashboard() {
        return mDashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        mDashboard = dashboard;
        doPropertyChanged();
    }

    private void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        inflater.inflate(R.layout.dashboard_view_group_list, this);

        mContainer = findViewById(R.id.dvgl_container);
        mContainer.setVisibility(View.GONE);
        RecyclerView mRecycleView = findViewById(R.id.dvgl_list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(context, LinearLayout.VERTICAL, false));
        mGroupViewAdapter = new GroupViewAdapter(getDashboard());
        mRecycleView.setAdapter(mGroupViewAdapter);
    }

    private void doPropertyChanged() {
        if (getDashboard() == null)
            return;

        if (mBGDrawer != null) {
            mBGDrawer.interrupt();
            mBGDrawer = null;
        }

        mGroupViewAdapter.setDashboard(getDashboard());
        mBGDrawer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("Presleep -> " + getDashboard().getName(), Globals.ACTIVITY);
                    Thread.sleep(500);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Render thread -> " + getDashboard().getName(), Globals.ACTIVITY);
                            mContainer.setVisibility(View.VISIBLE);
                            getDashboard().renderBackgroundTo(getContext(), mContainer);
                            AlphaAnimation animation = new AlphaAnimation(0f, 1.0f);
                            animation.setDuration(1000);
                            mContainer.startAnimation(animation);
                        }
                    });

                } catch (InterruptedException ignored) {
                    Log.i("Render -> BROKE " + getDashboard().getName(), Globals.ACTIVITY);

                }
            }
        });
        mBGDrawer.start();

        mGroupViewAdapter.notifyDataSetChanged();
    }

    class GroupViewAdapter extends RecyclerView.Adapter<GroupViewAdapter.ViewHolder> {
        Dashboard mDashboard;

        GroupViewAdapter(Dashboard dashboard) {
            this.mDashboard = dashboard;
        }

        public Dashboard getDashboard() {
            return mDashboard;
        }

        public void setDashboard(Dashboard dashboard) {
            mDashboard = dashboard;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dashboard_view_group, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mGroup = mDashboard.getGroupAt(position);
            holder.mGroupTitle.setTitle(holder.mGroup.getName());
            holder.mGroupTitle.setVisibility(holder.mGroup.getDisplayName() ? View.VISIBLE : View.GONE);

            int blockSize = Resources.getSystem().getDisplayMetrics().widthPixels / Globals.BLOCK_COLUMNS;
            BlockViewAdapter adapter = new BlockViewAdapter(holder.mGroup, blockSize);
            holder.mBlockView.setAdapter(adapter);
        }

        @Override
        public int getItemCount() {
            return mDashboard == null ? 0 : mDashboard.getGroups().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final GroupTitle mGroupTitle;
            RecyclerView mBlockView;
            Group mGroup;

            public ViewHolder(View itemView) {
                super(itemView);

                mGroupTitle = itemView.findViewById(R.id.dvg_title);
                mBlockView = itemView.findViewById(R.id.dvg_dashboard);
            }
        }
    }

    class BlockViewAdapter extends RecyclerView.Adapter<IBlockUIHandler.BlockViewHolder> {
        int mBlockSize;
        private Group mGroup;

        BlockViewAdapter(Group group, int blockSize) {
            this.mGroup = group;
            this.mBlockSize = blockSize;
        }

        @Override
        public int getItemViewType(int position) {
            return IBlock.GetTypeID(mGroup.getBlockAt(position));
        }

        @Override
        public IBlockUIHandler.BlockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            IBlock block;
            try {
                block = IBlock.CreateByTypeID(viewType);
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(block.getUIHandler().getViewLayoutID(), parent, false);

                return block.getUIHandler().GetViewHolder(view);
            } catch (Exception ignored) {
                return null;
            }
        }

        @Override
        public void onBindViewHolder(IBlockUIHandler.BlockViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return mGroup.getBlocks().size();
        }

    }
}
