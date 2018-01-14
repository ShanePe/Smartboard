package shane.pennihome.local.smartboard;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Adapters.DashboardRowAdapter;
import shane.pennihome.local.smartboard.Adapters.Interface.OnDashboardAdapterListener;
import shane.pennihome.local.smartboard.Adapters.Interface.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Row;
import shane.pennihome.local.smartboard.Fragments.Tabs.SmartboardFragment;
import shane.pennihome.local.smartboard.Fragments.Tabs.RowsFragment;

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
    private DashboardRowAdapter mRowAdapter;
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

        mRowAdapter = new DashboardRowAdapter(this, new Dashboard(), new OnDashboardAdapterListener(){
            @Override
            public void AddBlock(Row row) {
                row.getBlocks().add(new Block());
                mRowAdapter.notifyDataSetChanged();
            }

            @Override
            public void RowDisplayNameChanged(Row row, boolean displayName) {
                row.setDisplayName(displayName);
            }
        });
    }

    public void showPropertyWindow(String title, int resource, final OnPropertyWindowListener onPropertyWindowListener)
    {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(title);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View)inflater.inflate(resource, null);

        if(onPropertyWindowListener!=null)
            onPropertyWindowListener.onWindowShown(view);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(onPropertyWindowListener!=null)
                    onPropertyWindowListener.onOkSelected(view);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void ShowInput(String title, final OnProcessCompleteListener onProcessCompleteListener)
    {
        final Context me = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(onProcessCompleteListener != null)
                {
                    try{
                        String val =  input.getText().toString();
                        if(TextUtils.isEmpty(val))
                            throw new Exception("Value not supplied");
                        onProcessCompleteListener.complete(true, val);
                    }catch(Exception ex){
                        Toast.makeText(me, "Error : " + ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public DashboardRowAdapter getRowAdapter() {
        return mRowAdapter;
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
            switch (position)
            {
                case 0: return SmartboardFragment.newInstance(1);
                case 1: return RowsFragment.newInstance(2);
                default: return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        public SmartboardActivity getContext() {
            return mContext;
        }

        public void setContext(SmartboardActivity context) {
            mContext = context;
        }
    }
}
