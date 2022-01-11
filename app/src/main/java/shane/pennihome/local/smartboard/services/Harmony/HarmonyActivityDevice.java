package shane.pennihome.local.smartboard.services.Harmony;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IAdditional;

public class HarmonyActivityDevice extends IAdditional {
    String mId;
    boolean mOn;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public boolean isOn() {
        return mOn;
    }

    public void setOn(boolean on) {
        this.mOn = on;
    }

    public HarmonyActivityDevice() {
    }

    public HarmonyActivityDevice(String mId, boolean mOn) {
        this.mId = mId;
        this.mOn = mOn;
    }

    @Override
    public String getKey() {
        return mId;
    }
}
