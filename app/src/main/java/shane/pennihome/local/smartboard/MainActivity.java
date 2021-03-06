package shane.pennihome.local.smartboard;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Options;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.DashboardFragment;
import shane.pennihome.local.smartboard.fragments.DeviceFragment;
import shane.pennihome.local.smartboard.fragments.OptionsFragment;
import shane.pennihome.local.smartboard.fragments.RoutineFragment;
import shane.pennihome.local.smartboard.fragments.ServicesFragment;
import shane.pennihome.local.smartboard.fragments.TemplateFragment;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.fragments.listeners.OnOptionsChangedListener;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.ui.DashboardLayout;
import shane.pennihome.local.smartboard.ui.ScreenBlocker;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Dashboards mDashboards;
    private DashboardLayout mDashboardLayout;
    private ProgressBar mDashboardLoader;
    private Thread mDashboardRender;
    private Options mOptions;

    @Override
    protected void onPostResume() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        if (Monitor.IsInstaniated())
            if (Monitor.getMonitor().isLoaded()) {
                final ProgressDialog progressDialog = new ProgressDialog();
                progressDialog.setMessage("Refreshing...");
                progressDialog.show(this);

                //noinspection rawtypes
                Monitor.getMonitor().verifyDashboardThings(new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (!Monitor.getMonitor().isRunning())
                            Monitor.getMonitor().start();

                        try {
                            progressDialog.dismiss();
                        } catch (Exception ignored) {
                        }//Ok because saveState might have fired.
                        if (mDashboards == null)
                            renderDashboards();
                        else if (mDashboards.size() == 0)
                            renderDashboards();
                    }
                });
            }
        startScreenFadeOutMonitor();
        super.onPostResume();
    }

    private void pauseFadeMonitor() {
        if (mOptions != null)
            mOptions.setPaused(true);
    }

    private void unpauseFadeMonitor() {
        if (mOptions != null)
            mOptions.setPaused(false);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Globals.setSharedPreferences(this);

        mDashboardLayout = findViewById(R.id.dl_main);
        mDashboardLoader = findViewById(R.id.db_load_progress);

        View headerView = navigationView.getHeaderView(0);
        ImageButton btnHome = headerView.findViewById(R.id.mnu_btn_home);
        final ImageButton btnRefresh = headerView.findViewById(R.id.mnu_btn_refresh);
        ImageButton btnSettings = headerView.findViewById(R.id.mnu_btn_opts);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goHome();
                Monitor.destroy();
                Monitor.Create((AppCompatActivity) ((ContextThemeWrapper) view.getContext()).getBaseContext(), new OnProcessCompleteListener<ArrayList<String>>() {
                    @Override
                    public void complete(boolean success, ArrayList<String> source) {
                        if (!success)
                            for (String e : source)
                                Toast.makeText(btnRefresh.getContext(), e, Toast.LENGTH_LONG).show();
                        renderDashboards();
                        Monitor.getMonitor().start();
                    }
                });
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions();
                drawer.closeDrawers();
            }
        });

        init(savedInstanceState);

        try {
            CookieManager.getInstance().setAcceptCookie(true);
        } catch (Exception ignore) {
        }
    }

    public void goHome() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        drawer.closeDrawers();
        unpauseFadeMonitor();
    }

    private void init(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String monitor = savedInstanceState.getString("monitor");
            if (monitor != null) {
                try {
                    Monitor.Create(monitor);
                    renderDashboards();
                    if (!Monitor.getMonitor().isRunning())
                        Monitor.getMonitor().start();
                } catch (Exception ignored) {
                    Monitor.reset();
                }
            } else
                Monitor.reset();
        }

        if (!Monitor.IsInstaniated())
            Monitor.Create(this, new OnProcessCompleteListener<ArrayList<String>>() {
                @Override
                public void complete(boolean success, ArrayList<String> source) {
                    if (!success)
                        for (String e : source)
                            Toast.makeText(Globals.getContext(), e, Toast.LENGTH_LONG).show();
                    renderDashboards();
                    Monitor.getMonitor().start();
                }
            });
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        setScreenFadeout();
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

    @SuppressWarnings({"SameReturnValue"})
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.mnu_hubs)
            serviceList();
        else if (id == R.id.mnu_device)
            deviceList();
        else if (id == R.id.mnu_routine)
            routineList();
        else if (id == R.id.mnu_dashboard)
            dashboardList();
        else if (id == R.id.mnu_template)
            templateList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        renderDashboards();
    }

    private void renderDashboards() {
        mDashboardLayout.post(new Runnable() {
            @Override
            public void run() {

                mDashboardLayout.setVisibility(View.GONE);
                mDashboardLayout.reset();

                if (mDashboardRender != null) {
                    mDashboardRender.interrupt();
                    if (mDashboardRender != null)
                        try {
                            mDashboardRender.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }

                mDashboardRender = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            populateDashbboards();
                        } catch (Exception ignored) {
                        } finally {
                            mDashboardRender = null;
                        }
                    }
                });
                mDashboardRender.start();
            }
        });
    }

    public void populateDashbboards() {
        if (Monitor.IsInstaniated())
            Monitor.getMonitor().stop();

        final DBEngine db = new DBEngine(this);
        //db.cleanDataStore();
        mDashboardLoader.post(new Runnable() {
            @Override
            public void run() {
                mDashboardLoader.setVisibility(View.VISIBLE);
            }
        });


        if (mDashboards == null)
            mDashboards = new Dashboards();
        else
            mDashboards.clear();

        for (IDatabaseObject d : db.readFromDatabaseByType(IDatabaseObject.Types.Dashboard))
            mDashboards.add((Dashboard) d);

        mDashboards.sort();

        Monitor.getMonitor().setDashboards(mDashboards);

        mDashboardLayout.post(new Runnable() {
            @Override
            public void run() {
                mDashboardLayout.setDashboards(mDashboards);
                System.gc();

                if (mDashboards.size() == 0) {
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.openDrawer(Gravity.START);
                }
                mDashboardLoader.post(new Runnable() {
                    @Override
                    public void run() {
                        mDashboardLoader.setVisibility(View.GONE);
                        mDashboardLayout.setVisibility(View.VISIBLE);
                    }
                });
                if (Monitor.IsInstaniated() && !Monitor.getMonitor().isRunning())
                    Monitor.getMonitor().start();
            }
        });

        if (mOptions != null)
            mOptions.stopMonitorForScreenFadeOut();

        mOptions = Options.getFromDataStore(this);
        doOptions();
    }

    @Override
    protected void onResume() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        super.onResume();
    }

    public void stopScreenFadeOutMonitor() {
        if (mOptions == null)
            return;

        if (mOptions.isFadeOut())
            mOptions.stopMonitorForScreenFadeOut();
    }

    public void startScreenFadeOutMonitor() {
        if (mOptions == null)
            mOptions = Options.getFromDataStore(this);

        setScreenFadeout();
    }

    private void setScreenFadeout() {
        if (mOptions == null)
            return;

        if (mOptions.isFadeOut()) {
            mOptions.stopMonitorForScreenFadeOut();
            mOptions.startMonitorForScreenFadeOut(new Options.OnFadeTimeElapsedListener() {
                @Override
                public void onElapsed() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ScreenBlocker blocker = findViewById(R.id.main_blocker);
                            blocker.setOnBlockListener(new ScreenBlocker.OnBlockListener() {
                                @Override
                                public void OnShown() {
                                    if (Monitor.IsInstaniated())
                                        Monitor.getMonitor().startSlowCheck();
                                    Broadcaster.broadcastMessage(new ThingChangedMessage("all", ThingChangedMessage.What.Disable));
                                    WindowManager.LayoutParams params = getWindow().getAttributes();
                                    params.screenBrightness = -1;
                                    getWindow().setAttributes(params);
                                }

                                @Override
                                public void OnDismiss() {
                                    if (Monitor.IsInstaniated()) {
                                        WindowManager.LayoutParams params = getWindow().getAttributes();
                                        params.screenBrightness = 1;
                                        getWindow().setAttributes(params);
                                        Broadcaster.broadcastMessage(new ThingChangedMessage("all", ThingChangedMessage.What.Enable));
                                    }
                                }

                                @Override
                                public void OnDismissStart() {
                                    if (Monitor.IsInstaniated()) {
                                        try {
                                            Monitor.getMonitor().stopSlowCheck();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            if (Monitor.getMonitor().isLoaded())
                                blocker.show();
                        }
                    });
                }
            });
        }
    }

    private void doOptions() {
        if (mOptions == null)
            mOptions = Options.getFromDataStore(this);

        mOptions.stopMonitorForScreenFadeOut();
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                if (mOptions.isKeepScreenOn())
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                setScreenFadeout();
            }
        });
    }

    private void showOptions() {
        pauseFadeMonitor();
        final OptionsFragment fragment = new OptionsFragment();
        fragment.setOnOptionChangeListener(new OnOptionsChangedListener() {
            @Override
            public void OnChange(Options options) {
                if (mOptions != null)
                    mOptions.stopMonitorForScreenFadeOut();

                mOptions = options;
                doOptions();
            }
        });
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("settings");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void serviceList() {
        pauseFadeMonitor();
        final ServicesFragment fragment = new ServicesFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("serviceList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void templateList() {
        pauseFadeMonitor();
        final TemplateFragment fragment = new TemplateFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("templateList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void deviceList() {
        pauseFadeMonitor();
        DeviceFragment fragment = new DeviceFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("deviceList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void routineList() {
        mOptions.stopMonitorForScreenFadeOut();
        RoutineFragment fragment = new RoutineFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().addToBackStack("routineList");
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void dashboardList() {
        pauseFadeMonitor();
        DashboardFragment fragment = new DashboardFragment();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (Monitor.IsInstaniated()) {
                if (Monitor.getMonitor().isRunning())
                    Monitor.getMonitor().stop();
                if (Monitor.getMonitor().isLoaded())
                    outState.putString("monitor", Monitor.getMonitor().toJson());
            }
            stopScreenFadeOutMonitor();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
}
