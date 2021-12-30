package shane.pennihome.local.smartboard.things.temperature;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
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
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesClrSelector;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by SPennicott on 10/02/2018.
 */

public class TemperatureUIHandler extends IBlockUIHandler {
    TemperatureUIHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(Activity activity, View view, Things things, Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.tp_swiper);
        TabLayout tabLayout = view.findViewById(R.id.tp_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.tp_tab_properties);
        viewSwiper.addView("Colours", R.id.tp_tab_background);
        viewSwiper.addView("Template", R.id.tp_tab_template);

        final ThingProperties tpProps = view.findViewById(R.id.tp_properties);
        final ThingPropertiesClrSelector tpBackground = view.findViewById(R.id.tp_background);
        TemplateProperties tempProps = view.findViewById(R.id.tp_template);

        tpProps.initialise(things, getBlock());
        tpBackground.initialise(getBlock());

        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.Temperature));
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
            ViewSwiper viewSwiper = view.findViewById(R.id.tp_swiper);

            ThingProperties tbProps = (ThingProperties)viewSwiper.getView(R.id.tp_properties);
            ThingPropertiesClrSelector tbBackground = (ThingPropertiesClrSelector) viewSwiper.getView(R.id.tp_background);
            TemplateProperties tempProps = (TemplateProperties)viewSwiper.getView(R.id.tp_template);

            tbProps.populate(getBlock(), null);
            tbBackground.populate(getBlock());

            if(tempProps.isSaveAsTemplate())
                tempProps.createTemplate(view.getContext(), getBlock());

            if (onBlockSetListener != null)
                onBlockSetListener.OnSet(getBlock());
        }
        catch (Exception ex)
        {
            Toast.makeText(view.getContext(), "Error : " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public BlockEditViewHolder GetEditHolder(View view) {
        return new TemperatureEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new TemperatureViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        if(getBlock().getThing() == null)
            return;

        TemperatureEditorHolder holder = (TemperatureEditorHolder) viewHolder;

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
        if(getBlock().getThing()==null)
        {
            viewHolder.itemView.setVisibility(View.GONE);
            return;
        }

        final TemperatureViewHolder holder = (TemperatureViewHolder) viewHolder;

        holder.mTitle.setText(getBlock().getName());
        if(getBlock().getThing() != null)
            holder.mValue.setText(String.format("%s°",getBlock().getThing(Temperature.class).getTemperature()));
        getBlock().renderForegroundColourTo(holder.mTitle);
        getBlock().renderForegroundColourTo(holder.mValue);
        getBlock().renderBackgroundTo(holder.mContainer);
        getBlock().renderUnreachableBackground(holder.itemView);

        if (getBlock().getWidth() == 1)
            holder.mValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
        else
            holder.mValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64f);

        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        getBlock().setOnThingActionListener(new OnThingActionListener() {
            @Override
            public void OnReachableStateChanged(IThing thing) {
                getBlock().renderUnreachableBackground(holder.itemView);
            }

            @Override
            public void OnStateChanged(IThing thing) {
                if(getBlock().getThing() != null)
                    holder.mValue.setText(String.format("%s°",getBlock().getThing(Temperature.class).getTemperature()));
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
        });
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_temperature;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_temperature;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_temperature;
    }

    public class TemperatureEditorHolder extends BlockEditViewHolder {
        final FrameLayout mLayout;
        final TextView mBaName;
        final ImageView mBaImg;
        final TextView mBaDevice;
        final TextView mBaSize;

        TemperatureEditorHolder(View view) {
            super(view);

            mLayout = view.findViewById(R.id.tp_block_area);
            mBaName = view.findViewById(R.id.tp_ba_name);
            mBaImg = view.findViewById(R.id.tp_ba_image);
            mBaDevice = view.findViewById(R.id.tp_ba_device);
            mBaSize = view.findViewById(R.id.tp_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class TemperatureViewHolder extends BlockViewHolder {
        final LinearLayoutCompat mContainer;
        final TextView mTitle;
        final AppCompatTextView mValue;

        TemperatureViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.bvt_container);
            mTitle = itemView.findViewById(R.id.bvt_title);
            mValue = itemView.findViewById(R.id.bvt_value);
        }
    }
}
