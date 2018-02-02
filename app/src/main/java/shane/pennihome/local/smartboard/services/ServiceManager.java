package shane.pennihome.local.smartboard.services;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.services.PhillipsHue.PhillipsHueService;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.ui.listeners.OnPropertyWindowListener;

/**
 * Created by shane on 29/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ServiceManager {
    public ServiceManager() {
    }

    public static Services getActiveServices(Context context) {
        Services services = new Services();
        DBEngine dbEngine = new DBEngine(context);
        for (IDatabaseObject s : dbEngine.readFromDatabaseByType(IDatabaseObject.Types.Service))
            services.add((IService) s);
        return services;
    }

    public static Services getServices() {
        Services services = new Services();
        services.add(new SmartThingsService());
        services.add(new PhillipsHueService());
        return services;
    }

    public void registerService(final AppCompatActivity activity, final IService service, final OnProcessCompleteListener<IService> onProcessCompleteListener) {
        try {
            IRegisterServiceFragment dialogFragment = service.getRegisterDialog();
            dialogFragment.setService(service);
            dialogFragment.setOnProcessCompleteListener(new OnProcessCompleteListener<IService>() {
                @Override
                public void complete(boolean success, IService source) {
                    if(success)
                    {
                        try {
                            service.register(activity);
                            new DBEngine(activity).writeToDatabase(source);
                            Monitor.getMonitor().AddService(activity, source);
                        } catch (Exception e) {
                            Toast.makeText(activity,"Could not register service : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    if(onProcessCompleteListener != null)
                        onProcessCompleteListener.complete(success, source);
                }
            });
            dialogFragment.show(activity.getSupportFragmentManager(), "Service_Register");
        } catch (Exception ex) {
            Toast.makeText(activity, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unRegisterService(Context context, IService service, OnProcessCompleteListener onProcessCompleteListener)
    {
        service.unregister();

        IService storedService = Monitor.getMonitor().getServices().getByType(service.getServiceType());
        if(storedService!=null) {
            DBEngine engine = new DBEngine(context);
            engine.deleteFromDatabase(storedService);

            Monitor.getMonitor().removeService(storedService);
        }
        if(onProcessCompleteListener!=null)
            onProcessCompleteListener.complete(true, null);
    }
}
