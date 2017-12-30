package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 29/12/17.
 */

public class DeviceViewAdapter extends ThingViewAdapter {
    public DeviceViewAdapter(List<Thing> items, ThingFragment.OnListFragmentInteractionListener listener) {
        super(items, listener);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_device;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder)holder;
        Device d = (Device)mValues.get(position);
        vh.mItem = d;
        vh.mContentView.setText(d.getName());
        vh.mSwitch.setChecked(d.getOn());
        vh.mTypeView.setText(d.getType());
        vh.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(vh.mItem);
                }
            }
        });

        vh.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Switch sw = v.findViewById(R.id.switch1);
                vh.mItem.Toggle(new ProcessCompleteListener<Device>() {
                    @Override
                    public void Complete(boolean success, Device source) {
                        sw.setChecked(source.getOn());
                    }
                });
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Switch mSwitch;
        public final TextView mContentView;
        public final TextView mTypeView;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mTypeView = view.findViewById(R.id.type);
            mSwitch = view.findViewById(R.id.switch1);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
