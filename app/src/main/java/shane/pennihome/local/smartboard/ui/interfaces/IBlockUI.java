package shane.pennihome.local.smartboard.ui.interfaces;

import android.app.Activity;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockClickListener;
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.things.Things;

/**
 * Created by SPennicott on 17/01/2018.
 */

public abstract class IBlockUI {
    private IBlock mIBlock;

    public IBlockUI(IBlock block) {
        mIBlock = block;
    }

    public abstract void buildBlockPropertyView(Activity activity, View view, Things things, Group group);

    public abstract void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener);

    public abstract BaseEditorViewHolder GetEditorViewHolder(View view);

    public abstract void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId, OnBlockClickListener onBlockClickListener);

    public IBlock getBlock() {
        return mIBlock;
    }

    public class BaseEditorViewHolder extends AbstractDraggableItemViewHolder {
        public BaseEditorViewHolder(View itemView) {
            super(itemView);
        }
    }
}
