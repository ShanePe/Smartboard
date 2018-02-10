package shane.pennihome.local.smartboard.things.switches;

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
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.data.Template;
import shane.pennihome.local.smartboard.data.Templates;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlockUIHandler;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.IconSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
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
    public void buildEditorWindowView(final Activity activity, View view, final Things things, final Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.sw_swiper);
        TabLayout tabLayout = view.findViewById(R.id.sw_tabs);

        viewSwiper.setTabLayout(tabLayout);
        viewSwiper.addView("Properties", R.id.sw_tab_properties);
        viewSwiper.addView("Colours", R.id.sw_tab_background);
        viewSwiper.addView("Template", R.id.sw_tab_template);

        final ThingProperties tpProps = view.findViewById(R.id.sw_properties);
        final SwitchPropertiesClrSelector tpBackground = view.findViewById(R.id.sw_background);
        final IconSelector iconSelector = view.findViewById(R.id.sw_icon_selector);
        TemplateProperties tempProps = view.findViewById(R.id.sw_template);

        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.Switch));
        tempProps.setOnTemplateActionListener(new TemplateProperties.OnTemplateActionListener() {
            @Override
            public void OnTemplateSelected(Template template) {
               tpProps.applyTemplate(template);
               tpBackground.applyTemplate(template);
               iconSelector.applyTemplate(template);
            }
        });

        tpProps.initialise(things, getBlock());
        tpBackground.initialise(getBlock(SwitchBlock.class));
        iconSelector.setIconPath(getBlock(SwitchBlock.class).getIcon());
        iconSelector.setIconSize(getBlock(SwitchBlock.class).getIconSize());
    }

    @Override
    public void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener) {
        try {
            ViewSwiper viewSwiper = view.findViewById(R.id.sw_swiper);
            ThingProperties tbProps = (ThingProperties)viewSwiper.getView(R.id.sw_properties);
            SwitchPropertiesClrSelector tbBackground = (SwitchPropertiesClrSelector) viewSwiper.getView(R.id.sw_background);
            IconSelector iconSelector = (IconSelector)viewSwiper.getView(R.id.sw_icon_selector);
            TemplateProperties tempProps = (TemplateProperties)viewSwiper.getView(R.id.sw_template);

            tbProps.populate(getBlock(), null);
            tbBackground.populate(getBlock(SwitchBlock.class));
            getBlock(SwitchBlock.class).setIcon(iconSelector.getIconPath());
            getBlock(SwitchBlock.class).setIconSize(iconSelector.getIconSize());

            if(tempProps.isSaveAsTemplate())
                tempProps.createTemplate(view.getContext(), getBlock());

            if (onBlockSetListener != null)
                onBlockSetListener.OnSet(getBlock());
        } catch (Exception ex) {
            Toast.makeText(view.getContext(), "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
        if (getBlock().getThing() == null)
            return;

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

//        if (backgroundResourceId != 0)
//            holder.mContainer.setBackgroundResource(backgroundResourceId);
    }

    @Override
    public void BindViewHolder(BlockViewHolder viewHolder) {
        if (getBlock().getThing() == null) {
            viewHolder.itemView.setVisibility(View.GONE);
            return;
        }

        final SwitchViewHolder holder = (SwitchViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());

        getBlock().renderForegroundColourToTextView(holder.mTitle);
        getBlock().renderBackgroundTo(holder.itemView);
        getBlock().renderUnreachableBackground(holder.itemView);
        getBlock(SwitchBlock.class).renderIconTo(holder.mIcon);
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
                        if (!success)
                            Toast.makeText(holder.itemView.getContext(), "Error:" + source, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getBlock().setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(boolean isUnReachable) {
                getBlock().renderUnreachableBackground(holder.itemView);
            }

            @Override
            public void OnStateChanged() {
                getBlock().renderForegroundColourToTextView(holder.mTitle);
                getBlock().renderBackgroundTo(holder.itemView);
                getBlock().renderUnreachableBackground(holder.itemView);
            }
        });
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
        public final FrameLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        //      public final FrameLayout mContainer;

        public SwitchEditorHolder(View view) {
            super(view);
//            mContainer = view.findViewById(R.id.sw_dashboard_block);
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
        ImageView mIcon;
        ProgressBar mProgress;

        public SwitchViewHolder(View itemView) {
            super(itemView);

            mContainer = itemView.findViewById(R.id.bvs_container);
            mTitle = itemView.findViewById(R.id.bvs_title);
            mIcon = itemView.findViewById(R.id.bvs_icon);
            mProgress = itemView.findViewById(R.id.bvs_progress);
        }
    }
}
