package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.listeners.OnThingSelectListener;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 17/01/18.
 */

public class ThingSelectionAdapter extends RecyclerView.Adapter<ThingSelectionAdapter.ViewHolder> {

    private Things mThings;
    private OnThingSelectListener mOnThingSelectListener;
    public ThingSelectionAdapter(OnThingSelectListener onThingSelectListener) {
        this.mThings = Things.getAvailableTypes();
        this.mOnThingSelectListener = onThingSelectListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thing_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IThing t = mThings.get(position);
        holder.mText.setText(t.getFriendlyName());
        holder.mImg.setImageResource(t.getDefaultIconResource());

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnThingSelectListener != null)
                    mOnThingSelectListener.ThingSelected(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mThings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayoutCompat mContainer;
        private TextView mText;
        private ImageView mImg;
        private View mView;
        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mContainer = itemView.findViewById(R.id.things_select_container);
            mText = itemView.findViewById(R.id.things_select_name);
            mImg = itemView.findViewById(R.id.things_select_img);
        }
    }
}
