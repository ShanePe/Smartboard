package shane.pennihome.local.smartboard.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.services.dialogs.LoaderDialog;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 31/01/18.
 */

public class ServiceLoader {
    //AppCompatActivity mActivity;
    private Services mServices;
    //private ServiceLoadDialog mServiceLoadDialog;
    //private OnProcessCompleteListener<ServiceLoaderResult> mOnProcessCompleteListener;

    public ServiceLoader() {
    }



    /*
    public OnProcessCompleteListener<ServiceLoaderResult> getOnProcessCompleteListener() {
        return mOnProcessCompleteListener;
    }

    public void setOnProcessCompleteListener(OnProcessCompleteListener<ServiceLoaderResult> onProcessCompleteListener) {
        this.mOnProcessCompleteListener = onProcessCompleteListener;
    }*/

    public Services getServices() {
        if (mServices == null)
            mServices = new Services();

        return mServices;
    }

    public void setServices(Services services) {
        mServices = services;
    }

    public ServiceLoaderResult getThingsForDashboards(){
        return null;
    }

    public ServiceLoaderResult getThings() {
        ServiceLoaderResult serviceLoaderResult = new ServiceLoaderResult();
        Services services = getServices();
        if (services.size() > 0) {
            ExecutorService executor = Executors.newFixedThreadPool(services.size());
            ArrayList<ServiceCaller> serviceThreads = new ArrayList<>();

            for (IService s : services) {
                serviceThreads.add(new ServiceCaller(s));
                for (IThingsGetter g : s.getThingGetters())
                    LoaderDialog.AsyncLoaderDialog.AddMessage(g.getLoadMessage(), g.getLoadMessage());
            }
            try {
                List<Future<ServiceLoaderResult>> results = executor.invokeAll(serviceThreads);

                for (Future<ServiceLoaderResult> s : results) {
                    serviceLoaderResult.getResult().addAll(s.get().getResult());
                    for (String k : s.get().getErrors().keySet())
                        serviceLoaderResult.getErrors().put(k, serviceLoaderResult.getErrors().get(k));
                }
            } catch (Exception ignore) {
            }
        }

        return serviceLoaderResult;
    }

    private class ServiceCaller implements Callable<ServiceLoaderResult> {

        private IService mService;

        public ServiceCaller(IService service) {
            this.mService = service;
        }

        @Override
        public ServiceLoaderResult call()  {
            ServiceLoaderResult result = new ServiceLoaderResult();

            ArrayList<IThingsGetter> getters = mService.getThingGetters();
            for (IThingsGetter g : getters) {
                try {
                    result.getResult().addAll(g.getThings());
                    LoaderDialog.AsyncLoaderDialog.RemoveMessage(g.getLoadMessage());
                } catch (Exception e) {
                    result.getErrors().put(e.getMessage(), g);
                    LoaderDialog.AsyncLoaderDialog.RemoveMessage(g.getLoadMessage());
                }
            }
            return result;
        }
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