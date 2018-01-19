package shane.pennihome.local.smartboard.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;

/**
 * Created by shane on 13/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Dashboard extends IDatabaseObject {
    private final List<Group> mGroups = new ArrayList<>();
    private long mOrderId;

    public static Dashboard Load(String json) {
        Dashboard ret = new Dashboard();
        try {
            ret = JsonBuilder.Get().fromJson(json, Dashboard.class);
        } catch (Exception e) {
            Log.e("Smartboard", "Error : " + e.getMessage());
        }

        return ret;
    }

    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Dashboard;
    }

    public Group getGroupAt(int index) {
        return mGroups.get(index);
    }

    public long getOrderId() {
        return mOrderId;
    }

    public void setOrderId(long orderId) {
        this.mOrderId = orderId;
    }
}
