package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Interface.onThingToggledListener;
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
        vh.mNameView.setText(d.getName());
        vh.mSwitchView.setChecked(d.getOn());
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

        vh.mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Switch sw = v.findViewById(R.id.device_switch);
                vh.mItem.setOnThingToggledListener(new onThingToggledListener() {
                    @Override
                    public void Toggled() {
                        sw.setChecked(vh.mItem.getOn());
                    }
                });
                vh.mItem.Toggle();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Switch mSwitchView;
        public final TextView mNameView;
        public final TextView mTypeView;
        public Device mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.device_name);
            mTypeView = view.findViewById(R.id.device_type);
            mSwitchView = view.findViewById(R.id.device_switch);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
