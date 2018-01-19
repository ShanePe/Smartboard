package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
public abstract class IThingUIHandler {
    private IThing mIThing;

    protected IThingUIHandler(IThing iThing) {
        mIThing = iThing;
    }

    public abstract void buildBlockPropertyView(Activity activity, View view, Things things, Group group);

    public abstract void populateBlockFromView(View view, OnThingSetListener onThingSetListener);

    public abstract BaseEditorViewHolder GetEditorViewHolder(View view);

    public abstract void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId);

    public abstract int getViewResourceID();

    public abstract int getEditorViewResourceID();

    protected IThing getThing() {
        return mIThing;
    }

    public void setThing(IThing thing)
    {
        mIThing = thing;
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
