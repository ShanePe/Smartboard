package shane.pennihome.local.smartboard.thingsframework.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.Blocks;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.ui.listeners.OnBlockSelectListener;

/**
 * Created by shane on 17/01/18.
 */

@SuppressWarnings("ALL")
public class BlockSelectionAdapter extends RecyclerView.Adapter<BlockSelectionAdapter.ViewHolder> {

    private final Blocks mBlocks;
    private final OnBlockSelectListener mOnBlockSelectListener;

    public BlockSelectionAdapter(OnBlockSelectListener onBlockSelectListener) {
        this.mBlocks = Blocks.getAvailableTypes();
        this.mOnBlockSelectListener = onBlockSelectListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_thing_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final IBlock t = mBlocks.get(position);
        holder.mText.setText(t.getFriendlyName());
        holder.mImg.setImageResource(t.getDefaultIconResource());

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBlockSelectListener != null)
                    mOnBlockSelectListener.BlockSelected(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlocks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout mContainer;
        private final TextView mText;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.things_select_container);
            mText = itemView.findViewById(R.id.things_select_name);
            mImg = itemView.findViewById(R.id.things_select_img);
        }
    }
}
