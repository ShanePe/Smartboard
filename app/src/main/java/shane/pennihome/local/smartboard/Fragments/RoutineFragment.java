package shane.pennihome.local.smartboard.Fragments;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import shane.pennihome.local.smartboard.Adapters.RoutineViewAdapter;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 30/12/17.
 */

public class RoutineFragment extends ThingFragment {
    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_routine_list;
    }

    @Override
    public RecyclerView.Adapter getAdapter(List<Thing> things, OnListFragmentInteractionListener onFragInt) {
        return new RoutineViewAdapter(things, onFragInt);
    }
}
