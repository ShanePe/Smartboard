package shane.pennihome.local.smartboard.services.interfaces;

import android.support.v4.app.DialogFragment;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;

/**
 * Created by SPennicott on 02/02/2018.
 */

public class IRegisterServiceFragment extends DialogFragment {
    private OnProcessCompleteListener<IService> mOnProcessCompleteListener;
    private IService mService;

    protected OnProcessCompleteListener<IService> getOnProcessCompleteListener() {
        return mOnProcessCompleteListener;
    }

    public void setOnProcessCompleteListener(OnProcessCompleteListener<IService> onProcessCompleteListener) {
        this.mOnProcessCompleteListener = onProcessCompleteListener;
    }

    protected IService getService() {
        return mService;
    }

    protected <T extends IService> T getService(Class<T> c) {
        //noinspection unchecked
        return (T)mService;
    }

    public void setService(IService service) {
        mService = service;
    }
}
