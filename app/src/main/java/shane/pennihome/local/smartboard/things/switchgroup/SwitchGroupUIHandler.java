package shane.pennihome.local.smartboard.things.switchgroup;

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
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
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
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.MultiThingSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesIcon;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by shane on 19/02/18.
 */

class SwitchGroupUIHandler extends IBlockUIHandler {
    SwitchGroupUIHandler(IBlock block) {
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

        Things selectable = new Things();
        selectable.addAll(Monitor.getMonitor().getThings(Switch.class));

        TemplateProperties tempProps = view.findViewById(R.id.dg_template);
        tpProps.initialise(null,null, (IIconBlock) getBlock());
        tpBackground.initialise(getBlock(SwitchBlock.class));

        multiThingSelector.setData(Monitor.getMonitor().getServices(), selectable);

        getBlock(SwitchGroupBlock.class).loadThing();

        multiThingSelector.setSelectedThings(getBlock().getThing(SwitchGroup.class).getChildThings());
        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.SwitchGroup));
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

            if (getBlock().getThing() == null)
                getBlock().setThing(new SwitchGroup());

            tbProps.populate((IIconBlock) getBlock(), null);
            tbBackground.populate(getBlock(SwitchBlock.class));

            getBlock(SwitchGroupBlock.class).getThingKeys().clear();
            for (IThing t : multiThingSelector.getSelectedThings())
                getBlock(SwitchGroupBlock.class).getThingKeys().add(t.getKey());

            getBlock().getThing(SwitchGroup.class).getChildThings().clear();

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
        return new SwitchGroupEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new SwitchGroupViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if (getBlock().getThing() == null)
            return;

        SwitchGroupEditorHolder holder = (SwitchGroupEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        if (getBlock().getThing() != null)
            holder.mBaDevice.setText(String.format("%s Device(s)", getBlock(SwitchGroupBlock.class).getThingKeys().size()));

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
        final SwitchGroupViewHolder holder = (SwitchGroupViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());
        holder.mTitle.setVisibility(getBlock().isHideTitle() ? View.GONE : View.VISIBLE);

        getBlock().renderForegroundColourTo(holder.mTitle);
        getBlock().renderBackgroundTo(holder.itemView);
        getBlock().renderUnreachableBackground(holder.itemView);
        getBlock(SwitchGroupBlock.class).renderIconTo(holder.mIcon);
        getBlock().renderForegroundColourTo(holder.mDimmer);
        getBlock().renderForegroundColourTo(holder.mProgress);
        getBlock().startListeningForChanges();

        SwitchGroup sg = getBlock().getThing(SwitchGroup.class);
        if (sg.getChildThings().size() != 0) {
            holder.mDimmer.setVisibility(getBlock().getThing(Switch.class).isDimmer() ? View.VISIBLE : View.GONE);
            holder.mDimmer.setProgress(getBlock().getThing(SwitchGroup.class).getDimmerLevel());
            holder.mDimmer.setEnabled(getBlock().getThing(SwitchGroup.class).isOn());
        } else
            holder.mDimmer.setEnabled(false);

        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute(holder, v, false);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UIHelper.doLongClickReact(v);
                execute(holder, v, true);
                return true;
            }
        });

        holder.mDimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getBlock(SwitchGroupBlock.class).execute(holder.mProgress, "level", holder.mDimmer.getProgress(),
                        new OnProcessCompleteListener<String>() {
                            @Override
                            public void complete(boolean success, String source) {
                                if (!success)
                                    Toast.makeText(holder.itemView.getContext(), "Error:" + source, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        getBlock().setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(IThing thing) {
                getBlock().renderUnreachableBackground(holder.itemView);
            }

            @Override
            public void OnStateChanged(IThing thing) {
                getBlock().renderForegroundColourTo(holder.mTitle);
                getBlock().renderBackgroundTo(holder.itemView);
                getBlock().renderUnreachableBackground(holder.itemView);
                getBlock(SwitchBlock.class).renderIconTo(holder.mIcon);
                holder.mDimmer.setEnabled(getBlock().getThing(SwitchGroup.class).isOn());
            }

            @Override
            public void OnDimmerLevelChanged(IThing thing) {
                holder.mDimmer.setProgress(getBlock().getThing(SwitchGroup.class).getDimmerLevel());
            }

            @Override
            public void OnSupportColourFlagChanged(IThing thing) {

            }

            @Override
            public void OnSupportColourChanged(IThing thing) {

            }

            @Override
            public void OnDisabledChanged(IThing thing, boolean disabled) {
                getBlock().doEnabled(holder.itemView, !disabled);
            }
        });
    }

    private void execute(final SwitchGroupViewHolder holder, View currentView, boolean delay) {
        if (getBlock().getThing().isUnreachable() || holder.mProgress.getVisibility() == View.VISIBLE || currentView == holder.mDimmer)
            return;

        getBlock().execute(holder.mProgress, delay, new OnProcessCompleteListener<String>() {
            @Override
            public void complete(boolean success, String source) {
                if (!success)
                    Toast.makeText(holder.itemView.getContext(), "Error:" + source, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_switchgroup;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_switchgroup;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_switchgroup;
    }

    public class SwitchGroupEditorHolder extends BlockEditViewHolder {
        final FrameLayout mLayout;
        final TextView mBaName;
        final ImageView mBaImg;
        final TextView mBaDevice;
        final TextView mBaSize;

        SwitchGroupEditorHolder(View view) {
            super(view);

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

    public class SwitchGroupViewHolder extends BlockViewHolder {
        final LinearLayoutCompat mContainer;
        final TextView mTitle;
        final ImageView mIcon;
        final ProgressBar mProgress;
        final SeekBar mDimmer;

        SwitchGroupViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.bvdg_container);
            mTitle = itemView.findViewById(R.id.bvdg_title);
            mIcon = itemView.findViewById(R.id.bvdg_icon);
            mProgress = itemView.findViewById(R.id.bvdg_progress);
            mDimmer = itemView.findViewById(R.id.bvdg_dimmer);
        }
    }
}
