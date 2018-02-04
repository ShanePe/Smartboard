package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Group;

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
                    Thread.sleep(1000);
                    mContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            getDashboard().createBackground(getContext(), mContainer);
                        }
                    });
                } catch (InterruptedException ignored) {
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
        }

        @Override
        public int getItemCount() {
            return mDashboard == null ? 0 : mDashboard.getGroups().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final GroupTitle mGroupTitle;
            Group mGroup;

            public ViewHolder(View itemView) {
                super(itemView);

                mGroupTitle = itemView.findViewById(R.id.dvg_title);
            }
        }
    }
}
