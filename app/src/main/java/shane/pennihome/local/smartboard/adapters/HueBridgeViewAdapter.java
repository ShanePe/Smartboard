package shane.pennihome.local.smartboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.HueBridge;
import shane.pennihome.local.smartboard.fragments.HueBridgeFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link shane.pennihome.local.smartboard.data.HueBridge} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HueBridgeViewAdapter extends RecyclerView.Adapter<HueBridgeViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private List<HueBridge> mValues = new ArrayList<>();

    public HueBridgeViewAdapter(List<HueBridge> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        if (mValues == null)
            mValues = new ArrayList<>();
        mListener = listener;
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

        holder.mIdView.setText(String.format(holder.mView.getContext().getString(R.string.plhldr_bridge_id),
                mValues.get(position).getIp(),
                mValues.get(position).getId()));

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
        final TextView mIdView;
        HueBridge mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.hue_ip);
        }
    }
}
