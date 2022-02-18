package shane.pennihome.local.smartboard.services.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.services.ServiceManager;
import shane.pennihome.local.smartboard.services.Services;
import shane.pennihome.local.smartboard.services.interfaces.IService;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 31/01/18.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private final Services mServices;

    public ServiceAdapter() {
        this.mServices = ServiceManager.getServices();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mService = mServices.get(position);
        holder.btnService.setText(holder.mService.getName());
        holder.btnService.setBackgroundResource(holder.mService.isActive() ? R.drawable.btn_round_accent : R.drawable.btn_round);
        Drawable drawable = ResourcesCompat.getDrawable(holder.mView.getResources(), holder.mService.getDrawableIconResource(), null);
        holder.btnService.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        holder.btnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ServiceManager serviceManager = new ServiceManager();
                if (holder.mService.isActive())
                    //noinspection rawtypes
                    UIHelper.showConfirm(holder.mView.getContext(), "Confirm",
                            String.format("Are you sure you want to unregister from the %s service?", holder.mService.getName()),
                            new OnProcessCompleteListener() {
                                @Override
                                public void complete(boolean success, Object source) {
                                    if (success) {
                                        serviceManager.unRegisterService(holder.mView.getContext(), holder.mService, new OnProcessCompleteListener<Void>() {
                                            @Override
                                            public void complete(boolean success, Void source) {
                                                if (success)
                                                    notifyItemChanged(holder.getAdapterPosition());
                                            }
                                        });
                                    }
                                }
                            });
                else {
                    serviceManager.registerService((AppCompatActivity) holder.mView.getContext(), holder.mService, new OnProcessCompleteListener<IService>() {
                        @Override
                        public void complete(boolean success, IService source) {
                            holder.mService = source;
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final Button btnService;
        IService mService;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btnService = itemView.findViewById(R.id.ser_name);
        }
    }
}
