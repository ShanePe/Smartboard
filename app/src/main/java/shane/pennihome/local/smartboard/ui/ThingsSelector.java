package shane.pennihome.local.smartboard.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

public class ThingsSelector extends LinearLayoutCompat {
    //private IService mService;
    private Spinner mSpService;
    private Spinner mSpThing;
    private SpinnerServiceAdapter mServiceAptr;
    private SpinnerThingAdapter mThingsAptr;
    private LayoutInflater mInflater;

    private OnThingsSelectedListener mOnThingsSelectedListener;

    public ThingsSelector(Context context) {
        super(context);
        initializeViews(context);
    }

    public ThingsSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ThingsSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);
    }

    public Services getServices() {
        return mServiceAptr == null ? null : mServiceAptr.getServices();
    }

    public Things getThings() {
        return mThingsAptr == null ? null : mThingsAptr.getThings();
    }


    public void setData(Services services, Things things) {
        if (things != null) {
            mThingsAptr = new SpinnerThingAdapter(mInflater, things);
            mSpThing.setAdapter(mThingsAptr);
            mSpThing.setVisibility(View.VISIBLE);
        } else
            mSpThing.setVisibility(View.GONE);

        if (services != null) {
            mServiceAptr = new SpinnerServiceAdapter(mInflater, services);
            mSpService.setAdapter(mServiceAptr);
            mSpService.setVisibility(services.size() > 1 ? View.VISIBLE : View.GONE);
        } else
            mSpService.setVisibility(View.GONE);


    }

    public IThing getThing() {
        return (IThing) mSpThing.getSelectedItem();
    }

    public void setThing(IThing thing) {
        if (thing != null) {
            if (mSpService != null && getServices() != null) {
                int serviceAt = getServices().getIndex(getServices().getByType(thing.getServiceType()));
                if (mSpService.getSelectedItemPosition() != serviceAt)
                    mSpService.setSelection(serviceAt);
            }
            if (mSpThing != null && getThings() != null) {
                int thingsAt = getThings().getIndex(thing);
                if (mSpThing.getSelectedItemPosition() != thingsAt)
                    mSpThing.setSelection(thingsAt);
            }
        } else {
            mSpService.setSelection(-1);
            mSpThing.setSelection(-1);
        }
    }

    public OnThingsSelectedListener getOnThingsSelectedListener() {
        return mOnThingsSelectedListener;
    }

    public void setOnThingsSelectedListener(OnThingsSelectedListener onThingsSelectedListener) {
        this.mOnThingsSelectedListener = onThingsSelectedListener;
    }

    private void initializeViews(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert mInflater != null;
        mInflater.inflate(R.layout.custom_things_selector, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mSpService = this.findViewById(R.id.prop_sp_service);
        mSpThing = this.findViewById(R.id.prop_sp_thing);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startListening();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void startListening() {
        if (mSpService.getOnItemClickListener() == null)
            mSpService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    IService service = (IService) parent.getItemAtPosition(position);
                    if (service instanceof SpinnerServiceAdapter.BlankService)
                        service = null;
                    mThingsAptr.filter(service);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mThingsAptr.clearFilter();
                }
            });

        if (mSpThing.getOnItemClickListener() == null)
            mSpThing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mOnThingsSelectedListener != null)
                        mOnThingsSelectedListener.OnSelected((IThing) adapterView.getItemAtPosition(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    if (mOnThingsSelectedListener != null)
                        mOnThingsSelectedListener.OnSelected(null);
                }
            });
    }

    public interface OnThingsSelectedListener {
        void OnSelected(IThing thing);
    }

    /**
     * Created by shane on 14/01/18.
     */

    public static class SpinnerThingAdapter extends BaseAdapter implements SpinnerAdapter {
        private static LayoutInflater mInflater = null;
        private Things mThings;
        private Things mFilteredThings;

        public SpinnerThingAdapter(LayoutInflater inflater, Things things) {
            mInflater = inflater;
            mThings = things;
            mFilteredThings = mThings;
        }

        @Override
        public int getCount() {
            return mFilteredThings.size();
        }

        @Override
        public Object getItem(int position) {
            return mFilteredThings.get(position < getCount() ? position : 0);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.spinner_thing, null);

            if (position < getCount()) {
                IThing thing = (IThing) getItem(position);

                TextView txtName = (TextView) convertView.findViewById(R.id.txt_spin_name);
                TextView txtSrc = (TextView) convertView.findViewById(R.id.txt_spin_source);

                txtName.setText(thing.getName());
                txtSrc.setText(thing instanceof Switch ? ((Switch) thing).getType() : thing.getThingType().toString());
            }
            return convertView;
        }

        public Things getThings() {
            return mFilteredThings;
        }

        public void clearFilter() {
            filter(null);
        }

        public void filter(IService service) {
            mFilteredThings = service == null ? mThings : mThings.getForService(service);
            notifyDataSetChanged();
        }
    }

    public static class SpinnerServiceAdapter extends BaseAdapter implements SpinnerAdapter {
        private static LayoutInflater mInflater = null;
        private final BlankService mBlankService;
        private Services mServices;

        public SpinnerServiceAdapter(LayoutInflater inflater, Services services) {
            mInflater = inflater;
            mBlankService = new BlankService();
            setServices(services);
        }

        @Override
        public int getCount() {
            return mServices.size();
        }

        @Override
        public Object getItem(int position) {
            return mServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public Services getServices() {
            return mServices;
        }

        private void setServices(Services services) {
            Services s = new Services();
            if (services.size() > 1)
                if (!s.hasService(mBlankService))
                    s.add(mBlankService);

            s.addAll(services);
            this.mServices = s;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.spinner_service, null);
            }

            IService service = (IService) getItem(position);

            ImageView img = (ImageView) convertView.findViewById(R.id.img_spin_icon_srv);
            TextView txtName = (TextView) convertView.findViewById(R.id.txt_spin_name_srv);

            if (service.getServiceType() == IService.ServicesTypes.SmartThings)
                img.setImageResource(R.mipmap.icon_switch_mm_fg);
            else if (service.getServiceType() == IService.ServicesTypes.PhilipsHue)
                img.setImageResource(R.mipmap.icon_phlogo_mm_fg);
            else if (service.getServiceType() == IService.ServicesTypes.HarmonyHub)
                img.setImageResource(R.mipmap.icon_hub_mm_fg);
            else
                img.setImageResource(R.mipmap.icon_dashboard_mm_fg);

            txtName.setText(service.getName());

            return convertView;
        }

        class BlankService extends IService {

            @Override
            public String getName() {
                return "All";
            }

            @Override
            public Types getDatabaseType() {
                return null;
            }

            @Override
            public IRegisterServiceFragment getRegisterDialog() {
                return null;
            }

            @Override
            public int getDrawableIconResource() {
                return 0;
            }

            @Override
            protected boolean isRegistered() {
                return false;
            }

            @Override
            public boolean isAwaitingAction() {
                return false;
            }

            @Override
            public void connect() {

            }

            @Override
            public ArrayList<IThingsGetter> getThingGetters() {
                return null;
            }

            @Override
            public ServicesTypes getServiceType() {
                return null;
            }
        }
    }
}
