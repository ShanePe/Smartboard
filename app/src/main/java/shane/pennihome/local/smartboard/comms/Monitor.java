package shane.pennihome.local.smartboard.comms;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Map;

import shane.pennihome.local.smartboard.comms.interfaces.IController;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.comms.philipshue.PHBridgeController;
import shane.pennihome.local.smartboard.comms.smartthings.STController;
import shane.pennihome.local.smartboard.data.TokenHueBridge;
import shane.pennihome.local.smartboard.data.TokenSmartThings;
import shane.pennihome.local.smartboard.data.interfaces.ITokenInfo;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 120;
    private static Things mThings;
    private final Activity mActivity;
    private Thread mMonitorThread = null;
    private static Services mServices = null;

    public static Services getServices() {
        return mServices;
    }

    public static void setServices(Services services) {
        Monitor.mServices = services;
    }

    public static Things getThings() {
        if(mThings == null)
            mThings = new Things();
        return Monitor.mThings;
    }

    public static void setThings(Things mThings) {
        Monitor.mThings = mThings;
    }

    public static  <T extends IThing> IThings<T> getThings(Class<T> cls)
    {
        IThings<T> items = getThings().getOfType(cls);
        items.sort();
        return items;
    }


    public Monitor(final AppCompatActivity activity) {
        mActivity = activity;
        setServices(ServiceManager.getActiveServices(activity));
        processThings(activity,new OnProcessCompleteListener<ServiceManager.ServiceLoader.ServiceLoaderResult>() {
            @Override
            public void complete(boolean success, ServiceManager.ServiceLoader.ServiceLoaderResult source) {
                getThings().clear();
                setThings(source.getResult());
                for(String e:source.getErrors().keySet())
                    if (activity != null)
                        Toast.makeText(activity, String.format("Error getting things : %s", e), Toast.LENGTH_LONG);
            }
        });
    }

    private void processThings(OnProcessCompleteListener onProcessCompleteListener)
    {
        processThings(null, onProcessCompleteListener);
    }

    private void processThings(final AppCompatActivity activity, OnProcessCompleteListener onProcessCompleteListener)
    {
        ServiceManager.ServiceLoader loader = new ServiceManager().getServiceLoader();
        loader.setActivity(activity);
        for (IService s : getServices())
            if (s.isValid())
                loader.getServices().add(s);
            else if (s.isAwaitingAction() && activity != null)
                Toast.makeText(activity, String.format("Service %s is awaiting an action.", s.getName()), Toast.LENGTH_LONG);
        try {
            if(onProcessCompleteListener != null)
                loader.setOnProcessCompleteListener(onProcessCompleteListener);
            loader.execute();

        } catch (Exception e) {
            if (activity != null)
                Toast.makeText(activity, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG);
            else
                e.printStackTrace();
        }
    }

    public void Start() {
        Stop();

        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                      Thread.sleep(1000 * Monitor.SECOND_CHECK);
                      processThings();

                    } catch (Exception ex) {
                    }

                }
            }
        });

        mMonitorThread.start();
    }

    private void checkStateChange() {
        for (Switch d : getThings(Switch.class)) {
            if (d.getService() == type) {
                Switch s = src.getbyId(d.getId());
                if (s == null)
                    d.setState(Switch.States.Unreachable);
                else {
                    if (s.getState() != d.getState())
                        d.setState(s.getState());
                    src.remove(s);
                }
            }
        }

        if (src.size() != 0)
            mThings.addAll(src);
    }

//
//    public void getSmartThingsThings(final OnProcessCompleteListener<STController> processComplete) {
//        getThings(IService.ServicesTypes.SmartThings, new OnProcessCompleteListener<IController>() {
//            @Override
//            public void complete(boolean success, IController source) {
//                if (processComplete != null)
//                    processComplete.complete(success, (STController) source);
//            }
//        });
//    }
//
//    public void getHueBridgeThings(final OnProcessCompleteListener<PHBridgeController> processComplete) {
//        getThings(IService.ServicesTypes.PhilipsHue, new OnProcessCompleteListener<IController>() {
//            @Override
//            public void complete(boolean success, IController source) {
//                if (processComplete != null)
//                    processComplete.complete(success, (PHBridgeController) source);
//            }
//        });
//    }

