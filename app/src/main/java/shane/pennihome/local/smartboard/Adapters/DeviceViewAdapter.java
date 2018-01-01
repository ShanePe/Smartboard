package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Interface.onThingListener;
import shane.pennihome.local.smartboard.Fragments.ThingFragment;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
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
        final ViewHolder vh = (ViewHolder) holder;
        Device d = (Device) mValues.get(position);

        vh.mItem = d;
        vh.mNameView.setText(d.getName());
        vh.mSwitchView.setChecked(d.isOn());
        vh.mSwitchView.setEnabled(d.getState() != Device.States.Unreachable);
        vh.mTypeView.setText(d.getType());

        if (vh.mItem.getSource() == Thing.Source.SmartThings) {
            vh.mImgView.setImageResource(R.drawable.icon_switch);
            vh.mSourceView.setText(R.string.device_st_label);
        } else if (vh.mItem.getSource() == Thing.Source.PhilipsHue) {
            vh.mImgView.setImageResource(R.drawable.icon_phlogo);
            vh.mSourceView.setText(R.string.device_ph_label);
        }
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
                vh.mItem.Toggle();
            }
        });

        vh.mItem.setOnThingListener(new onThingListener() {
            @Override
            public void StateChanged() {
                vh.mSwitchView.setChecked(vh.mItem.isOn());
                vh.mSwitchView.setEnabled(vh.mItem.getState() != Device.States.Unreachable);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mImgView;
        final Switch mSwitchView;
        final TextView mNameView;
        final TextView mTypeView;
        final TextView mSourceView;
        Device mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;

            mImgView = view.findViewById(R.id.device_img);
            mNameView = view.findViewById(R.id.device_name);
            mTypeView = view.findViewById(R.id.device_type);
            mSwitchView = view.findViewById(R.id.device_switch);
            mSourceView = view.findViewById(R.id.device_source);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
