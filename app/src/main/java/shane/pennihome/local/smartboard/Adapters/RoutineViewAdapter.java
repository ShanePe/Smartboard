package shane.pennihome.local.smartboard.Adapters;

import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RoutineViewAdapter extends ThingViewAdapter {
    public RoutineViewAdapter(List<IThing> items) {
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
        final Routine r = (Routine) mValues.get(position);
        vh.mItem = r;
        vh.mNameView.setText(r.getName());

        if (vh.mItem.getSource() == IThing.Source.SmartThings) {
            vh.mImgView.setImageResource(R.drawable.icon_switch);
            vh.mSourceView.setText(R.string.device_st_label);
        } else if (vh.mItem.getSource() == IThing.Source.PhilipsHue) {
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
        final View mView;
        final ImageView mImgView;
        final TextView mNameView;
        final AppCompatImageButton mButtonView;
        final TextView mSourceView;
        Routine mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mImgView = view.findViewById(R.id.routine_img);
            mNameView = view.findViewById(R.id.routine_name);
            mButtonView = view.findViewById(R.id.routine_btn_execute);
            mSourceView = view.findViewById(R.id.routine_source);
        }
    }
}
