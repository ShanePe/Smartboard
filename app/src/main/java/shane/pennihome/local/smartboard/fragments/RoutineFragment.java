package shane.pennihome.local.smartboard.fragments;

import android.support.v7.widget.RecyclerView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.things.Interface.IThingCollection;
import shane.pennihome.local.smartboard.things.Routine.RoutineViewAdapter;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineFragment extends ThingFragment {
    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_routine_list;
    }

    @Override
    protected RecyclerView.Adapter getAdapter(IThingCollection things) {
        return new RoutineViewAdapter(things);
    }

    @Override
    IThingCollection getDataSource() {
        return getMainActivity().getMonitor().getRoutines();
    }
}