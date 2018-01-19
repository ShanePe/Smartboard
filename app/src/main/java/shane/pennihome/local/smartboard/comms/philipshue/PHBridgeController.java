package shane.pennihome.local.smartboard.comms.philipshue;

import android.app.Activity;

import shane.pennihome.local.smartboard.comms.interfaces.IController;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings({"ALL", "ConstantConditions"})
public class PHBridgeController extends IController<PHBridgeController> {
    public PHBridgeController(Activity activity) {
        super(activity);
    }

    @Override
    public void Connect(final OnProcessCompleteListener<PHBridgeController> processCompleteListener) {
        final PHBridgeController me = this;
        PHBridgeConnector con = new PHBridgeConnector(mActivity, new OnProcessCompleteListener<PHBridgeConnector>() {
            @Override
            public void complete(boolean success, PHBridgeConnector source) {
                if (success)
                    processCompleteListener.complete(success, me);
            }
        });
        con.execute();
    }

    @Override
    public void getDevices(final OnProcessCompleteListener<IThings<Switch>> processCompleteListener) {
        @SuppressWarnings("unused") final PHBridgeController me = this;
        PHBridgeDeviceGetter dGet = new PHBridgeDeviceGetter(mActivity, new OnProcessCompleteListener<PHBridgeDeviceGetter>() {
            @Override
            public void complete(boolean success, PHBridgeDeviceGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getSwitches());
            }
        });
        dGet.execute();
    }

    @Override
    public void getRoutines(final OnProcessCompleteListener<IThings<Routine>> processCompleteListener) {
        @SuppressWarnings("unused") final PHBridgeController me = this;
        PHBridgeRoutineGetter rGet = new PHBridgeRoutineGetter(mActivity, new OnProcessCompleteListener<PHBridgeRoutineGetter>() {
            @Override
            public void complete(boolean success, PHBridgeRoutineGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getRoutines());
            }
        });
        rGet.execute();
    }
}
