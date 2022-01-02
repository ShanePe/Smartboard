package shane.pennihome.local.smartboard.services;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.services.PhilipsHue.HueBridgeService;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsServicePAT;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;

/**
 * Created by shane on 29/01/18.
 */

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
        services.add(new SmartThingsServicePAT());
        services.add(new HueBridgeService());
        return services;
    }

    public void registerService(final AppCompatActivity activity, final IService service, final OnProcessCompleteListener<IService> onProcessCompleteListener) {
        try {
            final IRegisterServiceFragment dialogFragment = service.getRegisterDialog();
            dialogFragment.setService(service);

            dialogFragment.setOnProcessCompleteListener(new OnProcessCompleteListener<IService>() {
                @Override
                public void complete(final boolean success, final IService source) {
                dialogFragment.dismiss();
                if (success)
                        source.register(activity, onProcessCompleteListener);
                 else
                        Toast.makeText(activity, "Could not register service", Toast.LENGTH_LONG).show();
                }
            });

            dialogFragment.show(activity.getSupportFragmentManager(), "Service_Register");
        } catch (Exception ex) {
            Toast.makeText(activity, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void unRegisterService(Context context, IService service, OnProcessCompleteListener<Void> onProcessCompleteListener) {
        service.unregister(context, onProcessCompleteListener);
    }
}
