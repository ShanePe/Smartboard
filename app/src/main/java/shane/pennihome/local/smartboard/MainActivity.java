package shane.pennihome.local.smartboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import shane.pennihome.local.smartboard.Comms.SmartThings.STDevicesGetter;
import shane.pennihome.local.smartboard.Comms.SmartThings.STEndPointGetter;
import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.SmartThings.STTokenGetter;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.SmartThingsTokenInfo;
import shane.pennihome.local.smartboard.Fragments.DeviceFragment;
import shane.pennihome.local.smartboard.Fragments.RoutineFragment;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ThingFragment.OnListFragmentInteractionListener {

    private List<Device> mDevices = new ArrayList<>();
    private List<Routine> mRoutines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
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

        if (id == R.id.btn_authst_mnu)
            getAuthorToken();
        else if (id == R.id.btn_device_mnu)
            deviceList();
        else if (id == R.id.btn_routine_mnu)
            routineList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void init() {
        SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
        Calendar c = Calendar.getInstance();
        if (smartThingsTokenInfo.getToken() == null ||
                Objects.equals(smartThingsTokenInfo.getToken(), "") ||
                smartThingsTokenInfo.getExpires().before(c.getTime()))
            getAuthorToken();
         else
            this.getDevices();
    }

    private void getAuthorToken()
    {
        final MainActivity me = this;
        this.authorise(new ProcessCompleteListener<Activity>() {
            @Override
            public void Complete(boolean success, Activity source) {
                if(success)
                    me.getDevices();
            }
        });
    }

    private void getDevices() {
        mDevices = null;
        mRoutines = null;
        final MainActivity me = this;
        STEndPointGetter endPointGetter = new STEndPointGetter(new ProcessCompleteListener<STEndPointGetter>() {
            @Override
            public void Complete(boolean success, STEndPointGetter source) {
                if (success) {
                    SmartThingsTokenInfo smartThingsTokenInfo = SmartThingsTokenInfo.Load();
                    STDevicesGetter devicesGetter = new STDevicesGetter(smartThingsTokenInfo.getRequestUrl(), true, me, new ProcessCompleteListener<STDevicesGetter>() {
                        @Override
                        public void Complete(boolean success, STDevicesGetter source) {
                            if (success) {
                                mDevices = source.getDevices();
                                mRoutines = source.getRoutines();
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
    }

    private void deviceList() {
        DeviceFragment fragment = new DeviceFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>)(List<?>) mDevices);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    private void routineList() {
        RoutineFragment fragment = new RoutineFragment();
        //noinspection unchecked
        fragment.setThings((List<Thing>)(List<?>) mRoutines);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void authorise(final ProcessCompleteListener<Activity> processComplete) {
        Log.i(Globals.ACTIVITY, "authorise");
        final Dialog auth_dialog = new Dialog(MainActivity.this);
        auth_dialog.setContentView(R.layout.auth_dialog);
        WebView web = auth_dialog.findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);

        // check request
        String requestUrl = (Globals.OAUTH_URL +
                "?redirect_uri=" + Globals.REDIRECT_URI +
                "&response_type=code&client_id=" + Globals.CLIENT_ID +
                "&scope=" + Globals.OAUTH_SCOPE +
                "&redirect_uri=" + Globals.SERVEUR_URI);

        // Loading of the Smartthing Webside : For authorization
        web.loadUrl(requestUrl);
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                MainActivity.this.setTitle("Loading...");
                MainActivity.this.setProgress(progress * 100);

                if(progress == 100)
                    MainActivity.this.setTitle(R.string.app_name);
            }
        });

        web.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;
            final Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(Globals.ACTIVITY, "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(Globals.ACTIVITY, "onPageFinished");
                super.onPageFinished(view, url);
                // Check if the answer contains a code
                if (url.contains("?code=") && !authComplete) {
                    // check answer

                    Uri uri = Uri.parse(url);

                    // Code recovery
                    SmartThingsTokenInfo smartThingsTokenInfo = new SmartThingsTokenInfo();
                    smartThingsTokenInfo.setAuthCode(uri.getQueryParameter("code"));
                    Log.i(Globals.ACTIVITY, "Auth Code :" + smartThingsTokenInfo.getAuthCode());

                    authComplete = true;
                    resultIntent.putExtra("code", smartThingsTokenInfo.getAuthCode());

                    MainActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    smartThingsTokenInfo.Save();
                    auth_dialog.dismiss();

                    // Application by Token
                    STTokenGetter tokenGetter = new STTokenGetter(new ProcessCompleteListener<STTokenGetter>() {
                        @Override
                        public void Complete(boolean success, STTokenGetter source) {
                            if(processComplete != null)
                                processComplete.Complete(success, MainActivity.this);
                        }
                    }, MainActivity.this);
                    tokenGetter.execute();

                } else if (url.contains("error=access_denied")) {
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();

                    auth_dialog.dismiss();
                    if(processComplete!=null)
                        processComplete.Complete(false, MainActivity.this);
                }
            }
        });

        auth_dialog.show();
        auth_dialog.setCancelable(true);
    }

    @Override
    public void onListFragmentInteraction(Thing item) {

    }
}
