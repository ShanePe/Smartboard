package shane.pennihome.local.smartboard.things.stmodes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import shane.pennihome.local.smartboard.thingsframework.interfaces.IExecutor;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingActionListener;
import shane.pennihome.local.smartboard.ui.SizeSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesClrSelector;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by shane on 11/02/18.
 */

public class SmartThingModeUIHandler extends IBlockUIHandler {

    SmartThingModeUIHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(Activity activity, View view, Things things, Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.stm_swiper);
        TabLayout tabLayout = view.findViewById(R.id.stm_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.stm_tab_props);
        viewSwiper.addView("Colours", R.id.stm_tab_background);
        viewSwiper.addView("Template", R.id.stm_tab_template);

        final ThingPropertiesClrSelector tpBackground = view.findViewById(R.id.stm_background);
        TemplateProperties tempProps = view.findViewById(R.id.stm_template);
        final SizeSelector sizeSelector = view.findViewById(R.id.stm_size);

        sizeSelector.initialise(getBlock());

        tpBackground.initialise(getBlock());

        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.SmartThingMode));
        tempProps.setOnTemplateActionListener(new TemplateProperties.OnTemplateActionListener() {
            @Override
            public void OnTemplateSelected(Template template) {
                tpBackground.applyTemplate(template);
                sizeSelector.applyTemplate(template);
            }
        });
    }

    @Override
    public void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener) {
        try {
            ViewSwiper viewSwiper = view.findViewById(R.id.stm_swiper);

            ThingPropertiesClrSelector tbBackground = (ThingPropertiesClrSelector) viewSwiper.getView(R.id.stm_background);
            TemplateProperties tempProps = (TemplateProperties) viewSwiper.getView(R.id.stm_template);
            SizeSelector sizeSelector = (SizeSelector) viewSwiper.getView(R.id.stm_size);

            IThings<SmartThingMode> things = Monitor.getMonitor().getThings(SmartThingMode.class);
            if (things.size() == 0)
                throw new Exception("SmartThings mode is not found");

            SmartThingMode thing = things.get(0);
            getBlock().setThingKey(thing.getKey());
            getBlock().setName(thing.getName());
            tbBackground.populate(getBlock());
            sizeSelector.populate(getBlock());

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
        return new SmartThingsModeEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new SmartThingsModeViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if (getBlock().getThing() == null)
            return;

        SmartThingsModeEditorHolder holder = (SmartThingsModeEditorHolder) viewHolder;

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

        final SmartThingsModeViewHolder holder = (SmartThingsModeViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());
        if (getBlock().getThing() != null)
            holder.mValue.setText(getBlock().getThing(SmartThingMode.class).getSelectedText());
        getBlock().renderForegroundColourTo(holder.mTitle);
        getBlock().renderForegroundColourTo(holder.mValue);
        getBlock().renderBackgroundTo(holder.mContainer);
        getBlock().renderUnreachableBackground(holder.itemView);
        getBlock().startListeningForChanges();
        getBlock(SmartThingModeBlock.class).setIconColour(holder.mIcon);
        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        getBlock().setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(IThing thing) {
                getBlock().renderUnreachableBackground(holder.itemView);
            }

            @Override
            public void OnStateChanged(IThing thing) {
                if (getBlock().getThing() != null)
                    holder.mValue.setText(getBlock().getThing(SmartThingMode.class).getSelectedText());
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showModeSelectionWindow(holder.itemView.getContext(), new SmartThingsModeAdapter.OnModeSelectedListener() {
                        @Override
                        public void OnModeSelected(int index, String name) {
                            @SuppressWarnings("unchecked") IExecutor<String> executor = (IExecutor<String>) getBlock().getExecutor();
                            executor.setValue(name);

                            getBlock().execute(holder.mProgress, executor, new OnProcessCompleteListener<String>() {
                                @Override
                                public void complete(boolean success, String source) {

                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showModeSelectionWindow(Context context, final SmartThingsModeAdapter.OnModeSelectedListener onModeSelectedListener) throws Exception {
        final DialogInterface[] dial = new DialogInterface[1];
        SmartThingsModeAdapter adapter = new SmartThingsModeAdapter(new SmartThingsModeAdapter.OnModeSelectedListener() {
            @Override
            public void OnModeSelected(int index, String name) {
                onModeSelectedListener.OnModeSelected(index, name);
                dial[0].dismiss();
            }
        });

        UIHelper.showPropertyWindow(context, "Select SmartThings Mode", R.layout.dialog_smartthings_mode_list,
                false, adapter, 3, null, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dial[0] = dialog;
                    }
                });
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_stmode;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_stmode;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_stmode;
    }

    public class SmartThingsModeEditorHolder extends BlockEditViewHolder {
        final FrameLayout mLayout;
        final TextView mBaName;
        final ImageView mBaImg;
        final TextView mBaDevice;
        final TextView mBaSize;

        SmartThingsModeEditorHolder(View view) {
            super(view);

            mLayout = view.findViewById(R.id.stm_block_area);
            mBaName = view.findViewById(R.id.stm_ba_name);
            mBaImg = view.findViewById(R.id.stm_ba_image);
            mBaDevice = view.findViewById(R.id.stm_ba_device);
            mBaSize = view.findViewById(R.id.stm_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class SmartThingsModeViewHolder extends BlockViewHolder {
        final LinearLayoutCompat mContainer;
        final TextView mTitle;
        final TextView mValue;
        final ProgressBar mProgress;
        final ImageView mIcon;

        SmartThingsModeViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.bvstm_container);
            mTitle = itemView.findViewById(R.id.bvstm_title);
            mValue = itemView.findViewById(R.id.bvstm_value);
            mProgress = itemView.findViewById(R.id.bvstm_progress);
            mIcon = itemView.findViewById(R.id.bvstm_icon);
        }
    }
}
