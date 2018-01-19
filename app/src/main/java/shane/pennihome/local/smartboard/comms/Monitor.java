package shane.pennihome.local.smartboard.comms;

import android.app.Activity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.comms.interfaces.IController;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.comms.philipshue.PHBridgeController;
import shane.pennihome.local.smartboard.comms.smartthings.STController;
import shane.pennihome.local.smartboard.data.TokenHueBridge;
import shane.pennihome.local.smartboard.data.TokenSmartThings;
import shane.pennihome.local.smartboard.data.interfaces.ITokenInfo;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Monitor {
    private static final int SECOND_CHECK = 120;
    private final Activity mActivity;
    private Thread mMonitorThread = null;
    private static Things mThings;

    public static Things getThings() {
        return Monitor.mThings;
    }

    public static void setThings(Things mThings) {
        Monitor.mThings = mThings;
    }

    public Monitor(Activity activity) {
        mActivity = activity;

        getThings(IThing.Sources.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                getThings(IThing.Sources.PhilipsHue, new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                    }
                });
            }
        });
    }

    public <T extends IThing> IThings<T> getThings(Class<T> cls)
    {
        IThings<T> items = mThings.getOfType(cls);
        items.sort();
        return items;
    }

    public void getSmartThingsThings(final OnProcessCompleteListener<STController> processComplete) {
        getThings(IThing.Sources.SmartThings, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (STController) source);
            }
        });
    }

    public void getHueBridgeThings(final OnProcessCompleteListener<PHBridgeController> processComplete) {
        getThings(IThing.Sources.PhilipsHue, new OnProcessCompleteListener<IController>() {
            @Override
            public void complete(boolean success, IController source) {
                if (processComplete != null)
                    processComplete.complete(success, (PHBridgeController) source);
            }
        });
    }

    private void getThings(IThing.Sources type, final OnProcessCompleteListener<IController> processComplete) {
        try {
            if(mThings == null)
                mThings = new Things();
            else
                mThings.remove(type);

            SourceInfo s = new SourceInfo(type, mActivity);

            if (s.getToken().isAwaitingAuthorisation())
                s.getController().getAll(new OnProcessCompleteListener<IController>() {
                    @Override
                    public void complete(boolean success, IController source) {
                        if (success) {
                            mThings.addAll(source.getThings());
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
                            mThings.addAll(source.getThings());
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

    private void monitorThings(final IThing.Sources type) {
        try {
            SourceInfo s = new SourceInfo(type);
            if (s.getToken().isAuthorised()) {
                s.getController().getDevices(new OnProcessCompleteListener<IThings<Switch>>() {
                    @Override
                    public void complete(boolean success, IThings<Switch> source) {
                        if (success)
                            checkStateChange(source, type);
                    }
                });
            }
        } catch (Exception ex) {
        }//Don't crash on monitor thread.
    }

    private void checkStateChange(IThings<Switch> src, IThing.Sources type) {
        for (Switch d : getThings(Switch.class)) {
            if (d.getSource() == type) {
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

    public void Start() {
        Stop();

        mMonitorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000 * Monitor.SECOND_CHECK);

                        monitorThings(IThing.Sources.SmartThings);
                        monitorThings(IThing.Sources.PhilipsHue);
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
        private ITokenInfo mToken = null;
        private IController mController = null;
        private Activity mActivity = null;

        public SourceInfo(IThing.Sources sources, Activity activity) throws Exception {
            mActivity = activity;
            getTokenAndController(sources);
        }

        public SourceInfo(IThing.Sources sources) throws Exception {
            getTokenAndController(sources);
        }

        private void getTokenAndController(IThing.Sources type) throws Exception {
            if (type == IThing.Sources.SmartThings) {
                mToken = ITokenInfo.Load(TokenSmartThings.class);
                mController = new STController(mActivity);
            } else if (type == IThing.Sources.PhilipsHue) {
                mToken = ITokenInfo.Load(TokenHueBridge.class);
                mController = new PHBridgeController(mActivity);
            } else
                throw new Exception("Invalid Sources Type");
        }

        public ITokenInfo getToken() {
            return mToken;
        }

        public IController getController() {
            return mController;
        }
    }
}
