package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class MultiThingSelector extends GridLayout {
    private Things mThings;
    private Things mSelectedThings;

    private MultiThingSelectorAdapter mAdapter;

    public MultiThingSelector(Context context) {
        super(context);
        initialiseView(context);
    }

    public MultiThingSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseView(context);
    }

    public MultiThingSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialiseView(context);
    }

    private Things getThings() {
        if (mThings == null)
            mThings = new Things();
        return mThings;
    }

    public void setThings(Things things) {
        mThings = things;
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

    }

    public Things getSelectedThings() {
        if (mSelectedThings == null)
            mSelectedThings = new Things();
        return mSelectedThings;
    }

    public void setSelectedThings(Things selectedThings) {
        mSelectedThings = selectedThings;
    }

    private void initialiseView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.custom_multi_thing_selection_list, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        RecyclerView rv = findViewById(R.id.cmts_recycleview);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mAdapter = new MultiThingSelectorAdapter();
        rv.setAdapter(mAdapter);
    }

    public class MultiThingSelectorAdapter extends RecyclerView.Adapter<MultiThingSelectorAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_multi_thing_selection, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = getThings().get(position);
            if (holder.mItem.getServiceType() == IService.ServicesTypes.SmartThings) {
                holder.mImg.setImageResource(R.mipmap.icon_switch_mm_fg);
            } else if (holder.mItem.getServiceType() == IService.ServicesTypes.PhilipsHue) {
                holder.mImg.setImageResource(R.mipmap.icon_phlogo_mm_fg);
            }
            holder.mName.setText(holder.mItem.getName());

            if (getSelectedThings() != null)
                holder.itemView.setSelected((getSelectedThings().getbyId(holder.mItem.getId()) != null));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.setSelected(!holder.itemView.isSelected());
                    if (holder.itemView.isSelected())
                        getSelectedThings().add(getThings().getByKey(holder.mItem.getKey()));
                    else
                        getSelectedThings().remove(getThings().getByKey(holder.mItem.getKey()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mThings.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mImg;
            final TextView mName;
            IThing mItem;

            public ViewHolder(View itemView) {
                super(itemView);

                mImg = itemView.findViewById(R.id.cmts_icon);
                mName = itemView.findViewById(R.id.cmts_name);
            }
        }
    }

}

