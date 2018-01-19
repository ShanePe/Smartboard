package shane.pennihome.local.smartboard.comms.smartthings;

import android.app.Activity;

import shane.pennihome.local.smartboard.comms.interfaces.IController;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

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
    public void getDevices(final OnProcessCompleteListener<IThings<Switch>> processCompleteListener) {
        STSwitchGetter dGet = new STSwitchGetter(mActivity, new OnProcessCompleteListener<STSwitchGetter>() {
            @Override
            public void complete(boolean success, STSwitchGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getSwitches());
                else
                    processCompleteListener.complete(success, null);
            }
        });
        dGet.execute();
    }

    @Override
    public void getRoutines(final OnProcessCompleteListener<IThings<Routine>> processCompleteListener) {
        STRoutineGetter rGet = new STRoutineGetter(mActivity, new OnProcessCompleteListener<STRoutineGetter>() {
            @Override
            public void complete(boolean success, STRoutineGetter source) {
                if (success)
                    processCompleteListener.complete(success, source.getRoutines());
                else
                    processCompleteListener.complete(success, null);
            }
        });
        rGet.execute();
    }

}
