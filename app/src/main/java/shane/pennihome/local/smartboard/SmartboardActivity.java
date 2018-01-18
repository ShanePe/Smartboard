package shane.pennihome.local.smartboard;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import shane.pennihome.local.smartboard.adapters.EditGroupAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.tabs.GroupsFragment;
import shane.pennihome.local.smartboard.fragments.tabs.SmartboardFragment;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;

public class SmartboardActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private EditGroupAdapter mEditGroupAdapter;
    private Things mThings = new Things();
    private Dashboard mDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sb);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mEditGroupAdapter = new EditGroupAdapter(this);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> devices = extras.getStringArrayList("devices");
        ArrayList<String> routines = extras.getStringArrayList("routines");
        mDashboard = Dashboard.Load(extras.getString("dashboard"));

        for (String j : devices)
            try {
                mThings.add(Switch.Load(j));
            } catch (Exception ex) {
            }
        for (String j : routines)
            try {
                mThings.add(Routine.Load(j));
            } catch (Exception ex) {
            }

        for (Group g : mDashboard.getGroups())
            for (IThing thing : g.getThings())
                if (thing != null) {
                    int thingPos = mThings.GetIndex(thing);
                    if (thingPos != -1)
                        thing.copyValuesFrom(mThings.get(thingPos));
                }
    }

    public Things getThings() {
        return mThings;
    }

    public void WriteDashboardToDatabase() {
        if (mDashboard == null)
            return;

        final SmartboardActivity me = this;
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

    public Dashboard getDashboard() {
        return mDashboard;
    }

    public EditGroupAdapter getGroupAdapter() {
        return mEditGroupAdapter;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SmartboardActivity mContext;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
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
            // Show 3 total pages.
            return 2;
        }
    }
}
