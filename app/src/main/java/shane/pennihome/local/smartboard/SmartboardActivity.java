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

import shane.pennihome.local.smartboard.adapters.EditGroupAdapter;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.tabs.GroupsFragment;
import shane.pennihome.local.smartboard.fragments.tabs.SmartboardFragment;

public class SmartboardActivity extends AppCompatActivity {

    private EditGroupAdapter mEditGroupAdapter;
    private Dashboard mDashboard;

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

        mEditGroupAdapter = new EditGroupAdapter(this);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        mDashboard = Dashboard.Load(extras.getString("dashboard"));

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

        for (Group g : mDashboard.getGroups())
            for (IThing thing : g.getThings())
                if (thing != null) {
                    int thingPos = Monitor.getThings().GetIndex(thing);
                    if (thingPos != -1)
                        thing.copyValuesFrom(Monitor.getThings().get(thingPos));
                }
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

        if(mDashboard.getOrderId() == 0)
            mDashboard.setOrderId(Globals.GetNextLongId());

        DBEngine db = new DBEngine(this);
        db.WriteToDatabase(mDashboard);
    }

    @Override
    public void onBackPressed() {
        WriteDashboardToDatabaseInstant();
        super.onBackPressed();
    }

    public void DataChanged() {
        WriteDashboardToDatabase();
        mEditGroupAdapter.notifyDataSetChanged();
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

    public EditGroupAdapter getGroupAdapter() {
        return mEditGroupAdapter;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        //private SmartboardActivity mContext;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SmartboardFragment.newInstance(1);
                case 1:

                    return GroupsFragment.newInstance(2);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
