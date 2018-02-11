package shane.pennihome.local.smartboard.comms;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.services.ServiceLoader;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 30;//120;
    private static Monitor mMonitor;
    private Things mThings;
    private Services mServices = null;
    private Thread mMonitorThread = null;
    private boolean mLoaded;
    private ServiceLoader mLoader;

    private Monitor() {
    }

    public static Monitor getMonitor() {
        if (mMonitor == null)
            mMonitor = new Monitor();
        return mMonitor;
    }

    public static boolean IsInstaniated() {
        return (mMonitor != null);
    }

    public static void reset() {
        if (mMonitor != null) {
            mMonitor.stop();
            mMonitor = null;
        }
    }

    public static Monitor Create(String json) throws JSONException {
        getMonitor().fromJson(json);
        return getMonitor();
    }

    public static Monitor Create(final AppCompatActivity activity, final OnProcessCompleteListener<Void> onProcessCompleteListener) {
        getMonitor().setServices(ServiceManager.getActiveServices(activity));

        getMonitor().getThingsFromService(activity, new OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult>() {
            @Override
            public void complete(boolean success, final ServiceLoader.ServiceLoaderResult source) {
                if (success)
                    getMonitor().setThings(source.getResult());
                else
                    for (String e : source.getErrors().keySet())
                        Toast.makeText(activity, String.format("Error getting things : %s", e), Toast.LENGTH_LONG).show();
                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(true, null);
                getMonitor().mLoaded = true;

            }
        });
        return getMonitor();
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    public boolean isRunning() {
        return mMonitorThread != null;
    }

    public Services getServices() {
        return mServices;
    }

    private void setServices(Services services) {
        mServices = services;
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

    private void getThingsFromService(OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult> onProcessCompleteListener) {
        getThingsFromService(null, onProcessCompleteListener);
    }

    private void getThingsFromService(final Context context, OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult> onProcessCompleteListener) {
        getThingsFromService(context, getServices(), onProcessCompleteListener);
    }

    private void getThingsFromService(final Context context, Services services, final OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult> onProcessCompleteListener) {
        mLoader = new ServiceLoader(context);
        for (IService s : services)
            if (s.isValid())
                mLoader.getServices().add(s);
            else if (s.isAwaitingAction() && context != null)
                Toast.makeText(context, String.format("Service %s is awaiting an action.", s.getName()), Toast.LENGTH_LONG);
        try {
            mLoader.setOnProcessCompleteListener(new OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult>() {
                @Override
                public void complete(boolean success, ServiceLoader.ServiceLoaderResult source) {
                    onProcessCompleteListener.complete(success, source);
                    mLoader = null;
                }
            });
            mLoader.execute();

        } catch (Exception e) {
            if (context != null)
                Toast.makeText(context, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG);
            else
                e.printStackTrace();
        }
    }

    private void updateThingsFromService() {
        getThingsFromService(new OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult>() {
            @Override
            public void complete(boolean success, ServiceLoader.ServiceLoaderResult source) {
                verifyThingState(source.getResult());
            }
        });
    }

    public void start() {
        if (isRunning())
            return;

        Log.i("Starting Monitor,", Globals.ACTIVITY);
        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * Monitor.SECOND_CHECK);
                        updateThingsFromService();
                    } catch (InterruptedException ieu) {
                        break;
                    } catch (Exception ex) {
                    }
                }
            }
        });

        mMonitorThread.start();
    }

    public void verifyThings() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    updateThingsFromService();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    public void AddService(final Context context, IService service) {
        stop();
        try {
            getServices().remove(service.getServiceType());
            getThings().remove(service);

            Services services = new Services();
            services.add(service);
            getServices().add(service);
            getThingsFromService(context, services, new OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult>() {
                @Override
                public void complete(boolean success, ServiceLoader.ServiceLoaderResult source) {
                    getMonitor().getThings().addAll(source.getResult());
                    for (String e : source.getErrors().keySet())
                        Toast.makeText(context, String.format("Error getting things : %s", e), Toast.LENGTH_LONG);
                }
            });
        } finally {
            start();
        }
    }

    private void verifyThingState(Things currentThings) {
        for (IThing currentThing : getThings()) {
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

        Log.i("Stopping Montor,", Globals.ACTIVITY);
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            mMonitorThread = null;
        }

        if (mLoader != null) {
            mLoader.dismissDialog();
            mLoader.cancel(true);
            mLoader = null;
        }

        while (mMonitorThread != null)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
