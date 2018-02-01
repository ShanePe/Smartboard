package shane.pennihome.local.smartboard.services;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsService;
import shane.pennihome.local.smartboard.services.interfaces.IService;

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
        return services;
    }

    public <T extends IService> void registerService(AppCompatActivity activity, Class<T> cls) {
        try {
            T instance = cls.newInstance();
            DialogFragment dialogFragment = instance.getRegisterDialog();
            Bundle args = new Bundle();
            args.putString("title", "Register SmartThingsService");
            dialogFragment.setArguments(args);

            @SuppressWarnings("unused") FragmentManager fragmentManager = activity.getSupportFragmentManager();
            dialogFragment.show(activity.getSupportFragmentManager(), "Service_Register");
        } catch (Exception ex) {

            Toast.makeText(activity, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    public ServiceLoader getServiceLoader() {
//        return new ServiceLoader();
//    }
}
