package shane.pennihome.local.smartboard;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.Monitor;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeController;
import shane.pennihome.local.smartboard.Comms.SmartThings.STController;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.TokenHueBridge;
import shane.pennihome.local.smartboard.Fragments.DeviceFragment;
import shane.pennihome.local.smartboard.Fragments.HueBridgeFragment;
import shane.pennihome.local.smartboard.Fragments.RoutineFragment;
import shane.pennihome.local.smartboard.Fragments.SmartThingsFragment;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ThingFragment.OnListFragmentInteractionListener,
        SmartThingsFragment.OnFragmentInteractionListener,
        HueBridgeFragment.OnListFragmentInteractionListener {

    private Monitor mMonitor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Globals.setSharedPreferences(this);
        //Globals.setContext(this);
        init();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_st_connect)
            smartThingsConnect();
        else if (id == R.id.btn_ph_connect)
            hueBridgeConnect();
        else if (id == R.id.btn_device_mnu)
            deviceList();
        else if (id == R.id.btn_routine_mnu)
            routineList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        mMonitor = new Monitor(this);
        mMonitor.Start();
    }

    private void smartThingsConnect() {
        @SuppressWarnings("unused") final MainActivity me = this;

        final SmartThingsFragment fragment = new SmartThingsFragment();
        //noinspection unchecked
        fragment.setmProcessComplete(new OnProcessCompleteListener<Activity>() {
            @Override
            public void complete(boolean success, Activity source) {
                if (success) {
                    mMonitor.getSmartThingsThings(new OnProcessCompleteListener<STController>() {
                        @Override
                        public void complete(boolean success, STController source) {
                        }
                    });
                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void hueBridgeConnect() {
        @SuppressWarnings("unused") final MainActivity me = this;

        final HueBridgeFragment fragment = new HueBridgeFragment();
        //noinspection unchecked
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    public void setToMainActivity() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(getFragmentManager().findFragmentById(R.id.content_main));
        ft.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    private void deviceList() {
        DeviceFragment fragment = new DeviceFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>) (List<?>) mMonitor.getDevices());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void routineList() {
        RoutineFragment fragment = new RoutineFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>) (List<?>) mMonitor.getRoutines());
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    @Override
    public void onListFragmentInteraction(Thing item) {

    }

    @Override
    public void onListFragmentInteraction(HueBridge item) {
        TokenHueBridge philipHueHub = TokenHueBridge.Load();
        philipHueHub.setAddress(item.getIp());
        philipHueHub.setId(item.getId());
        philipHueHub.setToken("");
        philipHueHub.Save();

        setToMainActivity();
        mMonitor.getHueBridgeThings(new OnProcessCompleteListener<PHBridgeController>() {
            @Override
            public void complete(boolean success, PHBridgeController source) {

            }
        });
    }
}
