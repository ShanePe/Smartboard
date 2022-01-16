package shane.pennihome.local.smartboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.tabs.GroupFragment;
import shane.pennihome.local.smartboard.fragments.tabs.SmartboardFragment;
import shane.pennihome.local.smartboard.thingsframework.adapters.GroupEditAdapter;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;

public class SmartboardActivity extends AppCompatActivity {
    private static AsyncTask<Void, Void, Void> mLoaderTask;
    private Dashboard mDashboard;
    private GroupEditAdapter mGroupEditAdapter;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ProgressDialog progressDialog = new ProgressDialog();
        progressDialog.setMessage("Loading ...");
        progressDialog.show(this);

        mGroupEditAdapter = new GroupEditAdapter(this);

        mLoaderTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void unused) {
                setContentView(R.layout.activity_smartboard);

                Toolbar toolbar = findViewById(R.id.toolbar_sb);
                setSupportActionBar(toolbar);

                final ActionBar actionBar = getSupportActionBar();
                assert actionBar != null;
                actionBar.setDisplayHomeAsUpEnabled(true);

                SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                final ViewPager mViewPager = findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                TabLayout tabLayout = findViewById(R.id.tabs);

                mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        hideKeyboard();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                progressDialog.dismiss();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                Bundle extras = getIntent().getExtras();
                assert extras != null;
                mDashboard = Dashboard.Load(extras.getString("dashboard_view_group_list"));
                mDashboard.loadThings();
                return null;
            }
        };

        mLoaderTask.execute();
    }

    private void writeDashboardToDatabase() {
        if (mDashboard == null)
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeDashboardToDatabaseInstant();
            }
        }).start();
    }

    private void writeDashboardToDatabaseInstant() {
        if (TextUtils.isEmpty(mDashboard.getName()))
            mDashboard.setName("My Dashboard");

        DBEngine db = new DBEngine(this);
        db.writeToDatabase(mDashboard);
    }

    @Override
    public void onBackPressed() {
        writeDashboardToDatabaseInstant();
        super.onBackPressed();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void DataChanged() {
        writeDashboardToDatabase();
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

    public GroupEditAdapter getGroupAdapter() {
        return mGroupEditAdapter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        writeDashboardToDatabaseInstant();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            writeDashboardToDatabaseInstant();
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);

    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return SmartboardFragment.newInstance(1);
                case 0:
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
}
