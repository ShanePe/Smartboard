package shane.pennihome.local.smartboard.fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.Callbacks.EditThingTouchHelperCallback;
import shane.pennihome.local.smartboard.thingsframework.adapters.DeviceViewAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DeviceFragment extends ThingFragment {

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_device_list;
    }

    @Override
    protected RecyclerView.Adapter getAdapter(IThings things) {
        return new DeviceViewAdapter(things);
    }

    @Override
    IThings getDataSource() {
        return getMainActivity().getMonitor().getDevices();
    }
}
