package shane.pennihome.local.smartboard.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 19/02/18.
 */

public class MultiThingSelector extends GridLayout {
    private LayoutInflater mInflater;
   // private IService mService;

    //private Things mThings;
    private Things mSelectedThings;
    private RecyclerView rview;

    private Spinner mSPService;
    private MultiThingSelectorAdapter mAdapter;
    private ThingsSelector.SpinnerServiceAdapter mServiceAdapter = null;

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

    public Services getServices() {
        return mServiceAdapter.getServices();
    }

    public void setData(Services services,Things things){
        mAdapter = new MultiThingSelectorAdapter(things);
        rview.setAdapter(mAdapter);

        mServiceAdapter = new ThingsSelector.SpinnerServiceAdapter(mInflater,services);
        mSPService.setAdapter(mServiceAdapter);
        mSPService.setVisibility(services.size() > 1 ? View.VISIBLE : View.GONE);
    }

    public Things getSelectedThings() {
        if (mSelectedThings == null)
            mSelectedThings = new Things();
        return mSelectedThings;
    }

    public void setSelectedThings(Things selectedThings) {
        mSelectedThings = selectedThings;
        if (selectedThings.size() > 0) {
            ArrayList<IService.ServicesTypes> check = new ArrayList<>();
            for (IThing t : selectedThings)
                if (!check.contains(t.getServiceType()))
                    check.add(t.getServiceType());

            int selected = getServices().getIndex(getServices().getByType(check.size() > 1 ? null : check.get(0)));
            mSPService.setSelection(selected);
        }
    }

    private void initialiseView(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert mInflater != null;
        mInflater.inflate(R.layout.custom_multi_thing_selection_list, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rview = findViewById(R.id.cmts_recycleview);
        rview.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mSPService = findViewById(R.id.prop_sp_service_mlti);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSPService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        IService service = (IService) parent.getItemAtPosition(position);
                        if (service instanceof ThingsSelector.SpinnerServiceAdapter.BlankService)
                            service = null;
                        mAdapter.filter(service);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        mAdapter.clearFilter();
                    }
                });

                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public class MultiThingSelectorAdapter extends RecyclerView.Adapter<MultiThingSelectorAdapter.ViewHolder> {
        Things mThings;
        Things mFilteredThings;

        public Things getThings() {
            return mFilteredThings;
        }

        public MultiThingSelectorAdapter(Things things) {
            this.mThings = things;
            this.mFilteredThings = things;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_multi_thing_selection, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mFilteredThings.get(position);
            if (holder.mItem.getServiceType() == IService.ServicesTypes.SmartThings) {
                holder.mImg.setImageResource(R.mipmap.icon_switch_mm_fg);
            } else if (holder.mItem.getServiceType() == IService.ServicesTypes.PhilipsHue) {
                holder.mImg.setImageResource(R.mipmap.icon_phlogo_mm_fg);
            }
            else if (holder.mItem.getServiceType() == IService.ServicesTypes.HarmonyHub) {
                holder.mImg.setImageResource(R.mipmap.icon_hub_mm_fg);
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
            return mFilteredThings.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filter(IService service){
            mFilteredThings = service == null?mThings:mThings.getForService(service);
            notifyDataSetChanged();
        }

        public void clearFilter(){
            filter(null);
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

