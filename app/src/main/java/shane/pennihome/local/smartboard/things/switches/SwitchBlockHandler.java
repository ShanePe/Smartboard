package shane.pennihome.local.smartboard.things.switches;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.ThingProperties;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
public class SwitchBlockHandler extends IBlockUIHandler {

    public SwitchBlockHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(final Activity activity, View view, Things things, final Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.sw_swiper);
        TabLayout tabLayout = view.findViewById(R.id.sw_tabs);
        viewSwiper.setTabLayout(tabLayout);
        viewSwiper.getViewAdapter().addView("Properties", R.id.sw_tab_properties);
        viewSwiper.getViewAdapter().addView("Colours", R.id.sw_tab_background);

        ThingProperties tpProps = view.findViewById(R.id.sw_properties);
        SwitchPropertiesClrSelector tpBackground = view.findViewById(R.id.sw_background);

        tpProps.initialise(things, getBlock());
        tpBackground.initialise(getBlock(SwitchBlock.class));
    }

    @Override
    public void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener) {
        ThingProperties tbProps = view.findViewById(R.id.sw_properties);
        SwitchPropertiesClrSelector tbBackground = view.findViewById(R.id.sw_background);

        tbProps.populate(getBlock(), null);
        tbBackground.populate(getBlock(SwitchBlock.class));

        if (onBlockSetListener != null)
            onBlockSetListener.OnSet(getBlock());
    }

    @Override
    public BlockEditViewHolder GetEditHolder(View view) {
        return new SwitchEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new SwitchViewHolder(view);
    }

    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        SwitchEditorHolder holder = (SwitchEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        if (getBlock().getThing() != null)
            holder.mBaDevice.setText(getBlock().getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s", getBlock().getWidth(), getBlock().getHeight()));

        @ColorInt final int bgClr = UIHelper.getThingColour(getBlock().getThing(), getBlock().getBackgroundColourWithAlpha(), getBlock(SwitchBlock.class).getBackgroundColourWithAlphaOn());
        @ColorInt int fgClr = UIHelper.getThingColour(getBlock().getThing(), getBlock().getForegroundColour(), getBlock(SwitchBlock.class).getForegroundColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);

        if (backgroundResourceId != 0)
            holder.mContainer.setBackgroundResource(backgroundResourceId);
    }

    @Override
    public void BindViewHolder(BlockViewHolder viewHolder) {
        
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_switch;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_switch;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_block_switch;
    }

    public class SwitchEditorHolder extends BlockEditViewHolder {
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public final FrameLayout mContainer;

        public SwitchEditorHolder(View view) {
            super(view);
            mContainer = view.findViewById(R.id.sw_dashboard_block);
            mLayout = view.findViewById(R.id.sw_block_area);
            mBaName = view.findViewById(R.id.sw_ba_name);
            mBaImg = view.findViewById(R.id.sw_ba_image);
            mBaDevice = view.findViewById(R.id.sw_ba_device);
            mBaSize = view.findViewById(R.id.sw_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class SwitchViewHolder extends BlockViewHolder {
        LinearLayoutCompat mContainer;
        TextView mTitle;

        public SwitchViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.bvs_container);
            mTitle = itemView.findViewById(R.id.bvs_title);
        }
    }
}
