package shane.pennihome.local.smartboard.services;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.services.dialogs.ServiceLoadDialog;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 31/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ServiceLoader extends AsyncTask<IThingsGetter, IThingsGetter, ServiceLoader.ServiceLoaderResult> {
    //AppCompatActivity mActivity;
    private Services mServices;
    private ServiceLoadDialog mServiceLoadDialog;
    private OnProcessCompleteListener<ServiceLoaderResult> mOnProcessCompleteListener;

    public ServiceLoader() {
    }

    public ServiceLoader(Context context) {
        if (context != null)
            mServiceLoadDialog = new ServiceLoadDialog(context);
    }

    public OnProcessCompleteListener<ServiceLoaderResult> getOnProcessCompleteListener() {
        return mOnProcessCompleteListener;
    }

    public void setOnProcessCompleteListener(OnProcessCompleteListener<ServiceLoaderResult> onProcessCompleteListener) {
        this.mOnProcessCompleteListener = onProcessCompleteListener;
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
            {
                ArrayList<IThingsGetter> getters = s.getThingGetters();
                for (IThingsGetter g : getters) {
                    try {
                        serviceLoaderResult.getResult().addAll(g.getThings());
                        onProgressUpdate(g);
                    } catch (Exception e) {
                        serviceLoaderResult.getErrors().put(e.getMessage(), g);
                    }
                }
            }
        }

        return serviceLoaderResult;
    }

    @Override
    protected void onPreExecute() {
        if (mServiceLoadDialog != null) {
            mServiceLoadDialog.setServices(getServices());
            mServiceLoadDialog.show();
        }
    }

    @Override
    protected ServiceLoaderResult doInBackground(IThingsGetter... iThingsGetters) {
        return getThings();
    }

    @Override
    protected void onPostExecute(ServiceLoaderResult serviceLoaderResult) {
        //super.onPostExecute(serviceLoaderResult);
        if (mOnProcessCompleteListener != null)
            mOnProcessCompleteListener.complete(serviceLoaderResult.getErrors().size() == 0, serviceLoaderResult);

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
                mErrors = new HashMap<>();

            return mErrors;
        }

        public Things getResult() {
            if (mResult == null)
                mResult = new Things();
            return mResult;
        }
    }
}