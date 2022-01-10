package shane.pennihome.local.smartboard.services.Harmony;

import android.text.TextUtils;

import java.net.URL;
import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutorRequest;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.services.SmartThings.SmartThingsServicePAT;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;

public class HarmonyHubService extends IService {
    private String mIp;
    private int mPort = 8088;
    private String mRemoteId;

    public static HarmonyHubService Load(String json) {
        try {
            return IService.fromJson(HarmonyHubService.class, json);
        } catch (Exception e) {
            return new HarmonyHubService();
        }
    }

    @Override
    public Types getDatabaseType() {
        return Types.Service;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        this.mIp = ip;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        this.mPort = port;
    }

    public String getRemoteId() {
        return mRemoteId;
    }

    public void setRemoteId(String remoteId) {
        this.mRemoteId = remoteId;
    }

    @Override
    public IRegisterServiceFragment getRegisterDialog() {
        return new HarmonyHubFragment();
    }

    @Override
    public int getDrawableIconResource() {
        return R.mipmap.harm_logo_mm_large_foreground;
    }

    @Override
    protected boolean isRegistered() {
        return mRemoteId != null && !TextUtils.isEmpty(mRemoteId);
    }

    @Override
    public boolean isAwaitingAction() {
        return false;
    }

    @Override
    public void connect()  {
    }

    @Override
    public ArrayList<IThingsGetter> getThingGetters() {
        return new ArrayList<>();
    }

    @Override
    public ServicesTypes getServiceType() {
        return ServicesTypes.HarmonyHub;
    }

    @Override
    public String getName() {
        return "Harmony Hub";
    }
}
