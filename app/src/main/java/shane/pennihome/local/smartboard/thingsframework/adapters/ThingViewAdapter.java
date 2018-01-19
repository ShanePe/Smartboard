package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

public abstract class ThingViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final IThings mValues;

    protected ThingViewAdapter(IThings items) {
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

    protected IThings getThings() {
        return mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
