package shane.pennihome.local.smartboard.things.dimmergroup;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.things.switches.SwitchPropertiesClrSelector;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.MultiThingSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesIcon;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by shane on 19/02/18.
 */

public class DimmerGroupUIHandler extends IBlockUIHandler {
    protected DimmerGroupUIHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(Activity activity, View view, Things things, Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.dg_swiper);
        TabLayout tabLayout = view.findViewById(R.id.dg_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.dg_tab_properties);
        viewSwiper.addView("Devices", R.id.dg_tab_things);
        viewSwiper.addView("Colours", R.id.dg_tab_background);
        viewSwiper.addView("Template", R.id.dg_tab_template);

        final ThingPropertiesIcon tpProps = view.findViewById(R.id.dg_properties);
        final SwitchPropertiesClrSelector tpBackground = view.findViewById(R.id.dg_background);
        MultiThingSelector multiThingSelector = view.findViewById(R.id.dg_things);

        TemplateProperties tempProps = view.findViewById(R.id.dg_template);

        IThings<Switch> iThings = Monitor.getMonitor().getThings(Switch.class);
        Things dimmable = new Things();
        for (Switch s : iThings)
            if (s.isDimmer())
                dimmable.add(s);

        tpProps.initialise(null, (IIconBlock) getBlock());
        tpBackground.initialise(getBlock(SwitchBlock.class));
        multiThingSelector.setThings(dimmable);

        if (getBlock().getThing() != null)
            for (String key : getBlock(DimmerGroupBlock.class).getThingKeys())
                if (getBlock(DimmerGroupBlock.class).getThings().getByKey(key) == null)
                    getBlock(DimmerGroupBlock.class).getThings()
                            .add(Monitor.getMonitor().getThings().getByKey(key));

        multiThingSelector.setSelectedThings(getBlock(DimmerGroupBlock.class).getThings());
        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.DimmerGroup));
        tempProps.setOnTemplateActionListener(new TemplateProperties.OnTemplateActionListener() {
            @Override
            public void OnTemplateSelected(Template template) {
                tpProps.applyTemplate(template);
                tpBackground.applyTemplate(template);
            }
        });
    }

    @Override
    public void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener) {
        try {
            ViewSwiper viewSwiper = view.findViewById(R.id.dg_swiper);
            ThingPropertiesIcon tbProps = (ThingPropertiesIcon) viewSwiper.getView(R.id.dg_properties);
            SwitchPropertiesClrSelector tbBackground = (SwitchPropertiesClrSelector) viewSwiper.getView(R.id.dg_background);
            TemplateProperties tempProps = (TemplateProperties) viewSwiper.getView(R.id.dg_template);
            MultiThingSelector multiThingSelector = (MultiThingSelector) viewSwiper.getView(R.id.dg_things);

            getBlock().setThing(new DimmerGroup());
            tbProps.populate((IIconBlock) getBlock(), null);
            tbBackground.populate(getBlock(SwitchBlock.class));

            getBlock(DimmerGroupBlock.class).getThingKeys().clear();
            getBlock(DimmerGroupBlock.class).getThings().clear();

            for (IThing t : multiThingSelector.getSelectedThings())
                getBlock(DimmerGroupBlock.class).getThingKeys().add(t.getKey());

            if (tempProps.isSaveAsTemplate())
                tempProps.createTemplate(view.getContext(), getBlock());

            if (onBlockSetListener != null)
                onBlockSetListener.OnSet(getBlock());
        } catch (Exception ex) {
            Toast.makeText(view.getContext(), "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public BlockEditViewHolder GetEditHolder(View view) {
        return new DimmerGroupEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new DimmerGroupViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if (getBlock().getThing() == null)
            return;

        DimmerGroupEditorHolder holder = (DimmerGroupEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        if (getBlock().getThing() != null)
            holder.mBaDevice.setText(String.format("%s Device(s)", getBlock(DimmerGroupBlock.class).getThingKeys().size()));

        holder.mBaSize.setText(String.format("%s x %s", getBlock().getWidth(), getBlock().getHeight()));

        @ColorInt int bgClr = UIHelper.getThingColour(getBlock().getThing(),
                getBlock().getBackgroundColourWithAlpha(),
                getBlock(SwitchBlock.class).getBackgroundColourWithAlphaOn());
        @ColorInt int fgClr = UIHelper.getThingColour(getBlock().getThing(),
                getBlock().getForegroundColour(),
                getBlock(SwitchBlock.class).getForegroundColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);
    }

    @Override
    public void BindViewHolder(BlockViewHolder viewHolder) {

    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_dimmergroup;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_dimmergroup;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_dimmergroup;
    }

    public class DimmerGroupEditorHolder extends BlockEditViewHolder {
        public final FrameLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        //      public final FrameLayout mContainer;

        public DimmerGroupEditorHolder(View view) {
            super(view);
//            mContainer = view.findViewById(R.id.sw_dashboard_block);
            mLayout = view.findViewById(R.id.dg_block_area);
            mBaName = view.findViewById(R.id.dg_ba_name);
            mBaImg = view.findViewById(R.id.dg_ba_image);
            mBaDevice = view.findViewById(R.id.dg_ba_device);
            mBaSize = view.findViewById(R.id.dg_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class DimmerGroupViewHolder extends BlockViewHolder {
        LinearLayoutCompat mContainer;
        TextView mTitle;
        ImageView mIcon;
        ProgressBar mProgress;
        SeekBar mDimmer;

        public DimmerGroupViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.bvs_container);
            mTitle = itemView.findViewById(R.id.bvs_title);
            mIcon = itemView.findViewById(R.id.bvs_icon);
            mProgress = itemView.findViewById(R.id.bvs_progress);
            mDimmer = itemView.findViewById(R.id.bvs_dimmer);
        }
    }
}
