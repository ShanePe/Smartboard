package shane.pennihome.local.smartboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.CookieManager;

import java.util.List;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.services.PhillipsHue.HueBridge;
import shane.pennihome.local.smartboard.data.TokenHueBridge;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.fragments.DeviceFragment;
import shane.pennihome.local.smartboard.services.PhillipsHue.HueBridgeFragment;
import shane.pennihome.local.smartboard.fragments.RoutineFragment;
import shane.pennihome.local.smartboard.fragments.ServicesFragment;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HueBridgeFragment.OnListFragmentInteractionListener {

    private Dashboards mDashboards;

    @Override
    protected void onPostResume() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        super.onPostResume();
    }

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

        CookieManager.getInstance().setAcceptCookie(true);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        //DBEngine db = new DBEngine(this);
        //db.cleanDataStore();

        if (savedInstanceState != null) {
            String monitor = savedInstanceState.getString("monitor");
            if (monitor != null)
                try {
                    Monitor.Create(monitor);
                } catch (Exception ignored) {
                }
        }
        if (!Monitor.IsInstaniated())
            Monitor.Create(this);


        Monitor.getMonitor().start();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        boolean handled = false;
        for (Fragment f : fragmentList) {
            if (f instanceof IFragment) {
                handled = ((IFragment) f).onBackPressed(this);

                if (handled) {
                    break;
                }
            }
        }

        if (!handled) {

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btn_st_connect)
            serviceList();
        else if (id == R.id.btn_ph_connect)
            hueBridgeConnect();
        else if (id == R.id.btn_device_mnu)
            deviceList();
        else if (id == R.id.btn_routine_mnu)
            routineList();
        else if (id == R.id.btn_dashboard_mnu)
            dashboardList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void populateDashbboards() {
        DBEngine db = new DBEngine(this);
        //db.cleanDataStore();
        if (mDashboards == null)
            mDashboards = new Dashboards();
        else
            mDashboards.clear();

        for (IDatabaseObject d : db.readFromDatabaseByType(IDatabaseObject.Types.Dashboard))
            mDashboards.add((Dashboard) d);

        mDashboards.sort();
    }

    private void smartThingsConnect() {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.registerService(this, new SmartThingsService(), null);
    }

    @Override
    protected void onResume() {
        populateDashbboards();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        super.onResume();
    }

    private void serviceList() {
        final ServicesFragment fragment = new ServicesFragment();
        //noinspection unchecked
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("serviceList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void hueBridgeConnect() {
        final HueBridgeFragment fragment = new HueBridgeFragment();
        //noinspection unchecked
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("huebridgeConnect");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    public void backToMainActivity() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(getSupportFragmentManager().findFragmentById(R.id.content_main));
        ft.commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    private void deviceList() {
        DeviceFragment fragment = new DeviceFragment();
        //noinspection unchecked
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("deviceList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void routineList() {
        RoutineFragment fragment = new RoutineFragment();
        //noinspection unchecked
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("routineList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void dashboardList() {
        DashboardFragment fragment = new DashboardFragment();
        //noinspection unchecked
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("dashboardList");
        ft.replace(R.id.content_main, fragment);

        ft.commit();
    }

    public Dashboards getDashboards() {
        if (mDashboards == null)
            return new Dashboards();

        return mDashboards;
    }

    @Override
    public void onListFragmentInteraction(HueBridge item) {
        TokenHueBridge philipHueHub = TokenHueBridge.Load();
        philipHueHub.setAddress(item.getIp());
        philipHueHub.setId(item.getId());
        philipHueHub.setToken("");
        philipHueHub.Save();

        backToMainActivity();
//        mMonitor.getHueBridgeThings(new OnProcessCompleteListener<PHBridgeController>() {
//            @Override
//            public void complete(boolean success, PHBridgeController source) {
//
//            }
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (Monitor.IsInstaniated())
                outState.putString("monitor", Monitor.getMonitor().toJson());
        } catch (Exception ignored) {
        }
    }
}
