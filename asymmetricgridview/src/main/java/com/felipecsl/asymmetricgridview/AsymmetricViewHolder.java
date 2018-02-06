package com.felipecsl.asymmetricgridview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

class AsymmetricViewHolder<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.ViewHolder {
    final VH wrappedViewHolder;

    AsymmetricViewHolder(VH wrappedViewHolder) {
        super(wrappedViewHolder.itemView);
        this.wrappedViewHolder = wrappedViewHolder;
    }

    AsymmetricViewHolder(View view) {
        super(view);
        wrappedViewHolder = null;
    }
}
