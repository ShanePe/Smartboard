package shane.pennihome.local.smartboard.Comms.SmartThings;

import android.app.Activity;

import shane.pennihome.local.smartboard.Comms.Interface.IController;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Devices;
import shane.pennihome.local.smartboard.Data.Routines;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class STController extends IController<STController> {

    public STController(Activity activity) {
        super(activity);
    }

    @Override
    public void Connect(final OnProcessCompleteListener<STController> processCompleteListener) {
        final STController me = this;
        STEndPointGetter epGet = new STEndPointGetter(mActivity, new OnProcessCompleteListener<STEndPointGetter>() {
            @Override
            public void complete(boolean success, STEndPointGetter source) {
                processCompleteListener.complete(success, me);
            }
        });
        epGet.execute();
    }

    @Override
    public void getDevices(final OnProcessCompleteListener<Devices> processCompleteListener) {
        STDevicesGetter dGet = new STDevicesGetter(mActivity, new OnProcessCompleteListener<STDevicesGetter>() {
            @Override
            public void complete(boolean success, STDevicesGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getDevices());
            }
        });
        dGet.execute();
    }

    @Override
    public void getRoutines(final OnProcessCompleteListener<Routines> processCompleteListener) {
        STRoutineGetter rGet = new STRoutineGetter(mActivity, new OnProcessCompleteListener<STRoutineGetter>() {
            @Override
            public void complete(boolean success, STRoutineGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getRoutines());
            }
        });
        rGet.execute();
    }

}
