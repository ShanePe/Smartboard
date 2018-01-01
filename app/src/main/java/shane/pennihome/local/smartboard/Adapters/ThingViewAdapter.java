package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Thing} and makes a call to the
 * specified {@link ThingFragment.OnListFragmentInteractionListener}.
 * .
 */
abstract class ThingViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final List<Thing> mValues;
    final ThingFragment.OnListFragmentInteractionListener mListener;

    ThingViewAdapter(List<Thing> items, ThingFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    protected abstract int getFragmentLayout();

    protected abstract RecyclerView.ViewHolder getViewHolder(View view);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getFragmentLayout(), parent, false);
        return getViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
