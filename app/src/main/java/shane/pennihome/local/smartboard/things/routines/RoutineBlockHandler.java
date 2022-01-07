package shane.pennihome.local.smartboard.things.routines;

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
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IIconBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesClrSelector;
import shane.pennihome.local.smartboard.ui.ThingPropertiesIcon;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by SPennicott on 17/01/2018.
 */

public class RoutineBlockHandler extends IBlockUIHandler {
    public RoutineBlockHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(final Activity activity, View view, Things things, final Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.rt_swiper);
        TabLayout tabLayout = view.findViewById(R.id.rt_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.rt_tab_properties);
        viewSwiper.addView("Colours", R.id.rt_tab_background);
        viewSwiper.addView("Template", R.id.rt_tab_template);

        final ThingPropertiesIcon tpProps = view.findViewById(R.id.rt_properties);
        final ThingPropertiesClrSelector tpBackground = view.findViewById(R.id.rt_background);
        TemplateProperties tempProps = view.findViewById(R.id.rt_template);

        tpProps.initialise(things, (IIconBlock) getBlock());
        tpBackground.initialise(getBlock());

        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.Routine));
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
            ViewSwiper viewSwiper = view.findViewById(R.id.rt_swiper);

            ThingPropertiesIcon tbProps = (ThingPropertiesIcon) viewSwiper.getView(R.id.rt_properties);
            ThingPropertiesClrSelector tbBackground = (ThingPropertiesClrSelector) viewSwiper.getView(R.id.rt_background);
            TemplateProperties tempProps = (TemplateProperties) viewSwiper.getView(R.id.rt_template);

            tbProps.populate((IIconBlock) getBlock(), null);
            tbBackground.populate(getBlock());

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
        return new RoutineEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new RoutineViewHolder(view);
    }

    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if (getBlock().getThing() == null)
            return;

        RoutineEditorHolder holder = (RoutineEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        if (getBlock().getThingKey() != null)
            holder.mBaDevice.setText(getBlock().getThing().getName());
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

        final RoutineViewHolder holder = (RoutineViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());

        getBlock().renderForegroundColourTo(holder.mTitle);
        getBlock().renderBackgroundTo(holder.mContainer);
        getBlock().renderUnreachableBackground(holder.itemView);
        getBlock(RoutineBlock.class).renderIconTo(holder.mIcon);
        getBlock().renderForegroundColourTo(holder.mProgress);
        getBlock().startListeningForChanges();

        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBlock().getThing().isUnreachable() || holder.mProgress.getVisibility() == View.VISIBLE)
                    return;

                getBlock().execute(holder.mProgress, new OnProcessCompleteListener<String>() {
                    @Override
                    public void complete(boolean success, String source) {
                        if (success) {
                            Toast.makeText(holder.itemView.getContext(), "Executed :" + getBlock().getName(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(holder.itemView.getContext(), "Error :" + source, Toast.LENGTH_SHORT).show();
                    }
                });
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
                getBlock().doEnabled(holder.itemView, !disabled);
            }
        });
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_routine;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_routine;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_routine;
    }


    public class RoutineEditorHolder extends BlockEditViewHolder {
        public final FrameLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        //      public final FrameLayout mContainer;

        public RoutineEditorHolder(View view) {
            super(view);
//            mContainer = view.findViewById(R.id.rt_cv_dashboard_block);
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

    public class RoutineViewHolder extends BlockViewHolder {
        LinearLayoutCompat mContainer;
        TextView mTitle;
        ImageView mIcon;
        ProgressBar mProgress;

        public RoutineViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.bvr_container);
            mTitle = itemView.findViewById(R.id.bvr_title);
            mIcon = itemView.findViewById(R.id.bvr_icon);
            mProgress = itemView.findViewById(R.id.bvr_progress);
        }
    }
}
