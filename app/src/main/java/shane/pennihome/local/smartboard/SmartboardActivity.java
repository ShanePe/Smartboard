package shane.pennihome.local.smartboard;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.tabs.GroupFragment;
import shane.pennihome.local.smartboard.fragments.tabs.SmartboardFragment;
import shane.pennihome.local.smartboard.thingsframework.adapters.GroupEditAdapter;

public class SmartboardActivity extends AppCompatActivity {

    private Dashboard mDashboard;
    private GroupEditAdapter mGroupEditAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartboard);

        Toolbar toolbar = findViewById(R.id.toolbar_sb);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mGroupEditAdapter = new GroupEditAdapter(this);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mDashboard = Dashboard.Load(extras.getString("dashboard_view_group_list"));
        mDashboard.loadThings();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==1)
                    hideKeyboard();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void WriteDashboardToDatabase() {
        if (mDashboard == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                WriteDashboardToDatabaseInstant();
            }
        }).start();
    }

    private void WriteDashboardToDatabaseInstant() {
        if (TextUtils.isEmpty(mDashboard.getName()))
            mDashboard.setName("My Dashboard");

        DBEngine db = new DBEngine(this);
        db.writeToDatabase(mDashboard);
    }

    @Override
    public void onBackPressed() {
        WriteDashboardToDatabaseInstant();
        super.onBackPressed();
    }

    public void DataChanged() {
        WriteDashboardToDatabase();
        mGroupEditAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = getCurrentFocus();
        if (v == null)
            return;

        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public Dashboard getDashboard() {
        return mDashboard;
    }

    public GroupEditAdapter getGroupAdapter()
    {
        return mGroupEditAdapter;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SmartboardFragment.newInstance(1);
                case 1:

                    return GroupFragment.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        WriteDashboardToDatabaseInstant();
    }
}
