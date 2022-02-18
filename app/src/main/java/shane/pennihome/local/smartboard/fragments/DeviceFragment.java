package shane.pennihome.local.smartboard.fragments;

import android.support.v7.widget.RecyclerView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.adapters.DeviceViewAdapter;

/**
 * Created by shane on 30/12/17.
 */

public class DeviceFragment extends ThingFragment {


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_device_list;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected RecyclerView.Adapter getAdapter(Things things) {
        return new DeviceViewAdapter(things);
    }

    @Override
    Things getDataSource() {
        return Monitor.getMonitor().getThings(Switch.class).toThings();
    }
}
