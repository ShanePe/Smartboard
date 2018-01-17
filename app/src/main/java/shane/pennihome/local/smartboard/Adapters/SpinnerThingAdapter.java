package shane.pennihome.local.smartboard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Switch;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 14/01/18.
 */

public class SpinnerThingAdapter extends BaseAdapter implements SpinnerAdapter {
    private Things mThings = new Things();
    private Context mContext;

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
            convertView = layoutInflater.inflate(R.layout.spinner_thing, null);
        }

        IThing thing = (IThing) getItem(position);

        ImageView img = (ImageView) convertView.findViewById(R.id.img_spin_icon);
        TextView txtType = (TextView) convertView.findViewById(R.id.txt_spin_type);
        TextView txtName = (TextView) convertView.findViewById(R.id.txt_spin_name);
        TextView txtSrc = (TextView) convertView.findViewById(R.id.txt_spin_source);

        if (thing.getSource() == IThing.Source.SmartThings) {
            img.setImageResource(R.drawable.icon_switch);
            txtSrc.setText(R.string.device_st_label);
        } else if (thing.getSource() == IThing.Source.PhilipsHue) {
            img.setImageResource(R.drawable.icon_phlogo);
            txtSrc.setText(R.string.device_ph_label);
        }

        if (thing instanceof Switch)
            txtType.setText(R.string.lbl_device);
        else if (thing instanceof Routine)
            txtType.setText(R.string.lbl_routine);

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
