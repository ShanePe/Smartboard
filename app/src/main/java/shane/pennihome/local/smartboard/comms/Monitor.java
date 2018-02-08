package shane.pennihome.local.smartboard.comms;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.JsonBuilder;
import shane.pennihome.local.smartboard.services.ServiceLoader;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.switches.Switch;
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
            }
        });
        return getMonitor();
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

    private void getThingsFromService(final Context context, Services services, OnProcessCompleteListener<ServiceLoader.ServiceLoaderResult> onProcessCompleteListener) {
        ServiceLoader loader = new ServiceLoader(context);
        for (IService s : services)
            if (s.isValid())
                loader.getServices().add(s);
            else if (s.isAwaitingAction() && context != null)
                Toast.makeText(context, String.format("Service %s is awaiting an action.", s.getName()), Toast.LENGTH_LONG);
        try {
            loader.setOnProcessCompleteListener(onProcessCompleteListener);
            loader.execute();

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
        stop();

        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * Monitor.SECOND_CHECK);
                        updateThingsFromService();
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
                updateThingsFromService();
            }
        }).start();
    }

    public void removeService(IService service) {
        for (IThing t : getThings().getForService(service))
            t.setUnreachable(true, true);
        getServices().remove(service);
    }

    public void AddService(final Context context, IService service) {
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
    }

    private void verifyThingState(Things currentThings) {
        for (IThing currentThing : getThings()) {
            IThing newThing = currentThings.getbyId(currentThing.getId());

            if (newThing == null) {
                if(!currentThing.isUnreachable())
                    currentThing.setUnreachable(true, true);
            } else {
                if(currentThing.isUnreachable() && !newThing.isUnreachable())
                    currentThing.setUnreachable(false, true);

                if (currentThing instanceof Switch) {
                    Switch currentSwitch = (Switch) currentThing;
                    Switch newSwitch = (Switch) newThing;
                    if (currentSwitch.isOn() != newSwitch.isOn()) {
                        currentSwitch.setOn(newSwitch.isOn(), true);
                    }
                }
                currentThings.remove(newThing);
            }
        }

        if (currentThings.size() != 0)
            getThings().addAll(currentThings);
    }


    private void stop() {
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            mMonitorThread = null;
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
        getMonitor().setServices(builder.get().fromJson(item.getString("things"), Services.class));
    }
}
