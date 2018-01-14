package shane.pennihome.local.smartboard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Data.Device;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

/**
 * Created by shane on 14/01/18.
 */

public class SpinnerThingAdapter extends BaseAdapter implements SpinnerAdapter {
    private List<Thing> mThings = new ArrayList<>();
    private SmartboardActivity mSmartboardActivity;

    public SpinnerThingAdapter(SmartboardActivity mSmartboardActivity) {
        this.mSmartboardActivity = mSmartboardActivity;
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
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.spinner_thing, null);
        }

        Thing thing = (Thing)getItem(position);

        ImageView img = (ImageView)convertView.findViewById(R.id.img_spin_icon);
        TextView txtType = (TextView) convertView.findViewById(R.id.txt_spin_type);
        TextView txtName = (TextView)convertView.findViewById(R.id.txt_spin_name);
        TextView txtSrc = (TextView)convertView.findViewById(R.id.txt_spin_source);

        if (thing.getSource() == Thing.Source.SmartThings) {
            img.setImageResource(R.drawable.icon_switch);
            txtSrc.setText(R.string.device_st_label);
        } else if (thing.getSource() == Thing.Source.PhilipsHue) {
            img.setImageResource(R.drawable.icon_phlogo);
            txtSrc.setText(R.string.device_ph_label);
        }

        if(thing instanceof Device)
            txtType.setText(R.string.lbl_device);
        else if(thing instanceof Routine)
            txtType.setText(R.string.lbl_routine);

        txtName.setText(thing.getName());

        return convertView;
    }

    public void setThings(List<Thing> mThings) {
        this.mThings = mThings;
    }

    public List<Thing> getThings()
    {
        return mThings;
    }
}
