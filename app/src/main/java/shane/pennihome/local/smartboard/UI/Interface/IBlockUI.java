package shane.pennihome.local.smartboard.UI.Interface;

import android.app.Activity;
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
    private IBlock mIBlock;

    public abstract void buildBlockPropertyView(Activity activity, View view, Things things, Group group);
    public abstract void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener);
    public abstract BaseEditorViewHolder GetEditorViewHolder(View view);
    public abstract void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId, View.OnClickListener onClickListener);

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
