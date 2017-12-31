package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Fragments.HueBridgeFragment.OnListFragmentInteractionListener;
import shane.pennihome.local.smartboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link shane.pennihome.local.smartboard.Data.HueBridge} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HueBridgeViewAdapter extends RecyclerView.Adapter<HueBridgeViewAdapter.ViewHolder> {

    private List<HueBridge> mValues = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;

    public HueBridgeViewAdapter(List<HueBridge> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        if(mValues == null)
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
        holder.mIdView.setText(mValues.get(position).getIp() + " [Id : " + mValues.get(position).getId() + "]");

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public HueBridge mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.hue_ip);
        }
}
}
