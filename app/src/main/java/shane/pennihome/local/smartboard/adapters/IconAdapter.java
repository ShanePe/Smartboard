package shane.pennihome.local.smartboard.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

import shane.pennihome.local.smartboard.R;

/**
 * Created by SPennicott on 06/02/2018.
 */

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder>{

    private String[] mIcons;
    private int mSelectedPos = RecyclerView.NO_POSITION;
    private String mSelected;
    private RecyclerView mRecycleView;

    public IconAdapter(Context context)
    {
        AssetManager assetManager = context.getAssets();
        try {
            mIcons = assetManager.list("icons");
        } catch (IOException e) {
            mIcons = new String[]{};
        }
    }

    public String getSelected()
    {
        if(mSelectedPos == RecyclerView.NO_POSITION)
            return "";
        else
            return "icons/" + mIcons[mSelectedPos];
    }

    public void setSelected(String iconPath)
    {
        mSelected = iconPath;
    }

    public int getSelectedPosition()
    {
        return mSelectedPos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mRecycleView = (RecyclerView) parent;

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImageView img = (ImageView)holder.itemView;
        final int pos = holder.getAdapterPosition() == -1 ? position : holder.getAdapterPosition();
        img.post(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream stream = holder.itemView.getContext().getAssets().open("icons/" + mIcons[pos]);
                    img.setImageDrawable(Drawable.createFromStream(stream, mIcons[pos]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(mSelectedPos);
                mSelectedPos = pos;
                mSelected = mIcons[pos];
                notifyItemChanged(mSelectedPos);
            }
        });
        if (("icons/" + mIcons[pos]).equals(mSelected)) {
            mSelectedPos = pos;
            mRecycleView.post(new Runnable() {
                @Override
                public void run() {
                    mRecycleView.getLayoutManager().scrollToPosition(pos);
                }
            });
        }

        holder.itemView.setSelected(position == mSelectedPos);
    }

    @Override
    public int getItemCount() {
        return mIcons.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
