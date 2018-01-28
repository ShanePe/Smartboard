package shane.pennihome.local.smartboard.thingsframework.interfaces;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;

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

    public <E extends IThing> E getThing(Class<E> cls) {
        //noinspection unchecked
        return (E) getThing();
    }

    protected void DoTabs(View view, int propToggleBtn, int propBGBtn, int tabPropView, int tabBGView) {
        final ToggleButton btnTabProp = view.findViewById(propToggleBtn);
        final ToggleButton btnTabBg = view.findViewById(propBGBtn);

        final View tabProp = view.findViewById(tabPropView);
        final View tabBg = view.findViewById(tabBGView);

        if (btnTabProp != null) {
            btnTabProp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btnTabBg.setChecked(false);
                        tabProp.setVisibility(View.VISIBLE);
                        tabBg.setVisibility(View.GONE);
                    }

                }
            });
            btnTabProp.setChecked(true);
        }

        if (btnTabBg != null) {
            btnTabBg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        btnTabProp.setChecked(false);
                        tabProp.setVisibility(View.GONE);
                        tabBg.setVisibility(View.VISIBLE);
                    }
                }
            });
            tabBg.setVisibility(View.GONE);
        }
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
