package shane.pennihome.local.smartboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;

/**
 * Created by SPennicott on 10/02/2018.
 */

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {
    private Templates mTemplates;
    private OnInteractionListener mOnInteractionListener;

    public void setOnInteractionListener(OnInteractionListener oninteractionlistener) {
        this.mOnInteractionListener = oninteractionlistener;
    }

    public Templates getTemplates() {
        return mTemplates;
    }

    public TemplateAdapter(Templates templates) {
        this.mTemplates = templates;
    }

    public int addTemplate(Template template) {
        if (mTemplates == null)
            mTemplates = new Templates();
        mTemplates.add(template);
        return mTemplates.size() - 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_template, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mTemplates.get(position);
        holder.mTemplateName.setText(holder.mItem.getName());
        holder.mTemplateThingType.setText(holder.mItem.getBlock().getThingType().toString());
        holder.mTemplateSize.setText(String.format("%sx%s", holder.mItem.getBlock().getWidth(), holder.mItem.getBlock().getHeight()));
        holder.mItem.getBlock().renderTemplateBackgroundTo(holder.mContainer);
        if (holder.mItem.getBlock() instanceof IIconBlock)
            ((IIconBlock) holder.mItem.getBlock()).renderIconTo(holder.mIcon);
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnInteractionListener != null)
                    mOnInteractionListener.onInteraction(mTemplates.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
        holder.mContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnInteractionListener != null)
                    mOnInteractionListener.onItemLongClick(mTemplates.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTemplates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout mContainer;
        final TextView mTemplateName;
        final ImageView mIcon;
        final TextView mTemplateThingType;
        final TextView mTemplateSize;
        Template mItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.tmp_container);
            mTemplateName = itemView.findViewById(R.id.tmp_name);
            mIcon = itemView.findViewById(R.id.tmp_icon);
            mTemplateThingType = itemView.findViewById(R.id.tmp_thing);
            mTemplateSize = itemView.findViewById(R.id.tmp_size);
        }
    }

    public interface OnInteractionListener {
        void onInteraction(Template item, int position);

        void onItemLongClick(Template item, int position);
    }
}
