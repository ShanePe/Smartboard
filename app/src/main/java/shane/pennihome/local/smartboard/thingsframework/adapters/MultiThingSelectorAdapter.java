package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class MultiThingSelectorAdapter extends RecyclerView.Adapter<MultiThingSelectorAdapter.ViewHolder> {
    Things mThings;
    Things mSelectedThings;

    public MultiThingSelectorAdapter(Things things) {
        this.mThings = things;
        notifyDataSetChanged();
    }

    public Things getThings() {
        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
    }

    public Things getSelectedThings() {
        return mSelectedThings;
    }

    public void setSelectedThings(Things selectedThings) {
        mSelectedThings = selectedThings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_multi_thing_selection, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mThings.get(position);
        if (holder.mItem.getServiceType() == IService.ServicesTypes.SmartThings) {
            holder.mImg.setImageResource(R.mipmap.icon_switch_mm_fg);
        } else if (holder.mItem.getServiceType() == IService.ServicesTypes.PhilipsHue) {
            holder.mImg.setImageResource(R.mipmap.icon_phlogo_mm_fg);
        }
        holder.mName.setText(holder.mItem.getName());

        if (mSelectedThings != null)
            holder.itemView.setSelected((mSelectedThings.getbyId(holder.mItem.getId()) != null));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedThings == null)
                    mSelectedThings = new Things();

                holder.itemView.setSelected(!holder.itemView.isSelected());
                if (holder.itemView.isSelected())
                    mSelectedThings.add(holder.mItem);
                else
                    mSelectedThings.remove(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mName;
        IThing mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mImg = itemView.findViewById(R.id.cmts_icon);
            mName = itemView.findViewById(R.id.cmts_name);
        }
    }
}
