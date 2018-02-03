package shane.pennihome.local.smartboard.services.PhilipsHue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.PhilipsHue.HueBridgeFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link HueBridge} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HueBridgeViewAdapter extends RecyclerView.Adapter<HueBridgeViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private List<HueBridge> mValues = new ArrayList<>();

    HueBridgeViewAdapter(List<HueBridge> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        if (mValues == null)
            mValues = new ArrayList<>();
        mListener = listener;
    }

    public List<HueBridge> getItems()
    {
        return mValues;
    }

    void setItems(List<HueBridge> items)
    {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_huebridge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mIdView.setText(String.format("[%s]",holder.mItem.getId()));
        holder.mIpView.setText(String.format("[%s]",holder.mItem.getIp()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener)
                    mListener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIpView;
        final TextView mIdView;
        HueBridge mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.hue_id);
            mIpView = view.findViewById(R.id.hue_ip);
        }
    }
}
