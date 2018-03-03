package shane.pennihome.local.smartboard.things.time;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.grantland.widget.AutofitTextView;
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
import shane.pennihome.local.smartboard.ui.SizeSelector;
import shane.pennihome.local.smartboard.ui.TemplateProperties;
import shane.pennihome.local.smartboard.ui.ThingPropertiesClrSelector;
import shane.pennihome.local.smartboard.ui.ViewSwiper;

/**
 * Created by shane on 17/02/18.
 */

@SuppressWarnings("ALL")
class TimeUIHandler extends IBlockUIHandler {
    TimeUIHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildEditorWindowView(Activity activity, View view, Things things, Group group) {
        ViewSwiper viewSwiper = view.findViewById(R.id.tm_swiper);
        TabLayout tabLayout = view.findViewById(R.id.tm_tabs);
        viewSwiper.setTabLayout(tabLayout);

        viewSwiper.addView("Properties", R.id.tm_tab_props);
        viewSwiper.addView("Colours", R.id.tm_tab_background);
        viewSwiper.addView("Template", R.id.tm_tab_template);

        final ThingPropertiesClrSelector tpBackground = view.findViewById(R.id.tm_background);
        TemplateProperties tempProps = view.findViewById(R.id.tm_template);
        final SizeSelector sizeSelector = view.findViewById(R.id.tm_size);

        sizeSelector.initialise(getBlock());
        tpBackground.initialise(getBlock());

        tempProps.setTemplates(Templates.Load(view.getContext()).getForType(IThing.Types.Time));
        tempProps.setOnTemplateActionListener(new TemplateProperties.OnTemplateActionListener() {
            @Override
            public void OnTemplateSelected(Template template) {
                sizeSelector.applyTemplate(template);
                tpBackground.applyTemplate(template);
            }
        });
    }

    @Override
    public void buildBlockFromEditorWindowView(View view, OnBlockSetListener onBlockSetListener) {
        try {
            ViewSwiper viewSwiper = view.findViewById(R.id.tm_swiper);

            ThingPropertiesClrSelector tbBackground = (ThingPropertiesClrSelector) viewSwiper.getView(R.id.tm_background);
            TemplateProperties tempProps = (TemplateProperties) viewSwiper.getView(R.id.tm_template);
            SizeSelector sizeSelector = (SizeSelector) viewSwiper.getView(R.id.tm_size);

            Time thing = new Time();
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
        return new TimeEditorHolder(view);
    }

    @Override
    public BlockViewHolder GetViewHolder(View view) {
        return new TimeViewHolder(view);
    }

    @Override
    public void BindEditHolder(BlockEditViewHolder viewHolder, int backgroundResourceId) {
        TimeEditorHolder holder = (TimeEditorHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        holder.mBaImg.setImageResource(getBlock().getDefaultIconResource());

        holder.mBaSize.setText(String.format("%s x %s", getBlock().getWidth(), getBlock().getHeight()));

        @ColorInt final int bgClr = getBlock().getBackgroundColourWithAlpha();
        @ColorInt int fgClr = getBlock().getForegroundColour();

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);
    }

    @Override
    public void BindViewHolder(BlockViewHolder viewHolder) {
        if (getBlock().getThing() == null) {
            viewHolder.itemView.setVisibility(View.GONE);
            return;
        }

        final TimeViewHolder holder = (TimeViewHolder) viewHolder;

        getBlock().renderForegroundColourToTextView(holder.mValue);
        getBlock().renderBackgroundTo(holder.mContainer);
        getBlock().renderUnreachableBackground(holder.itemView);

        if (getBlock().getWidth() == 1)
            holder.mValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        else
            holder.mValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100f);

        holder.mBgImg.post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = holder.mBgImg.getResources().getDrawable(R.mipmap.icon_time_mm_fg, null);
                drawable.setAlpha(50);
                drawable.setColorFilter(getBlock().getForegroundColour(), PorterDuff.Mode.SRC_ATOP);
                holder.mBgImg.setImageDrawable(drawable);
            }
        });

        holder.itemView.setPadding(Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING, Globals.BLOCK_PADDING);

        if (holder.mTimerThread != null) {
            holder.mTimerThread.interrupt();
            if (holder.mTimerThread != null)
                try {
                    holder.mTimerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        final SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.mValue.setText(df.format(Calendar.getInstance().getTime()));

        holder.mTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            int curSec = Calendar.getInstance().get(Calendar.SECOND);
                            int laps = 60 - curSec;
                            Log.i(Globals.ACTIVITY, "Timer Thread Sleeping for " + laps);
                            Thread.sleep(laps * 1000);
                            holder.mValue.post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.mValue.setText(df.format(Calendar.getInstance().getTime()));
                                }
                            });
                        } catch (Exception e) {
                            break;
                        }
                    }
                } finally {
                    holder.mTimerThread = null;
                }
            }
        });
        holder.mTimerThread.start();
    }

    @Override
    public int getEditLayoutID() {
        return R.layout.block_edit_time;
    }

    @Override
    public int getViewLayoutID() {
        return R.layout.block_view_time;
    }

    @Override
    public int getEditorWindowLayoutID() {
        return R.layout.prop_time;
    }

    public class TimeEditorHolder extends BlockEditViewHolder {
        final FrameLayout mLayout;
        final TextView mBaName;
        final ImageView mBaImg;
        final TextView mBaSize;

        TimeEditorHolder(View view) {
            super(view);

            mLayout = view.findViewById(R.id.tm_block_area);
            mBaName = view.findViewById(R.id.tm_ba_name);
            mBaImg = view.findViewById(R.id.tm_ba_image);
            mBaSize = view.findViewById(R.id.tm_ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }

    public class TimeViewHolder extends BlockViewHolder {
        final FrameLayout mContainer;
        final AutofitTextView mValue;
        final ImageView mBgImg;
        Thread mTimerThread;

        TimeViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.bvtm_container);
            mValue = itemView.findViewById(R.id.bvtm_value);
            mBgImg = itemView.findViewById(R.id.bvtm_image);
        }
    }
}
