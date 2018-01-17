package shane.pennihome.local.smartboard.UI.Interface;

import android.content.Context;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.Interface.IBlock;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;

/**
 * Created by SPennicott on 17/01/2018.
 */

public abstract class IBlockUI {
    IBlock mIBlock;

    public abstract void buildBlockPropertyView(final Context context, View view, Things things, final Group group);
    public abstract void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener);
    public abstract BaseEditorViewHolder GetEditorViewHolder(View view);

    public IBlockUI(IBlock block)
    {
        mIBlock = block;
    }

    public IBlock getBlock(){return mIBlock;}

    public class BaseEditorViewHolder extends AbstractDraggableItemViewHolder
    {
        public BaseEditorViewHolder(View itemView) {
            super(itemView);
        }
    }
}
