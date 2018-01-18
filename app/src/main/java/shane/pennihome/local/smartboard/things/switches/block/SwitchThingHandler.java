package shane.pennihome.local.smartboard.things.switches.block;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
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

public class SwitchThingHandler extends IThingUIHandler {
    public SwitchThingHandler(IThing iThing) {
        super(iThing);
    }

    @Override
    public void buildBlockPropertyView(final Activity activity, View view, Things things, final Group group) {
        Spinner spThings = view.findViewById(R.id.sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

        final Button btnBGOff = view.findViewById(R.id.btn_clr_bg_Off);
        final Button btnBGOn = view.findViewById(R.id.btn_clr_bg_On);
        final Button btnFGOff = view.findViewById(R.id.btn_clr_fg_Off);
        final Button btnFGOn = view.findViewById(R.id.btn_clr_fg_On);

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
             //   if (getThing() == null) {
                    IThing thing = (IThing) parent.getItemAtPosition(position);
                    txtBlkName.setText(thing.getName());
               // }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtWidth.setMaxValue(4);
        txtWidth.setMinValue(1);
        txtHeight.setMaxValue(4);
        txtHeight.setMinValue(1);
        txtWidth.setWrapSelectorWheel(true);
        txtHeight.setWrapSelectorWheel(true);

        txtWidth.setValue(getThing().getBlock().getWidth());
        txtHeight.setValue(getThing().getBlock().getHeight());

        btnBGOff.setBackgroundColor(getThing().getBlock().getBackgroundColourOff());
        btnBGOn.setBackgroundColor(getThing().getBlock().getBackgroundColourOn());
        btnFGOff.setBackgroundColor(getThing().getBlock().getForeColourOff());
        btnFGOn.setBackgroundColor(getThing().getBlock().getForeColourOn());

        btnBGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock().getBackgroundColourOff(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setBackgroundColourOff(clr);
                        if (group != null) {
                            group.setDefaultBlockBackgroundColourOff(clr);
                        }
                        btnBGOff.setBackgroundColor(getThing().getBlock().getBackgroundColourOff());
                    }
                });
            }
        });

        btnFGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock().getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setForeColourOff(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOff(clr);
                        btnFGOff.setBackgroundColor(getThing().getBlock().getForeColourOff());

                    }
                });
            }
        });

        btnBGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock().getBackgroundColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setBackgroundColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockBackgroundColourOn(clr);
                        btnBGOn.setBackgroundColor(getThing().getBlock().getBackgroundColourOn());
                    }
                });
            }
        });

        btnFGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getThing().getBlock().getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getThing().getBlock().setForeColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOn(clr);
                        btnFGOn.setBackgroundColor(getThing().getBlock().getForeColourOn());
                    }
                });
            }
        });
    }

    @Override
    public void populateBlockFromView(View view, OnThingSetListener onThingSetListener) {
        Spinner spThings = view.findViewById(R.id.sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

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

        @ColorInt final int bgClr = UIHelper.getThingColour(getThing(), getThing().getBlock().getBackgroundColourOff(), getThing().getBlock().getBackgroundColourOn());
        @ColorInt int fgClr = UIHelper.getThingColour(getThing(), getThing().getBlock().getForeColourOff(), getThing().getBlock().getForeColourOn());

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
        public final View mView;
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public FrameLayout mContainer;

        public EditorViewHolder(View view) {
            super(view);
            mView = view;
            mContainer = view.findViewById(R.id.cv_dashboard_block);
            mLayout = view.findViewById(R.id.block_area);
            mBaName = view.findViewById(R.id.ba_name);
            mBaImg = view.findViewById(R.id.ba_image);
            mBaDevice = view.findViewById(R.id.ba_device);
            mBaSize = view.findViewById(R.id.ba_size);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }
}
