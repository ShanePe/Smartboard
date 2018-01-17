package shane.pennihome.local.smartboard.Comms;

import android.app.Activity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Comms.Interface.IController;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeController;
import shane.pennihome.local.smartboard.Comms.SmartThings.STController;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Interface.TokenInfo;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.Routines;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.Data.TokenHueBridge;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 120;
    private final Activity mActivity;
    private Thread mMonitorThread = null;
    //private Devices mDevices = new Devices();
    //private Routines mRoutines = new Routines();
    private Things mThings = new Things();

    public Monitor(Activity activity) {
        mActivity = activity;

        getThings(IThing.Source.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                getThings(IThing.Source.PhilipsHue, new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                    }
                });
            }
        });
    }

    public Devices getDevices() {
        Devices ret = new Devices();
        ret.addAll(mThings.getOfType(Device.class));
        ret.sort();
        return ret;
    }

    public Routines getRoutines() {
        Routines ret = new Routines();
        ret.addAll(mThings.getOfType(Routine.class));
        ret.sort();
        return ret;
    }

    public void getSmartThingsThings(final OnProcessCompleteListener<STController> processComplete) {
        getThings(IThing.Source.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (STController) source);
            }
        });
    }

    public void getHueBridgeThings(final OnProcessCompleteListener<PHBridgeController> processComplete) {
        getThings(IThing.Source.PhilipsHue, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (PHBridgeController) source);
            }
        });
    }

    private void getThings(IThing.Source type, final OnProcessCompleteListener<IController> processComplete) {
        try {
            //mDevices.remove(type);
            //mRoutines.remove(type);
            mThings.remove(type);
            SourceInfo s = new SourceInfo(type, mActivity);

            if (s.getToken().isAwaitingAuthorisation())
                s.getController().getAll(new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                        if (success) {
                            mThings.addAll(source.Devices());
                            mThings.addAll(source.Routine());
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
                            mThings.addAll(source.Devices());
                            mThings.addAll(source.Routine());
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
                processComplete.complete(true, null);
        }
    }

    private void monitorThings(final IThing.Source type) {
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

    private void checkStateChange(Devices src, IThing.Source type) {
        for (Device d : getDevices()) {
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

        if (src.size() != 0)
            mThings.addAll(src);
    }

    public void Start() {
        Stop();

        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * Monitor.SECOND_CHECK);

                        monitorThings(IThing.Source.SmartThings);
                        monitorThings(IThing.Source.PhilipsHue);
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

    private class SourceInfo {
        private TokenInfo mToken = null;
        private IController mController = null;
        private Activity mActivity = null;

        public SourceInfo(IThing.Source source, Activity activity) throws Exception {
            mActivity = activity;
            getTokenAndController(source);
        }

        public SourceInfo(IThing.Source source) throws Exception {
            getTokenAndController(source);
        }

        private void getTokenAndController(IThing.Source type) throws Exception {
            if (type == IThing.Source.SmartThings) {
                mToken = TokenInfo.Load(TokenSmartThings.class);
                mController = new STController(mActivity);
            } else if (type == IThing.Source.PhilipsHue) {
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
