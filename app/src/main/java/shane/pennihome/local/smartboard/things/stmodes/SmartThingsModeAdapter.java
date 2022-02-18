package shane.pennihome.local.smartboard.things.stmodes;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 11/02/18.
 */

public class SmartThingsModeAdapter extends RecyclerView.Adapter<SmartThingsModeAdapter.ViewHolder> {
    private final SmartThingMode mSmartThingMode;
    private final OnModeSelectedListener mOnModeSelectedListener;

    SmartThingsModeAdapter(OnModeSelectedListener onModeSelectedListener) throws Exception {
        IThings<SmartThingMode> things = Monitor.getMonitor().getThings(SmartThingMode.class);
        if (things.size() == 0)
            throw new Exception("Could not find SmartThings Modes");

        mSmartThingMode = things.get(0);
        mOnModeSelectedListener = onModeSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_smartthings_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mText.setText(mSmartThingMode.getModes().get(position));
        holder.itemView.setSelected(mSmartThingMode.getSelectedIndex() == position);
        if (holder.itemView.isSelected())
            holder.itemView.setBackgroundResource(R.drawable.btn_round_accent);
        else
            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnModeSelectedListener != null)
                        mOnModeSelectedListener.OnModeSelected(holder.getAdapterPosition(), mSmartThingMode.getModes().get(holder.getAdapterPosition()));
                }
            });
    }

    @Override
    public int getItemCount() {
        return mSmartThingMode.getModes().size();
    }

    public interface OnModeSelectedListener {
        void OnModeSelected(int index, String name);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayoutCompat mContainer;
        private final TextView mText;

        public ViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.stm_select_container);
            mText = itemView.findViewById(R.id.stm_select_name);
        }
    }
}
