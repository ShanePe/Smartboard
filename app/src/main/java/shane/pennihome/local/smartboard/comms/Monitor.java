package shane.pennihome.local.smartboard.comms;

import android.arch.core.util.Function;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Dashboard;
import shane.pennihome.local.smartboard.data.Dashboards;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.services.ServiceLoader;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.dialogs.LoaderDialog;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IGroupBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 30;//120;
    private static final int SLOW_SECOND_CHECK = 300;
    private static final int LOOP_LOOK_FOR_NEW = 5;//120;
    private static Monitor mMonitor;
    private Things mThings;
    private Services mServices = null;
    private Dashboards mDashboards = null;
    private Thread mMonitorThread = null;
    private boolean mLoaded;
    private boolean mBusy;
    private ServiceLoader mServiceLoader;
    private boolean mStopped;
    private Thread mVerifier;
    private boolean mSlowCheck;

    private Monitor() {
    }

    public static Monitor getMonitor() {
        if (mMonitor == null)
            mMonitor = new Monitor();
        return mMonitor;
    }

    public static void destroy() {
        if (mMonitor == null)
            return;

        mMonitor.stop();

        if (mMonitor.mThings != null)
            mMonitor.mThings.clear();
        if (mMonitor.mServices != null)
            mMonitor.mServices.clear();
        if (mMonitor.mDashboards != null)
            mMonitor.mDashboards.clear();

        mMonitor.mThings = null;
        mMonitor.mServices = null;
        mMonitor.mDashboards = null;

        reset();
    }

    public static boolean IsInstaniated() {
        return (mMonitor != null);
    }

    public static void reset() {
        if (mMonitor != null) {
            mMonitor.stop();
            mMonitor.mMonitorThread = null;
            mMonitor = null;
        }
    }

    public static Monitor Create(String json) throws JSONException {
        getMonitor().fromJson(json);
        return getMonitor();
    }

    public static Monitor Create(final AppCompatActivity activity, final OnProcessCompleteListener<ArrayList<String>> onProcessCompleteListener) {

        try {
            LoaderDialog.AsyncLoaderDialog.run(activity, new Function<Void, Void>() {
                @Override
                public Void apply(Void unused) {
                    LoaderDialog.AsyncLoaderDialog.AddMessage(this.toString(), "Loading ...");
                    getMonitor().setServices(ServiceManager.getActiveServices(activity));
                    LoaderDialog.AsyncLoaderDialog.RemoveMessage(this.toString());
                    final ArrayList<String> errors = new ArrayList<>();
                    ServiceLoader.ServiceLoaderResult source = getMonitor().getThingsFromService(activity);
                    if (source.getResult() != null)
                        getMonitor().setThings(source.getResult());
                    if (source.getErrors() != null)
                        for (String e : source.getErrors().keySet())
                            errors.add(String.format("Error getting things : %s", e));

                    getMonitor().mLoaded = true;

                    if (onProcessCompleteListener != null)
                        onProcessCompleteListener.complete(source.getErrors().size() == 0, errors);
                    return null;
                }
            });

        } catch (Exception ex) {
            Log.e(Globals.ACTIVITY, "Create: failed", ex);
        }
        return getMonitor();
    }

    public boolean isBusy() {
        return mBusy;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public boolean isRunning() {
        try {
            if (mMonitorThread == null)
                return false;
            return mMonitorThread.getState() != Thread.State.TERMINATED;
        } catch (Exception ignore) {
            return false;
        }
    }

    public boolean isSlowCheck() {
        return mSlowCheck;
    }

    public void setSlowCheck(boolean mSlowCheck) {
        this.mSlowCheck = mSlowCheck;
    }

    public Services getServices() {
        return mServices;
    }

    private void setServices(Services services) {
        mServices = services;
    }

    public Dashboards getDashboards() {
        return mDashboards;
    }

    public void setDashboards(Dashboards dashboards) {
        this.mDashboards = dashboards;
    }

    public Things getThings() {
        if (mThings == null)
            mThings = new Things();
        return mThings;
    }

    private void setThings(Things things) {
        mThings = things;
    }

    public <T extends IThing> IThings<T> getThings(Class<T> cls) {
        IThings<T> items = getThings().getOfType(cls);
        items.sort();
        return items;
    }

    private ServiceLoader.ServiceLoaderResult getThingsFromService() {
        return getThingsFromService(null);
    }

    private ServiceLoader.ServiceLoaderResult getThingsFromService(final Context context) {
        return getThingsFromService(context, getServices());
    }

    private ServiceLoader.ServiceLoaderResult getThingsFromService(final Context context, Services services) {
        mServiceLoader = new ServiceLoader();
        for (IService s : services)
            if (s.isValid())
                mServiceLoader.getServices().add(s);
            else if (s.isAwaitingAction() && context != null)
                Toast.makeText(context, String.format("Service %s is awaiting an action.", s.getName()), Toast.LENGTH_LONG).show();

        try {
            return mServiceLoader.getThings();
        } catch (Exception e) {
            if (context != null)
                Toast.makeText(context, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG).show();
            else
                e.printStackTrace();
        } finally {
            mServiceLoader = null;
        }

        return null;
    }

    private IService getServiceForThing(IThing thing) {
        for (IService s : getServices())
            if (s.isActive())
                if (s.getServiceType() == thing.getServiceType())
                    return s;

        return null;
    }

    private void addThingState(Things things, IThing thing) throws Exception {
        if (thing == null)
            return;

        if (thing.isStateful()) {
            IService service = getServiceForThing(thing);
            if (service != null) {
                IThingsGetter getter = service.getThingGetter(thing);
                if (getter != null)
                    things.add(getter.getThingState(thing.clone()));
            }
        }
    }

    private Things getThingsFromDashboards() throws Exception {
        Things things = new Things();
        if (mDashboards == null)
            return things;

        for (Dashboard dashboard : mDashboards)
            for (IBlock block : dashboard.GetBlocks())
                try {
                    if (mStopped)
                        return null;

                    if (Thread.interrupted())
                        return null;

                    if (IGroupBlock.class.isAssignableFrom(block.getClass())) {
                        for (String key : ((IGroupBlock) block).getThingKeys())
                            addThingState(things, getMonitor().getThings().getByKey(key));
                    } else
                        addThingState(things, block.getThing());


                } catch (Exception e) {
                    Log.e("Smartboard", "Error on getThingsFromDashboards: " + e.getMessage());
                }

        return things;
    }

    public void startSlowCheck() {
        stop();
        setSlowCheck(true);
        start();
    }

    public void stopSlowCheck() throws Exception {
        stop();
        while (isBusy())
            Thread.sleep(100);
        setSlowCheck(false);
        verifyThingsOnDashboardAsync(new OnProcessCompleteListener() {
            @Override
            public void complete(boolean success, Object source) {
                start();
            }
        });
    }

    public void start() {
        if (isRunning())
            return;

        mStopped = false;

        Log.i(Globals.ACTIVITY, "Starting Monitor");
        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int full_check = 0;

                    while (true) {
                        if (!isBusy())
                            try {
                                Log.i(Globals.ACTIVITY, "Monitor thread going to sleep for " + (isSlowCheck() ? Monitor.SLOW_SECOND_CHECK : Monitor.SECOND_CHECK) + " seconds");
                                Thread.sleep(1000 * (isSlowCheck() ? Monitor.SLOW_SECOND_CHECK : Monitor.SECOND_CHECK));
                                if (mStopped)
                                    break;

                                mBusy = true;
                                if (isLoaded()) {
                                    if (full_check < Monitor.LOOP_LOOK_FOR_NEW)
                                        verifyThingsOnDashboard();
                                    else {
                                        verifyThingState(getThingsFromService().getResult());
                                        full_check = 0;
                                    }
                                }
                            } catch (InterruptedException ieu) {
                                break;
                            } catch (Exception ex) {
                                if (!isRunning())
                                    break;
                            } finally {
                                full_check++;
                                mBusy = false;
                            }
                    }
                } finally {
                    mMonitorThread = null;
                }
            }
        });

        mMonitorThread.start();
    }

    public void verifyThings() {
        verifyThings(null);
    }

    public void verifyThings(final OnProcessCompleteListener onProcessCompleteListener) {
        if (isBusy())
            return;

        mVerifier = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    ServiceLoader.ServiceLoaderResult results = getThingsFromService();
                    if (results != null)
                        verifyThingState(results.getResult());
                    if (onProcessCompleteListener != null)
                        onProcessCompleteListener.complete(results != null, null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mVerifier = null;
                }
            }
        });

        mVerifier.start();
    }

    public void verifyDashboardThings() {
        verifyDashboardThings(1000);
    }

    public void verifyDashboardThings(int delay) {
        verifyDashboardThings(delay, null);
    }

    public void verifyDashboardThings(final OnProcessCompleteListener onProcessCompleteListener) {
        verifyDashboardThings(1000, onProcessCompleteListener);
    }

    public void verifyDashboardThings(final int delay, final OnProcessCompleteListener onProcessCompleteListener) {
        if (isBusy() || Thread.interrupted())
            return;
        verifyThingsOnDashboardAsync(onProcessCompleteListener);
    }

    private void verifyThingsOnDashboardAsync(OnProcessCompleteListener callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    verifyThingsOnDashboard();
                    if (callback != null)
                        callback.complete(true, null);
                } catch (Exception e) {
                    if (callback != null)
                        callback.complete(false, e);
                }
            }
        }).start();
    }

    private void verifyThingsOnDashboard() throws Exception {
        Things things = getThingsFromDashboards();
        if (things == null)
            return;

        for (IThing t : things) {
            if (mStopped)
                break;

            if (Thread.interrupted())
                break;

            IThing current = getThings().getByKey(t.getKey());
            if (current == null)
                current.setUnreachable(false, true);
            else
                current.verifyState(t);
        }
    }

    public void removeService(IService service) {
        stop();
        try {
            for (IThing t : getThings().getForService(service))
                t.setUnreachable(true, true);
            getServices().remove(service);
        } finally {
            start();
        }
    }

    public void addService(final Context context, IService service) {
        stop();
        try {
            getServices().remove(service.getServiceType());
            getThings().remove(service);

            Services services = new Services();
            services.add(service);
            getServices().add(service);
            ServiceLoader.ServiceLoaderResult source = getThingsFromService(context, services);
            getMonitor().getThings().addAll(source.getResult());
            for (String e : source.getErrors().keySet())
                Toast.makeText(context, String.format("Error getting things : %s", e), Toast.LENGTH_LONG).show();
        } finally {
            start();
        }
    }

    private void verifyThingState(Things currentThings) {
        for (IThing currentThing : getThings()) {
            if (mStopped)
                break;

            if (Thread.interrupted())
                break;

            IThing newThing = currentThings.getbyId(currentThing.getId());

            if (newThing == null) {
                if (!currentThing.isUnreachable())
                    currentThing.setUnreachable(true, true);
            } else {
                if (currentThing.isUnreachable() && !newThing.isUnreachable())
                    currentThing.setUnreachable(false, true);
                else
                    currentThing.verifyState(newThing);

                currentThings.remove(newThing);
            }
        }

        if (currentThings.size() != 0 && !mStopped)
            getThings().addAll(currentThings);
    }

    public void stop() {
        if (!isRunning())
            return;

        mStopped = true;

        if (mServiceLoader != null) {
            mServiceLoader.stop();
            mServiceLoader = null;
        }

        if (mVerifier != null) {
            mVerifier.interrupt();
            mVerifier = null;
        }

        Log.i(Globals.ACTIVITY, "Stopping Monitor");
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            if (mMonitorThread != null)
                try {
                    mMonitorThread = null;
                } catch (Exception ignore) {
                }
        }
    }

    public String toJson() throws JSONException {
        JSONObject ret = new JSONObject();
        JsonBuilder builder = new JsonBuilder();
        ret.put("things", builder.get().toJson(mThings));
        ret.put("services", builder.get().toJson(mServices));
        return ret.toString();
    }

    private void fromJson(String json) throws JSONException {
        JsonBuilder builder = new JsonBuilder();
        JSONObject item = new JSONObject(json);
        getMonitor().setThings(builder.get().fromJson(item.getString("things"), Things.class));
        getMonitor().setServices(builder.get().fromJson(item.getString("services"), Services.class));
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
