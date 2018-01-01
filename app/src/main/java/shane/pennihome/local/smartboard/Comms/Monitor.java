package shane.pennihome.local.smartboard.Comms;

import android.app.Activity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Comms.Interface.IController;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeController;
import shane.pennihome.local.smartboard.Comms.SmartThings.STController;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Interface.TokenInfo;
import shane.pennihome.local.smartboard.Data.Routines;
import shane.pennihome.local.smartboard.Data.TokenHueBridge;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 30;
    private final Activity mActivity;
    private Thread mMonitorThread = null;
    private Devices mDevices = new Devices();
    private Routines mRoutines = new Routines();

    public Monitor(Activity activity) {
        mActivity = activity;

        getThings(Thing.Source.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                getThings(Thing.Source.PhilipsHue, new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                    }
                });
            }
        });
    }

    public Devices getDevices() {
        return mDevices;
    }

    public void setDevices(Devices devices) {
        this.mDevices = devices;
    }

    public shane.pennihome.local.smartboard.Data.Routines getRoutines() {
        return mRoutines;
    }

    public void setRoutines(shane.pennihome.local.smartboard.Data.Routines routines) {
        mRoutines = routines;
    }

    public void getSmartThingsThings(final OnProcessCompleteListener<STController> processComplete) {
        getThings(Thing.Source.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (STController) source);
            }
        });
    }

    public void getHueBridgeThings(final OnProcessCompleteListener<PHBridgeController> processComplete) {
        getThings(Thing.Source.PhilipsHue, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (PHBridgeController) source);
            }
        });
    }

    private void getThings(Thing.Source type, final OnProcessCompleteListener<IController> processComplete) {
        try {
            mDevices.remove(type);
            mRoutines.remove(type);

            SourceInfo s = new SourceInfo(type, mActivity);

            if (s.getToken().isAwaitingAuthorisation())
                s.getController().getAll(new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                        if (success) {
                            mDevices.addAll(source.Devices());
                            mRoutines.addAll(source.Routine());
                            sortThings();
                        }
                        if (processComplete != null)
                            processComplete.complete(success, source);
                    }
                });
            else if (s.getToken().isAuthorised())
                s.getController().getThings(new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                        if (success) {
                            mDevices.addAll(source.Devices());
                            mRoutines.addAll(source.Routine());
                            sortThings();
                        }

                        if (processComplete != null)
                            processComplete.complete(success, source);
                    }
                });
            else if (processComplete != null)
                processComplete.complete(true, s.getController());

        } catch (Exception ex) {
            if (mActivity != null)
                Toast.makeText(mActivity, "Error gettings things for " + type.name(), Toast.LENGTH_SHORT).show();

            if (processComplete != null)
                processComplete.complete(false, null);
        }
    }

    private void monitorThings(final Thing.Source type) {
        try {
            SourceInfo s = new SourceInfo(type);
            if (s.getToken().isAuthorised()) {
                s.getController().getDevices(new OnProcessCompleteListener<Devices>() {
                    @Override
                    public void complete(boolean success, Devices source) {
                        if (success)
                            checkStateChange(source, type);
                    }
                });
            }
        } catch (Exception ex) {
        }//Don't crash on monitor thread.
    }

    private void checkStateChange(Devices src, Thing.Source type) {
        for (Device d : mDevices) {
            if (d.getSource() == type) {
                Device s = src.getbyId(d.getId());
                if (s == null)
                    d.setState(Device.States.Unreachable);
                else {
                    if (s.getState() != d.getState())
                        d.setState(s.getState());
                    src.remove(s);
                }
            }
        }

        if (src.size() != 0) {
            mDevices.addAll(src);
            mDevices.sort();
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

                        monitorThings(Thing.Source.SmartThings);
                        monitorThings(Thing.Source.PhilipsHue);
                    } catch (Exception ex) {
                    }

                }
            }
        });

        mMonitorThread.start();
    }

    private void Stop() {
        if (mMonitorThread != null) {
            mMonitorThread.interrupt();
            mMonitorThread = null;
        }
    }

    private void sortThings() {
        mDevices.sort();
        mRoutines.sort();
    }

    private class SourceInfo {
        private TokenInfo mToken = null;
        private IController mController = null;
        private Activity mActivity = null;

        public SourceInfo(Thing.Source source, Activity activity) throws Exception {
            mActivity = activity;
            getTokenAndController(source);
        }

        public SourceInfo(Thing.Source source) throws Exception {
            getTokenAndController(source);
        }

        private void getTokenAndController(Thing.Source type) throws Exception {
            if (type == Thing.Source.SmartThings) {
                mToken = TokenInfo.Load(TokenSmartThings.class);
                mController = new STController(mActivity);
            } else if (type == Thing.Source.PhilipsHue) {
                mToken = TokenInfo.Load(TokenHueBridge.class);
                mController = new PHBridgeController(mActivity);
            } else
                throw new Exception("Invalid Source Type");
        }

        public TokenInfo getToken() {
            return mToken;
        }

        public IController getController() {
            return mController;
        }
    }
}
