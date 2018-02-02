package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.app.Activity;
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

    public abstract void buildBlockPropertyView(Activity activity, View view, Things things, Group group);

    public abstract void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener);

    public abstract BaseEditorViewHolder GetEditorViewHolder(View view);

    public abstract void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId);

    public abstract int getViewResourceID();

    public abstract int getEditorViewResourceID();

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

    public class BaseEditorViewHolder extends AbstractDraggableItemViewHolder {
        public BaseEditorViewHolder(View itemView) {
            super(itemView);
        }
        public View getContainer()
        {
            return ((ViewGroup)itemView).getChildAt(0);
        }
    }
}