//    private void getThings(IService.ServicesTypes type, final OnProcessCompleteListener<IController> processComplete) {
//        try {
//            if(mThings == null)
//                mThings = new Things();
//            else
//                mThings.remove(type);
//
//            SourceInfo s = new SourceInfo(type, mActivity);
//
//            if (s.getToken().isAwaitingAuthorisation())
//                s.getController().getAll(new OnProcessCompleteListener<IController>() {
//                    @Override
//                    public void complete(boolean success, IController source) {
//                        if (success) {
//                            mThings.addAll(source.getThings());
//                        }
//                        if (processComplete != null)
//                            processComplete.complete(success, source);
//                    }
//                });
//            else if (s.getToken().isAuthorised())
//                s.getController().getThings(new OnProcessCompleteListener<IController>() {
//                    @Override
//                    public void complete(boolean success, IController source) {
//                        if (success) {
//                            mThings.addAll(source.getThings());
//                        }
//
//                        if (processComplete != null)
//                            processComplete.complete(success, source);
//                    }
//                });
//            else if (processComplete != null)
//                processComplete.complete(true, s.getController());
//
//        } catch (Exception ex) {
//            if (mActivity != null)
//                Toast.makeText(mActivity, "Error gettings things for " + type.name(), Toast.LENGTH_SHORT).show();
//
//            if (processComplete != null)
//                processComplete.complete(true, null);
//        }
//    }

//    private void monitorThings(final IService.ServicesTypes type) {
//        try {
//            SourceInfo s = new SourceInfo(type);
//            if (s.getToken().isAuthorised()) {
//                s.getController().getDevices(new OnProcessCompleteListener<IThings<Switch>>() {
//                    @Override
//                    public void complete(boolean success, IThings<Switch> source) {
//                        if (success)
//                            checkStateChange(source, type);
//                    }
//                });
//            }
//        } catch (Exception ex) {
//        }//Don't crash on monitor thread.
//    }

//    private void checkStateChange(IThings<Switch> src, IService.ServicesTypes type) {
//        for (Switch d : getThings(Switch.class)) {
//            if (d.getService() == type) {
//                Switch s = src.getbyId(d.getId());
//                if (s == null)
//                    d.setState(Switch.States.Unreachable);
//                else {
//                    if (s.getState() != d.getState())
//                        d.setState(s.getState());
//                    src.remove(s);
//                }
//            }
//        }
//
//        if (src.size() != 0)
//            mThings.addAll(src);
//    }

//    public void Start() {
//        Stop();
//
//        mMonitorThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(1000 * Monitor.SECOND_CHECK);
//
//                        monitorThings(IService.ServicesTypes.SmartThings);
//                        monitorThings(IService.ServicesTypes.PhilipsHue);
//                    } catch (Exception ex) {
//                    }
//
//                }
//            }
//        });
//
//        mMonitorThread.start();
//    }

    private void Stop() {
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            mMonitorThread = null;
        }
    }

    private class SourceInfo {
        private ITokenInfo mToken = null;
        private IController mController = null;
        private Activity mActivity = null;

        public SourceInfo(IService.ServicesTypes servicesTypes, Activity activity) throws Exception {
            mActivity = activity;
            getTokenAndController(servicesTypes);
        }

        public SourceInfo(IService.ServicesTypes servicesTypes) throws Exception {
            getTokenAndController(servicesTypes);
        }

        private void getTokenAndController(IService.ServicesTypes type) throws Exception {
            if (type == IService.ServicesTypes.SmartThings) {
                mToken = ITokenInfo.Load(TokenSmartThings.class);
                mController = new STController(mActivity);
            } else if (type == IService.ServicesTypes.PhilipsHue) {
                mToken = ITokenInfo.Load(TokenHueBridge.class);
                mController = new PHBridgeController(mActivity);
            } else
                throw new Exception("Invalid ServicesTypes Type");
        }

        public ITokenInfo getToken() {
            return mToken;
        }

        public IController getController() {
            return mController;
        }
    }
}
