package shane.pennihome.local.smartboard.things.switches.block;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThingUIHandler;

/**
 * Created by SPennicott on 17/01/2018.
 */

@SuppressWarnings("ALL")
public class SwitchThingHandler extends IThingUIHandler {
    public SwitchThingHandler(IThing iThing) {
        super(iThing);
    }

    private void DoTabs(View view)
    {
        final ToggleButton btnTabProp = view.findViewById(R.id.sw_tab1_btn);
        final ToggleButton btnTabBg = view.findViewById(R.id.sw_tab2_btn);

        final View tabProp = view.findViewById(R.id.sw_tab_prop);
        final View tabBg = view.findViewById(R.id.sw_tab_background);

        btnTabProp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    btnTabBg.setChecked(false);
                    tabProp.setVisibility(View.VISIBLE);
                    tabBg.setVisibility(View.GONE);
                }

            }
        });

        btnTabBg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    btnTabProp.setChecked(false);
                    tabProp.setVisibility(View.GONE);
                    tabBg.setVisibility(View.VISIBLE);
                }
            }
        });

        btnTabProp.setChecked(true);
        tabBg.setVisibility(View.GONE);
    }

    @Override
    public void buildBlockPropertyView(final Activity activity, View view, Things things, final Group group) {
        DoTabs(view);

        Spinner spThings = view.findViewById(R.id.sw_sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.sw_txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.sw_txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.sw_txt_blk_height);

        final Button btnBGOff = view.findViewById(R.id.sw_btn_clr_bg_Off);
        final Button btnBGOn = view.findViewById(R.id.sw_btn_clr_bg_On);
        final Button btnFGOff = view.findViewById(R.id.sw_btn_clr_fg_Off);
        final Button btnFGOn = view.findViewById(R.id.sw_btn_clr_fg_On);

        SeekBar sbTransOff = view.findViewById(R.id.sw_transOff);
        SeekBar sbTransOn = view.findViewById(R.id.sw_transOn);

        SpinnerThingAdapter aptr = new SpinnerThingAdapter(activity);
        aptr.setThings(things);
        spThings.setAdapter(aptr);

        final int spAtInx = getThing() == null ? -1 : things.GetIndex(getThing());
        if (spAtInx != -1) {
            spThings.setSelection(spAtInx);
            txtBlkName.setText(getThing().getName());
        }

        spThings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                IThing thing = (IThing) parent.getItemAtPosition(position);
                if(TextUtils.isEmpty(getThing().getName()))
                    getThing().setName(thing.getName());

                if (TextUtils.isEmpty(txtBlkName.getText()) ||
                        getThing().getName().equals(txtBlkName.getText().toString()))
                    txtBlkName.setText(thing.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sbTransOff.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getThing().getBlock().setBackgroundTransparency(progress);
                btnBGOff.setBackgroundColor(getThing().getBlock().getBackgroundColourWithAlpha());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbTransOn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getThing().getBlock(SwitchBlock.class).setBackgroundTransparencyOn(progress);
                btnBGOn.setBackgroundColor(getThing().getBlock(SwitchBlock.class).getBackgroundColourWithAlphaOn());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbTransOff.setProgress((int)getThing().getBlock().getBackgroundTransparency());
        sbTransOn.setProgress((int)getThing().getBlock(SwitchBlock.class).getBackgroundTransparencyOn());

        txtWidth.setMaxValue(4);
        txtWidth.setMinValue(1);
        txtHeight.setMaxValue(4);
        txtHeight.setMinValue(1);
        txtWidth.setWrapSelectorWheel(true);
        txtHeight.setWrapSelectorWheel(true);

        txtWidth.setValue(getThing().getBlock().getWidth());
        txtHeight.setValue(getThing().getBlock().getHeight());

        btnBGOff.setBackgroundColor(getThing().getBlock().getBackgroundColour());
        btnBGOn.setBackgroundColor(getThing().getBlock(SwitchBlock.class).getBackgroundColourOn());
        btnFGOff.setBackgroundColor(getThing().getBlock().getForeColour());
        btnFGOn.setBackgroundColor(getThing().getBlock(SwitchBlock.class).getForeColourOn());

        btnBGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock().getBackgroundColour(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setBackgroundColour(clr);
                        if (group != null) {
                            group.setDefaultBlockBackgroundColourOff(clr);
                        }
                        btnBGOff.setBackgroundColor(getThing().getBlock().getBackgroundColour());
                    }
                });
            }
        });

        btnFGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock(SwitchBlock.class).getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setForeColour(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOff(clr);
                        btnFGOff.setBackgroundColor(getThing().getBlock().getForeColour());

                    }
                });
            }
        });

        btnBGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock(SwitchBlock.class).getBackgroundColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock(SwitchBlock.class).setBackgroundColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockBackgroundColourOn(clr);
                        btnBGOn.setBackgroundColor(getThing().getBlock(SwitchBlock.class).getBackgroundColourOn());
                    }
                });
            }
        });

        btnFGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock(SwitchBlock.class).getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock(SwitchBlock.class).setForeColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOn(clr);
                        btnFGOn.setBackgroundColor(getThing().getBlock(SwitchBlock.class).getForeColourOn());
                    }
                });
            }
        });
    }

    @Override
    public void populateBlockFromView(View view, OnThingSetListener onThingSetListener) {
        Spinner spThings = view.findViewById(R.id.sw_sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.sw_txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.sw_txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.sw_txt_blk_height);

        getThing().copyValuesFrom((IThing)spThings.getSelectedItem());
        getThing().getBlock().setName(txtBlkName.getText().toString());
        getThing().getBlock().setWidth(txtWidth.getValue());
        getThing().getBlock().setHeight(txtHeight.getValue());

        if (onThingSetListener != null) {
            onThingSetListener.OnSet(getThing());
        }
    }

    @Override
    public BaseEditorViewHolder GetEditorViewHolder(View view) {
        return new EditorViewHolder(view);
    }

    public void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId) {
        EditorViewHolder holder = (EditorViewHolder) viewHolder;

        holder.mBaName.setText(getThing().getName());
        if (getThing() != null)
            holder.mBaImg.setImageResource(getThing().getDefaultIconResource());

        holder.mBaDevice.setText(getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s", getThing().getBlock().getWidth(), getThing().getBlock().getHeight()));

        @ColorInt final int bgClr = UIHelper.getThingColour(getThing(), getThing().getBlock().getBackgroundColourWithAlpha(), getThing().getBlock(SwitchBlock.class).getBackgroundColourWithAlphaOn());
        @ColorInt int fgClr = UIHelper.getThingColour(getThing(), getThing().getBlock().getForeColour(), getThing().getBlock(SwitchBlock.class).getForeColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);

        if (backgroundResourceId != 0)
            holder.mContainer.setBackgroundResource(backgroundResourceId);
    }

    @Override
    public int getViewResourceID() {
        return R.layout.dashboard_block_switch;
    }

    @Override
    public int getEditorViewResourceID() {
        return R.layout.prop_block_switch;
    }

    public class EditorViewHolder extends IThingUIHandler.BaseEditorViewHolder {
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public final FrameLayout mContainer;

        public EditorViewHolder(View view) {
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
}
