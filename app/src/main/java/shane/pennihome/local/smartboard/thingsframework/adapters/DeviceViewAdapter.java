package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;

/**
 * Created by shane on 29/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class DeviceViewAdapter extends ThingViewAdapter {
    BroadcastReceiver mBroadcastReceiver;
    public DeviceViewAdapter(final IThings items) {
        super(items);
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
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).stopListening();
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        Switch d = (Switch) mValues.get(position);

        vh.mItem = d;
        vh.mNameView.setText(d.getName());
        vh.mSwitchView.setChecked(d.isOn());
        vh.mSwitchView.setEnabled(!d.isUnreachable());
        vh.mTypeView.setText(d.getType());
        vh.startListening();

        if (vh.mItem.getServiceType() == IService.ServicesTypes.SmartThings) {
            vh.mImgView.setImageResource(R.drawable.icon_switch);
            vh.mSourceView.setText(R.string.device_st_label);
        } else if (vh.mItem.getServiceType() == IService.ServicesTypes.PhilipsHue) {
            vh.mImgView.setImageResource(R.mipmap.icon_phlogo_mm_fg);
            vh.mSourceView.setText(R.string.device_ph_label);
        }

        vh.mSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mSwitchView.setEnabled(false);
                JsonExecutorResult result = vh.mItem.execute();
                if (!result.isSuccess())
                    Toast.makeText(vh.mSwitchView.getContext(), "Error:" + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        vh.setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(IThing thing) {
                vh.mSwitchView.setChecked(!vh.mItem.isUnreachable());
            }

            @Override
            public void OnStateChanged(IThing thing) {
                vh.mSwitchView.setChecked(vh.mItem.isOn());
                vh.mSwitchView.setEnabled(true);
            }

            @Override
            public void OnDimmerLevelChanged(IThing thing) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImgView;
        final android.widget.Switch mSwitchView;
        final TextView mNameView;
        final TextView mTypeView;
        final TextView mSourceView;
        BroadcastReceiver mBroadcastReceiver;
        Switch mItem;
        OnThingActionListener mOnThingActionListener;

        ViewHolder(View view) {
            super(view);

            mImgView = view.findViewById(R.id.device_img);
            mNameView = view.findViewById(R.id.device_name);
            mTypeView = view.findViewById(R.id.device_type);
            mSwitchView = view.findViewById(R.id.device_switch);
            mSourceView = view.findViewById(R.id.device_source);
        }

        public OnThingActionListener getOnThingActionListener() {
            return mOnThingActionListener;
        }

        void setOnThingActionListener(OnThingActionListener onThingActionListener) {
            mOnThingActionListener = onThingActionListener;
        }

        void startListening() {
            if (mBroadcastReceiver == null && itemView != null) {
                mBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (mItem != null && mOnThingActionListener != null) {
                            IMessage<?> message = IMessage.fromIntent(intent);
                            if (message instanceof ThingChangedMessage) {
                                ThingChangedMessage thingChangedMessage = (ThingChangedMessage) message;
                                if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.State)
                                    mOnThingActionListener.OnStateChanged(mItem);
                                else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Unreachable)
                                    mOnThingActionListener.OnReachableStateChanged(mItem);
                                else if (thingChangedMessage.getWhatChanged() == ThingChangedMessage.What.Level)
                                    mOnThingActionListener.OnDimmerLevelChanged(mItem);
                            }
                        }
                    }
                };

                LocalBroadcastManager.getInstance(itemView.getContext()).registerReceiver(mBroadcastReceiver,
                        new IntentFilter(new ThingChangedMessage().getMessageType()));

            }
        }

        void stopListening() {
            if (mBroadcastReceiver != null && itemView != null) {
                LocalBroadcastManager.getInstance(itemView.getContext()).unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver = null;
            }
        }

        @Override
        protected void finalize() throws Throwable {
            stopListening();
            super.finalize();
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
