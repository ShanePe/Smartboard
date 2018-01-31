package shane.pennihome.local.smartboard.thingsframework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by shane on 14/01/18.
 */

@SuppressWarnings("ALL")
public class SpinnerThingAdapter extends BaseAdapter implements SpinnerAdapter {
    private final Context mContext;
    private Things mThings = new Things();

    public SpinnerThingAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mThings.size();
    }

    @Override
    public Object getItem(int position) {
        return mThings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.spinner_thing, null);
        }

        IThing thing = (IThing) getItem(position);

        ImageView img = (ImageView) convertView.findViewById(R.id.img_spin_icon);
        TextView txtName = (TextView) convertView.findViewById(R.id.txt_spin_name);
        TextView txtSrc = (TextView) convertView.findViewById(R.id.txt_spin_source);

        if (thing.getService() == IService.ServicesTypes.SmartThings) {
            img.setImageResource(R.drawable.icon_switch);
            txtSrc.setText(R.string.device_st_label);
        } else if (thing.getService() == IService.ServicesTypes.PhilipsHue) {
            img.setImageResource(R.drawable.icon_phlogo);
            txtSrc.setText(R.string.device_ph_label);
        }

        txtName.setText(thing.getName());

        return convertView;
    }

    public List<IThing> getThings() {
        return mThings;
    }

    public void setThings(Things mThings) {
        this.mThings = mThings;
    }
}
