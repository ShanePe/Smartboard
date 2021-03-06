package shane.pennihome.local.smartboard.things.routines;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.adapters.ThingViewAdapter;

/**
 * Created by shane on 30/12/17.
 */

public class RoutineViewAdapter extends ThingViewAdapter {
    public RoutineViewAdapter(Things items) {
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder vh = (ViewHolder) holder;
        final Routine r = (Routine) getThings().get(position);
        vh.mItem = r;
        vh.mNameView.setText(r.getName());

        if (vh.mItem.getServiceType() == IService.ServicesTypes.SmartThings) {
            vh.mImgView.setImageResource(R.mipmap.icon_switch_mm_fg);
            vh.mSourceView.setText(R.string.device_st_label);
        } else if (vh.mItem.getServiceType() == IService.ServicesTypes.PhilipsHue) {
            vh.mImgView.setImageResource(R.mipmap.icon_phlogo_mm_fg);
            vh.mSourceView.setText(R.string.device_ph_label);
        } else if (vh.mItem.getServiceType() == IService.ServicesTypes.HarmonyHub) {
            vh.mImgView.setImageResource(R.mipmap.logo_harm_mm_fg);
            vh.mSourceView.setText(R.string.device_harm_label);
        }

        vh.mButtonView.setEnabled(!vh.mItem.isUnreachable());

        vh.mButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vh.mItem.execute();
                Toast.makeText(vh.mImgView.getContext(), "Execute routine : " + vh.mItem.getName(), Toast.LENGTH_SHORT).show();
            }
        });

//        vh.mItem.setOnThingActionListener(new OnThingActionListener() {
//            @Override
//            public void OnReachableStateChanged(boolean isUnReachable) {
//                vh.mButtonView.setEnabled(!isUnReachable);
//            }
//        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
