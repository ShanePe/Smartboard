package shane.pennihome.local.smartboard.Fragments;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Adapters.DeviceViewAdapter;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.R;

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
    public RecyclerView.Adapter getAdapter(List<IThing> things) {
        return new DeviceViewAdapter(things);
    }

    @Override
    <E extends IThing> ArrayList<E> getDataSource() {
        return (ArrayList<E>) getMainActivity().getMonitor().getDevices();
    }
}
