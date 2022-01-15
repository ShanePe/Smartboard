package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;

/**
 * Created by shane on 03/02/18.
 */

public class DashboardLayout extends LinearLayoutCompat {
    private Dashboards mDashboards;
    private ViewSwiper mViewSwiper;

    public DashboardLayout(Context context) {
        super(context);
        initialiseView(context);
    }

    public DashboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public DashboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    private Dashboards getDashboards() {
        return mDashboards;
    }

    public void setDashboards(Dashboards dashboards) {
        mDashboards = dashboards;
        doPropertyChange();
    }

    private void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

        inflater.inflate(R.layout.dashboard_view_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        TabLayout mTabLayout = this.findViewById(R.id.dvl_tl);
        mViewSwiper = this.findViewById(R.id.dvl_vs);

        mViewSwiper.setTabLayout(mTabLayout);
        mViewSwiper.setAutoHideTabs(true);
        doPropertyChange();

    }

    public void reset() {
        if (mViewSwiper != null)
            if (mViewSwiper.getViewAdapter() != null) {
                mViewSwiper.getViewAdapter().mViewCache.clear();
                mViewSwiper.getViewAdapter().mTabs.clear();
                mViewSwiper.getViewAdapter().notifyDataSetChanged();
            }
    }

    private void doPropertyChange() {
        if (getDashboards() != null) {
            mViewSwiper.clear();
            for (Dashboard d : getDashboards()) {
                DashboardView dashboardView = new DashboardView(this.getContext());
                dashboardView.setDashboard(d);
                mViewSwiper.getViewAdapter().addView(d.getName(), dashboardView);
            }
        }
        mViewSwiper.getViewAdapter().notifyDataSetChanged();
        invalidate();
        requestLayout();

    }
}
