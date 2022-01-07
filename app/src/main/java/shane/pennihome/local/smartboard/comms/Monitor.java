package shane.pennihome.local.smartboard.comms;

import android.arch.core.util.Function;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int LOOP_LOOK_FOR_NEW = 5;//120;
    private static Monitor mMonitor;
    private Things mThings;
    private Services mServices = null;
    private Dashboards mDashboards = null;
    private Thread mMonitorThread = null;
    private boolean mLoaded;
    private boolean mBusy;

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

    public static Monitor Create(final AppCompatActivity activity, final OnProcessCompleteListener<Void> onProcessCompleteListener) {
        try {
            LoaderDialog.AsyncLoaderDialog.run(activity, new Function<Void, Void>() {
                @Override
                public Void apply(Void unused) {
                    LoaderDialog.AsyncLoaderDialog.AddMessage(this.toString(), "Loading ...");
                    getMonitor().setServices(ServiceManager.getActiveServices(activity));
                    LoaderDialog.AsyncLoaderDialog.RemoveMessage(this.toString());

                    ServiceLoader.ServiceLoaderResult source = getMonitor().getThingsFromService(activity);
                    if (source.getResult() != null)
                        getMonitor().setThings(source.getResult());
                    if (source.getErrors() != null)
                        for (String e : source.getErrors().keySet())
                            Toast.makeText(activity, String.format("Error getting things : %s", e), Toast.LENGTH_LONG).show();
                    getMonitor().mLoaded = true;

                    if (onProcessCompleteListener != null)
                        onProcessCompleteListener.complete(source.getErrors().size() == 0, null);
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
        if (mMonitorThread == null)
            return false;
        return mMonitorThread.getState() != Thread.State.TERMINATED;
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
        ServiceLoader loader = new ServiceLoader();
        for (IService s : services)
            if (s.isValid())
                loader.getServices().add(s);
            else if (s.isAwaitingAction() && context != null)
                Toast.makeText(context, String.format("Service %s is awaiting an action.", s.getName()), Toast.LENGTH_LONG);

        try {
            return loader.getThings();
        } catch (Exception e) {
            if (context != null)
                Toast.makeText(context, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG);
            else
                e.printStackTrace();
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
                if (getter != null && !things.hasThingWithKey(thing.getKey()))
                    things.add(getter.getThingState(thing.clone()));
            }
        }
    }

    private Things getThingsFromDashboards() throws Exception {
        Things things = new Things();
        for (Dashboard dashboard : mDashboards)
            for (IBlock block : dashboard.GetBlocks())
                try {
                    if(Thread.interrupted())
                        return null;
                    if (IGroupBlock.class.isAssignableFrom(block.getClass())) {
                        for (String key : ((IGroupBlock) block).getThingKeys())
                            if (!things.hasThingWithKey(key))
                                addThingState(things, getMonitor().getThings().getByKey(key));
                    } else
                        addThingState(things, block.getThing());


                } catch (Exception e) {
                    Log.e("Smartboard", "Error on getThingsFromDashboards: " + e.getMessage());
                }

        return things;
    }

    public void start() {
        if (isRunning())
            return;

        Log.i(Globals.ACTIVITY, "Starting Monitor");
        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int full_check = 0;

                    while (true) {
                        if (!isBusy())
                            try {
                                Thread.sleep(1000 * Monitor.SECOND_CHECK);
                                mBusy = true;
                                if (isLoaded()) {
                                    if (full_check < Monitor.LOOP_LOOK_FOR_NEW)
                                        verifyThingsOnDashboard();
                                    else
                                        verifyThingState(getThingsFromService().getResult());
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

        new Thread(new Runnable() {
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
                }
            }
        }).start();
    }

    public void verifyDashboardThings() {
        verifyDashboardThings(null);
    }

    public void verifyDashboardThings(final OnProcessCompleteListener onProcessCompleteListener) {
        if (isBusy() || Thread.interrupted())
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    verifyThingsOnDashboard();

                    if (onProcessCompleteListener != null)
                        onProcessCompleteListener.complete(true, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void verifyThingsOnDashboard() throws Exception {
        for (IThing t : getThingsFromDashboards()) {
            if(Thread.interrupted())
                return;

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
                Toast.makeText(context, String.format("Error getting things : %s", e), Toast.LENGTH_LONG);
        } finally {
            start();
        }
    }

    private void verifyThingState(Things currentThings) {
        for (IThing currentThing : getThings()) {
            if(Thread.interrupted())
                return;;

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

        if (currentThings.size() != 0)
            getThings().addAll(currentThings);
    }

    public void stop() {
        if (!isRunning())
            return;

        Log.i(Globals.ACTIVITY, "Stopping Monitor");
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            if (mMonitorThread != null)
                try {
                    mMonitorThread = null;
                } catch (Exception ignore) {}
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
