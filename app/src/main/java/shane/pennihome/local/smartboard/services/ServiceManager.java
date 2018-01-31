package shane.pennihome.local.smartboard.services;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.dialogs.ServiceLoadDialog;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.thingsframework.Things;

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

    public <T extends IService> void registerService(AppCompatActivity activity, Class<T> cls) {
        try {
            T instance = cls.newInstance();
            DialogFragment dialogFragment = instance.getRegisterDialog();
            Bundle args = new Bundle();
            args.putString("title", "Register SmartThingsService");
            dialogFragment.setArguments(args);

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            dialogFragment.show(activity.getSupportFragmentManager(), "Service_Register");
        } catch (Exception ex) {

            Toast.makeText(activity, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public ServiceLoader getServiceLoader() {
        return new ServiceLoader();
    }

    public class ServiceLoader {
        AppCompatActivity mActivity;
        Services mServices;
        ServiceLoadDialog mServiceLoadDialog;

        public Activity getActivity() {
            return mActivity;
        }

        public void setActivity(AppCompatActivity activity) {
            mActivity = activity;
        }

        public Services getServices() {
            if (mServices == null)
                mServices = new Services();

            return mServices;
        }

        public void setServices(Services services) {
            mServices = services;
        }

//        @Override
//        protected Things doInBackground(IThingsGetter... iThingsGetters) {
//        }

        public Things getThings() {
            onPreExecute();
            Things things = new Things();
            try {
                Thread.yield();
                for (IService s : getServices()) {
                    s.connect();
                    for (IThingsGetter g : s.getThingGetters()) {
                        things.addAll(g.getThings());
                        onProgressUpdate(g);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            onPostExecute(things);
            return things;

        }

        //        @Override
        protected void onPreExecute() {
            //          super.onPreExecute();

            if (mActivity != null)
                loadDialog();
        }

        private void loadDialog() {
            mServiceLoadDialog = new ServiceLoadDialog();
            mServiceLoadDialog.setCancelable(false);
            mServiceLoadDialog.setServices(getServices());

            mServiceLoadDialog.show(mActivity.getSupportFragmentManager(), "service_loader");
        }

        //        @Override
        protected void onPostExecute(Things iThings) {
//            super.onPostExecute(iThings);
            if (mServiceLoadDialog != null)
                mServiceLoadDialog.dismiss();
        }

        //        @Override
        protected void onProgressUpdate(IThingsGetter... values) {
            if (mServiceLoadDialog != null)
                mServiceLoadDialog.setGetterSuccess(values[0]);
        }
    }
}
