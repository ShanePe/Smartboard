package shane.pennihome.local.smartboard.services;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.HashMap;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.services.dialogs.ServiceLoadDialog;
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


    public class ServiceLoader extends AsyncTask<IThingsGetter, IThingsGetter, ServiceLoader.ServiceLoaderResult> {
        AppCompatActivity mActivity;
        Services mServices;
        ServiceLoadDialog mServiceLoadDialog;
        OnProcessCompleteListener<ServiceLoaderResult> mOnProcessCompleteListener;

        public OnProcessCompleteListener<ServiceLoaderResult> getOnProcessCompleteListener() {
            return mOnProcessCompleteListener;
        }

        public void setOnProcessCompleteListener(OnProcessCompleteListener<ServiceLoaderResult> onProcessCompleteListener) {
            this.mOnProcessCompleteListener = onProcessCompleteListener;
        }

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

        private ServiceLoaderResult getThings() {
            ServiceLoaderResult serviceLoaderResult = new ServiceLoaderResult();

            for (IService s : getServices()) {
                for (IThingsGetter g : s.getThingGetters()) {
                    try {
                        serviceLoaderResult.getResult().addAll(g.getThings());
                        onProgressUpdate(g);
                    } catch (Exception e) {
                        serviceLoaderResult.getErrors().put(e.getMessage(), g);
                    }
                }
            }

            return serviceLoaderResult;
        }

        private void loadDialog() {
            mServiceLoadDialog = ServiceLoadDialog.newInstance(getServices());
            mServiceLoadDialog.show(mActivity.getSupportFragmentManager(), "service_loader");
        }

        @Override
        protected void onPreExecute() {
            if (mActivity != null)
                loadDialog();
        }

        @Override
        protected ServiceLoaderResult doInBackground(IThingsGetter... iThingsGetters) {
            return getThings();
        }

        @Override
        protected void onPostExecute(ServiceLoaderResult result) {
            if (mOnProcessCompleteListener != null)
                mOnProcessCompleteListener.complete(true, result);

            if (mServiceLoadDialog != null)
                mServiceLoadDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(IThingsGetter... values) {
            if (mServiceLoadDialog != null)
                mServiceLoadDialog.setGetterSuccess(values[0]);
        }

        public class ServiceLoaderResult {
            private HashMap<String, IThingsGetter> mErrors;
            private Things mResult;

            public boolean isSuccess() {
                return getErrors().size() == 0;
            }

            public HashMap<String, IThingsGetter> getErrors() {
                if (mErrors == null)
                    mErrors = new HashMap<String, IThingsGetter>();

                return mErrors;
            }

            public Things getResult() {
                if (mResult == null)
                    mResult = new Things();
                return mResult;
            }
        }
    }
}
