package shane.pennihome.local.smartboard.things.routines;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.adapters.ThingViewAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineViewAdapter extends ThingViewAdapter {
    public RoutineViewAdapter(IThings items) {
        super(items);
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_routine;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        final Routine r = (Routine) getThings().get(position);
        vh.mItem = r;
        vh.mNameView.setText(r.getName());

        if (vh.mItem.getService() == IService.ServicesTypes.SmartThings) {
            vh.mImgView.setImageResource(R.drawable.icon_switch);
            vh.mSourceView.setText(R.string.device_st_label);
        } else if (vh.mItem.getService() == IService.ServicesTypes.PhilipsHue) {
            vh.mImgView.setImageResource(R.drawable.icon_phlogo);
            vh.mSourceView.setText(R.string.device_ph_label);
        }

        vh.mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.Toggle();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImgView;
        final TextView mNameView;
        final AppCompatImageButton mButtonView;
        final TextView mSourceView;
        Routine mItem;

        ViewHolder(View view) {
            super(view);
            mImgView = view.findViewById(R.id.routine_img);
            mNameView = view.findViewById(R.id.routine_name);
            mButtonView = view.findViewById(R.id.routine_btn_execute);
            mSourceView = view.findViewById(R.id.routine_source);
        }
    }
}
