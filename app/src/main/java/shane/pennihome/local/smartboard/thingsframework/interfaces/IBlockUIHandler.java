package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
public abstract class IBlockUIHandler {
    private IBlock mIBlock;

    protected IBlockUIHandler(IBlock block) {
        mIBlock = block;
    }

    public abstract void buildEditorWindowView(Activity activity, View view, Things things, Group group);

    public abstract void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener);

    public abstract BlockEditViewHolder GetEditHolder(View view);

    public abstract BlockViewHolder GetViewHolder(View view);

    public abstract void BindViewHolder(BlockEditViewHolder viewHolder, int backgroundResourceId);

    public abstract int getEditLayoutID();

    public abstract int getViewLayoutID();

    public abstract int getEditorWindowLayoutID();

    protected IBlock getBlock() {
        return mIBlock;
    }

    public void setBlock(IBlock block)
    {
        mIBlock = block;
    }

    public <E extends IBlock> E getBlock(Class<E> cls) {
        //noinspection unchecked
        return (E) getBlock();
    }

    public class BlockEditViewHolder extends AbstractDraggableItemViewHolder {
        public BlockEditViewHolder(View itemView) {
            super(itemView);
        }
        public View getContainer()
        {
            return ((ViewGroup)itemView).getChildAt(0);
        }
    }

    public class BlockViewHolder extends RecyclerView.ViewHolder {
        public BlockViewHolder(View itemView) {
            super(itemView);
        }
    }
}
