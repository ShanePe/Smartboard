package shane.pennihome.local.smartboard;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeConnector;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeDeviceGetter;
import shane.pennihome.local.smartboard.Comms.SmartThings.STDevicesGetter;
import shane.pennihome.local.smartboard.Comms.SmartThings.STEndPointGetter;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Data.HueBridgeToken;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsToken;
import shane.pennihome.local.smartboard.Fragments.DeviceFragment;
import shane.pennihome.local.smartboard.Fragments.HueBridgeFragment;
import shane.pennihome.local.smartboard.Fragments.RoutineFragment;
import shane.pennihome.local.smartboard.Fragments.SmartThingsFragment;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ThingFragment.OnListFragmentInteractionListener,
        SmartThingsFragment.OnFragmentInteractionListener,
        HueBridgeFragment.OnListFragmentInteractionListener {

    private List<Device> mDevices = new ArrayList<>();
    private List<Routine> mRoutines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            philipsHueConnect();
        else if (id == R.id.btn_device_mnu)
            deviceList();
        else if (id == R.id.btn_routine_mnu)
            routineList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        this.getDevices();
    }

    private void smartThingsConnect() {
        final MainActivity me = this;

        final SmartThingsFragment fragment = new SmartThingsFragment();
        //noinspection unchecked
        fragment.setmProcessComplete(new OnProcessCompleteListener<Activity>() {
            @Override
            public void Complete(boolean success, Activity source) {
                if (success) {
                    me.getDevices();
                }
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void philipsHueConnect() {
        final MainActivity me = this;

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

    private void getPHDevices(final OnProcessCompleteListener<Activity> processComplete) {
        final MainActivity me = this;
        connectPHBridge(new OnProcessCompleteListener<HueBridge>() {
            @Override
            public void Complete(boolean success, HueBridge source) {
                if (success) {
                    PHBridgeDeviceGetter phBridgeDeviceGetter = new PHBridgeDeviceGetter(me, true, new OnProcessCompleteListener<PHBridgeDeviceGetter>() {
                        @Override
                        public void Complete(boolean success, PHBridgeDeviceGetter source) {

                        }
                    });
                    phBridgeDeviceGetter.execute();
                }
            }
        });
    }

    private void connectPHBridge(final OnProcessCompleteListener<HueBridge> processComplete) {
        final MainActivity me = this;

        final HueBridgeToken philipHueHub = HueBridgeToken.Load();

        if (!philipHueHub.getAddress().equals("") && philipHueHub.getToken().equals("")) {
            PHBridgeConnector phBridgeConnector = new PHBridgeConnector(me, new OnProcessCompleteListener<PHBridgeConnector>() {
                @Override
                public void Complete(boolean success, PHBridgeConnector source) {
                    if (success) {
                        philipHueHub.setToken(source.getConnectHueBridge().getToken());
                        philipHueHub.Save();
                        if (processComplete != null)
                            processComplete.Complete(true, HueBridge.FromTokenInfo(philipHueHub));
                    }
                }
            });
            phBridgeConnector.execute();
        } else if (!philipHueHub.getAddress().equals("") && !philipHueHub.getToken().equals("")) {
            if (processComplete != null)
                processComplete.Complete(true, HueBridge.FromTokenInfo(philipHueHub));
        } else if (processComplete != null)
            processComplete.Complete(false, null);
    }

    private void getSTDevices(final OnProcessCompleteListener<Activity> processComplete) {
        final MainActivity me = this;

        SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
        Calendar c = Calendar.getInstance();

        if (!Objects.equals(smartThingsTokenInfo.getToken(), "") && smartThingsTokenInfo.getExpires().after(c.getTime())) {
            STEndPointGetter endPointGetter = new STEndPointGetter(new OnProcessCompleteListener<STEndPointGetter>() {
                @Override
                public void Complete(boolean success, STEndPointGetter source) {
                    if (success) {
                        SmartThingsToken smartThingsTokenInfo = SmartThingsToken.Load();
                        STDevicesGetter devicesGetter = new STDevicesGetter(smartThingsTokenInfo.getRequestUrl(), true, me, new OnProcessCompleteListener<STDevicesGetter>() {
                            @Override
                            public void Complete(boolean success, STDevicesGetter source) {
                                if (success) {
                                    mDevices = source.getDevices();
                                    mRoutines = source.getRoutines();

                                    if (processComplete != null)
                                        processComplete.Complete(true, me);
                                } else
                                    Toast.makeText(getApplicationContext(), "Could not get devices.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        devicesGetter.execute();

                    } else
                        Toast.makeText(getApplicationContext(), "Could not get endpoint.", Toast.LENGTH_SHORT).show();
                }
            }, me);

            endPointGetter.execute();
        } else {
            if (processComplete != null)
                processComplete.Complete(true, me);
        }
    }

    private void getDevices() {
        mDevices = null;
        mRoutines = null;

        getSTDevices(new OnProcessCompleteListener<Activity>() {
            @Override
            public void Complete(boolean success, Activity source) {
                getPHDevices(new OnProcessCompleteListener<Activity>() {
                    @Override
                    public void Complete(boolean success, Activity source) {

                    }
                });
            }
        });
    }

    private void deviceList() {
        DeviceFragment fragment = new DeviceFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>) (List<?>) mDevices);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void routineList() {
        RoutineFragment fragment = new RoutineFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>) (List<?>) mRoutines);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    @Override
    public void onListFragmentInteraction(Thing item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(HueBridge item) {
        HueBridgeToken philipHueHub = HueBridgeToken.Load();
        philipHueHub.setAddress(item.getIp());
        philipHueHub.setId(item.getId());
        philipHueHub.setToken("");
        philipHueHub.Save();

        setToMainActivity();
        getDevices();
    }
}
