package shane.pennihome.local.smartboard.services.interfaces;

import android.support.v4.app.DialogFragment;

import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;

/**
 * Created by SPennicott on 02/02/2018.
 */

public class IRegisterServiceFragment extends DialogFragment {
    private OnProcessCompleteListener<IService> mOnProcessCompleteListener;
    private IService mService;

    public OnProcessCompleteListener getOnProcessCompleteListener() {
        return mOnProcessCompleteListener;
    }

    public void setOnProcessCompleteListener(OnProcessCompleteListener<IService> onProcessCompleteListener) {
        this.mOnProcessCompleteListener = onProcessCompleteListener;
    }

    public IService getService() {
        return mService;
    }

    public <T extends IService> T getService(Class<T> cls) {
        return (T)getService();
    }

    public void setService(IService service) {
        mService = service;
    }
}
