package shane.pennihome.local.smartboard.Comms.PhilipsHue;

import android.app.Activity;

import shane.pennihome.local.smartboard.Comms.Interface.IController;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Routines;

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
    public void getDevices(final OnProcessCompleteListener<Devices> processCompleteListener) {
        @SuppressWarnings("unused") final PHBridgeController me = this;
        PHBridgeDeviceGetter dGet = new PHBridgeDeviceGetter(mActivity, new OnProcessCompleteListener<PHBridgeDeviceGetter>() {
            @Override
            public void complete(boolean success, PHBridgeDeviceGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getDevices());
            }
        });
        dGet.execute();
    }

    @Override
    public void getRoutines(final OnProcessCompleteListener<Routines> processCompleteListener) {
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
