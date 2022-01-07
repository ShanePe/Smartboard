package shane.pennihome.local.smartboard.things.routinegroup;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.MultiThingSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesClrSelector;
import shane.pennihome.local.smartboard.ui.ThingPropertiesIcon;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by shane on 19/02/18.
 */

class RoutineGroupUIHandler extends IBlockUIHandler {
    RoutineGroupUIHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(Activity activity, View view, Things things, Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.rtg_swiper);
        TabLayout tabLayout = view.findViewById(R.id.rtg_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.rtg_tab_properties);
        viewSwiper.addView("Devices", R.id.rtg_tab_things);
        viewSwiper.addView("Colours", R.id.rtg_tab_background);
        viewSwiper.addView("Template", R.id.rtg_tab_template);

        final ThingPropertiesIcon tpProps = view.findViewById(R.id.rtg_properties);
        final ThingPropertiesClrSelector tpBackground = view.findViewById(R.id.rtg_background);
        MultiThingSelector multiThingSelector = view.findViewById(R.id.rtg_things);

        Things selectable = new Things();
        selectable.addAll(Monitor.getMonitor().getThings(Routine.class));

        TemplateProperties tempProps = view.findViewById(R.id.rtg_template);
        tpProps.initialise(null, (IIconBlock) getBlock());
        tpBackground.initialise(getBlock());
        multiThingSelector.setThings(selectable);

        getBlock(RoutineGroupBlock.class).loadThing();

        multiThingSelector.setSelectedThings(getBlock().getThing(RoutineGroup.class).getChildThings());
        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.RoutineGroup));
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
            ViewSwiper viewSwiper = view.findViewById(R.id.rtg_swiper);
            ThingPropertiesIcon tbProps = (ThingPropertiesIcon) viewSwiper.getView(R.id.rtg_properties);
            ThingPropertiesClrSelector tbBackground = (ThingPropertiesClrSelector) viewSwiper.getView(R.id.rtg_background);
            TemplateProperties tempProps = (TemplateProperties) viewSwiper.getView(R.id.rtg_template);
            MultiThingSelector multiThingSelector = (MultiThingSelector) viewSwiper.getView(R.id.rtg_things);

            if (getBlock().getThing() == null)
                getBlock().setThing(new RoutineGroup());

            tbProps.populate((IIconBlock) getBlock(), null);
            tbBackground.populate(getBlock());

            getBlock(RoutineGroupBlock.class).getThingKeys().clear();
            for (IThing t : multiThingSelector.getSelectedThings())
                getBlock(RoutineGroupBlock.class).getThingKeys().add(t.getKey());

            getBlock().getThing(RoutineGroup.class).getChildThings().clear();

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
        return new RoutineGroupEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new RoutineGroupViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if (getBlock().getThing() == null)
            return;

        RoutineGroupEditorHolder holder = (RoutineGroupEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        if (getBlock().getThing() != null)
            holder.mBaDevice.setText(String.format("%s Device(s)", getBlock(RoutineGroupBlock.class).getThingKeys().size()));

        holder.mBaSize.setText(String.format("%s x %s", getBlock().getWidth(), getBlock().getHeight()));

        @ColorInt final int bgClr = getBlock().getBackgroundColourWithAlpha();
        @ColorInt int fgClr = getBlock().getForegroundColour();

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);
    }

    @Override
    public void BindViewHolder(BlockViewHolder viewHolder) {
        if (getBlock().getThing() == null) {
            viewHolder.itemView.setVisibility(View.GONE);
            return;
        }

        final RoutineGroupViewHolder holder = (RoutineGroupViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());
        holder.mTitle.setVisibility(getBlock().isHideTitle() ? View.GONE : View.VISIBLE);

        getBlock().renderForegroundColourTo(holder.mTitle);
        getBlock().renderBackgroundTo(holder.itemView);
        getBlock().renderUnreachableBackground(holder.itemView);
        getBlock(RoutineGroupBlock.class).renderIconTo(holder.mIcon);
        getBlock().startListeningForChanges();

        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.doClickReact(v);
                execute(holder,false);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UIHelper.doLongClickReact(v);
                execute(holder,true);
                return true;
            }
        });

        getBlock().setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(IThing thing) {

            }

            @Override
            public void OnStateChanged(IThing thing) {

            }

            @Override
            public void OnDimmerLevelChanged(IThing thing) {

            }

            @Override
            public void OnSupportColourFlagChanged(IThing thing) {

            }

            @Override
            public void OnSupportColourChanged(IThing thing) {

            }

            @Override
            public void OnDisabledChanged(IThing thing, boolean disabled) {
                getBlock().doEnabled(holder.itemView, !thing.isUnreachable() && !disabled);
            }
        });
    }

    private void execute(final RoutineGroupViewHolder holder,Boolean delay){
        if (getBlock().getThing().isUnreachable() || holder.mProgress.getVisibility() == View.VISIBLE)
            return;

        getBlock().execute(holder.mProgress,delay, new OnProcessCompleteListener<String>() {
            @Override
            public void complete(boolean success, String source) {
                if (!success)
                    Toast.makeText(holder.itemView.getContext(), "Error:" + source, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_routinegroup;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_routinegroup;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_routinegroup;
    }

    public class RoutineGroupEditorHolder extends BlockEditViewHolder {
        final FrameLayout mLayout;
        final TextView mBaName;
        final ImageView mBaImg;
        final TextView mBaDevice;
        final TextView mBaSize;

        RoutineGroupEditorHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.rtg_block_area);
            mBaName = view.findViewById(R.id.rtg_ba_name);
            mBaImg = view.findViewById(R.id.rtg_ba_image);
            mBaDevice = view.findViewById(R.id.rtg_ba_device);
            mBaSize = view.findViewById(R.id.rtg_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class RoutineGroupViewHolder extends BlockViewHolder {
        final LinearLayoutCompat mContainer;
        final TextView mTitle;
        final ImageView mIcon;
        final ProgressBar mProgress;

        RoutineGroupViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.bvrg_container);
            mTitle = itemView.findViewById(R.id.bvrg_title);
            mIcon = itemView.findViewById(R.id.bvrg_icon);
            mProgress = itemView.findViewById(R.id.bvrg_progress);
        }
    }
}
