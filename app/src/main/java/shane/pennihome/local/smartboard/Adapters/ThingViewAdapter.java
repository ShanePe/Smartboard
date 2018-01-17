package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IThing;

abstract class ThingViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final List<IThing> mValues;

    ThingViewAdapter(List<IThing> items) {
        mValues = items;
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
