package shane.pennihome.local.smartboard.services.Harmony;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IAdditional;

public class HarmonyFunction extends IAdditional {
    String mAction;
    String mName;
    String mLabel;

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public HarmonyFunction() {
    }

    public HarmonyFunction(String action, String name, String label) {
        this.mAction = action;
        this.mName = name;
        this.mLabel = label;
    }
}
