package shane.pennihome.local.smartboard.services.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.services.interfaces.IThingsGetter;

/**
 * Created by shane on 30/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ServiceLoadAdapter extends RecyclerView.Adapter<ServiceLoadAdapter.ViewHolder> {
    private Services mServices;
    private ArrayList<IThingsGetter> mGetters;

    public Services getServices() {
        if (mServices == null)
            mServices = new Services();
        return mServices;
    }

    public void setServices(Services services) {
        mServices = services;
        mGetters = new ArrayList<>();
        for (IService service : mServices)
            mGetters.addAll(service.getThingGetters());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_startup, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mGetter = mGetters.get(position);
        holder.mDesc.setText(holder.mGetter.getLoadMessage());
    }

    @Override
    public int getItemCount() {
        return mGetters == null ? 0 : mGetters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mDesc;
        IThingsGetter mGetter;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDesc = itemView.findViewById(R.id.sl_description);
        }

        public IThingsGetter getThingGetter() {
            return mGetter;
        }

        public void slideOut() {
            final Animation animation = AnimationUtils.loadAnimation(mView.getContext(), R.anim.slide_out_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    mView.setVisibility(View.GONE);
                }
            });

            mView.post(new Runnable() {
                @Override
                public void run() {
                    mView.startAnimation(animation);
                }
            });

        }
    }
}
