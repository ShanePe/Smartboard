package shane.pennihome.local.smartboard.adapters;

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

public class ServiceLoadAdapter extends RecyclerView.Adapter<ServiceLoadAdapter.ViewHolder> {
    Services mServices;
    ArrayList<IThingsGetter> mGetters;
    RecyclerView mRecycleView;

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

        mRecycleView = (RecyclerView) parent;
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
        View mView;
        TextView mDesc;
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
            final Animation animation = AnimationUtils.loadAnimation(mView.getContext(), R.anim.slide_out_left);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mView.setVisibility(View.GONE);
                    int getterPosition = mGetters.indexOf(mGetter);
                    mGetters.remove(mGetter);
                    mRecycleView.getAdapter().notifyItemRemoved(getterPosition);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

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
