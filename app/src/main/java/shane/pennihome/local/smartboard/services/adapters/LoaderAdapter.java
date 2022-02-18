package shane.pennihome.local.smartboard.services.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 30/01/18.
 */

public class LoaderAdapter extends RecyclerView.Adapter<LoaderAdapter.ViewHolder> {
    private ArrayList<Pair<String, String>> mMessages;

    public LoaderAdapter() {
        mMessages = new ArrayList<>();
    }

    public ArrayList<Pair<String, String>> getMessages() {
        return mMessages;
    }

    public void setMessages(ArrayList<Pair<String, String>> messages) {
        this.mMessages = messages;
    }

    public void addMessage(Pair<String, String> message) {
        this.mMessages.add(message);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_startup, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mLoadMessage = mMessages.get(position);
        holder.mDesc.setText(holder.mLoadMessage.second);
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mDesc;
        Pair<String, String> mLoadMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mDesc = itemView.findViewById(R.id.sl_description);
        }

        public Pair<String, String> getLoadMessage() {
            return mLoadMessage;
        }

        public void slideOut() {
            final Animation animation = AnimationUtils.loadAnimation(mView.getContext(), R.anim.slide_out_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
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
