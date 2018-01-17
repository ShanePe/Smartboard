package shane.pennihome.local.smartboard.fragments;

import android.support.v7.widget.RecyclerView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.things.Adapters.DeviceViewAdapter;
import shane.pennihome.local.smartboard.things.Interface.IThingCollection;

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
    protected RecyclerView.Adapter getAdapter(IThingCollection things) {
        return new DeviceViewAdapter(things);
    }

    @Override
    IThingCollection getDataSource() {
        return getMainActivity().getMonitor().getDevices();
    }
}
