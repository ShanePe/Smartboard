package shane.pennihome.local.smartboard.things.routines;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThingUIHandler;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.ui.ThingBackground;
import shane.pennihome.local.smartboard.ui.ThingProperties;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
public class RoutineThingHandler extends IThingUIHandler {
    public RoutineThingHandler(IThing iThing) {
        super(iThing);
    }

    @Override
    public void buildBlockPropertyView(final Activity activity, View view, Things things, final Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.rt_swiper);
        TabLayout tabLayout = view.findViewById(R.id.rt_tabs);
        viewSwiper.setTabLayout(tabLayout);
        viewSwiper.getViewAdapter().addView("Properties", R.id.rt_tab_properties);
        viewSwiper.getViewAdapter().addView("Colours", R.id.rt_tab_background);

        ThingProperties tpProps = view.findViewById(R.id.rt_properties);
        ThingBackground tpBackground = view.findViewById(R.id.rt_background);

        tpProps.initialise(things, getThing());
        tpBackground.initialise(getThing());
    }

    @Override
    public void populateBlockFromView(View view, OnThingSetListener onThingSetListener) {
        ThingProperties tbProps = view.findViewById(R.id.rt_properties);
        ThingBackground tbBackground = view.findViewById(R.id.rt_background);

        tbProps.populate(getThing(), null);
        tbBackground.populate(getThing());

        if (onThingSetListener != null)
            onThingSetListener.OnSet(getThing());
    }

    @Override
    public BaseEditorViewHolder GetEditorViewHolder(View view) {
        return new EditorViewHolder(view);
    }

    public void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId) {
        EditorViewHolder holder = (EditorViewHolder) viewHolder;

        holder.mBaName.setText(getThing().getBlock().getName());
        if (getThing() != null)
            holder.mBaImg.setImageResource(getThing().getDefaultIconResource());

        holder.mBaDevice.setText(getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s", getThing().getBlock().getWidth(), getThing().getBlock().getHeight()));

        @ColorInt final int bgClr = getThing().getBlock().getBackgroundColourWithAlpha();
        @ColorInt int fgClr = getThing().getBlock().getForeColour();

        // getThing().getBlock().renderBackground(holder.mLayout);
        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);

        if (backgroundResourceId != 0)
            holder.mContainer.setBackgroundResource(backgroundResourceId);
    }

    @Override
    public int getViewResourceID() {
        return R.layout.dashboard_block_routine;
    }

    @Override
    public int getEditorViewResourceID() {
        return R.layout.prop_block_routine;
    }


    public class EditorViewHolder extends BaseEditorViewHolder {
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public final FrameLayout mContainer;

        public EditorViewHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.rt_cv_dashboard_block);
            mLayout = view.findViewById(R.id.rt_block_area);
            mBaName = view.findViewById(R.id.rt_ba_name);
            mBaImg = view.findViewById(R.id.rt_ba_image);
            mBaDevice = view.findViewById(R.id.rt_ba_device);
            mBaSize = view.findViewById(R.id.rt_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }
}
