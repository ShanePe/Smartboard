package shane.pennihome.local.smartboard.blocks.routineblock;

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
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockClickListener;
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.things.Things;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.interfaces.IBlockUI;

/**
 * Created by SPennicott on 17/01/2018.
 */

public class RoutineBlockHandler extends IBlockUI {
    public RoutineBlockHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildBlockPropertyView(final Activity activity, View view, Things things, final Group group) {
        Spinner spThings = view.findViewById(R.id.rt_sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.rt_txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.rt_txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.rt_txt_blk_height);

        final Button btnBGOff = view.findViewById(R.id.rt_btn_clr_bg_Off);
        final Button btnFGOff = view.findViewById(R.id.rt_btn_clr_fg_Off);

        SpinnerThingAdapter aptr = new SpinnerThingAdapter(activity);
        aptr.setThings(things);
        spThings.setAdapter(aptr);

        final int spAtInx = getBlock().getThing() == null ? -1 : things.GetIndex(getBlock().getThing());
        if (spAtInx != -1) {
            spThings.setSelection(spAtInx);
            txtBlkName.setText(getBlock().getName());
        }

        spThings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (getBlock().getThing() == null) {
                    IThing thing = (IThing) parent.getItemAtPosition(position);
                    txtBlkName.setText(thing.getName());
                }
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

        txtWidth.setValue(getBlock().getWidth());
        txtHeight.setValue(getBlock().getHeight());

        btnBGOff.setBackgroundColor(getBlock().getBackgroundColourOff());
        btnFGOff.setBackgroundColor(getBlock().getForeColourOff());

        btnBGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getBlock().getBackgroundColourOff(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getBlock().setBackgroundColourOff(clr);
                        if (group != null) {
                            group.setDefaultBlockBackgroundColourOff(clr);
                        }
                        btnBGOff.setBackgroundColor(getBlock().getBackgroundColourOff());
                    }
                });
            }
        });

        btnFGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(activity, getBlock().getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getBlock().setForeColourOff(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOff(clr);
                        btnFGOff.setBackgroundColor(getBlock().getForeColourOff());

                    }
                });
            }
        });
    }

    @Override
    public void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener) {
        Spinner spThings = view.findViewById(R.id.rt_sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.rt_txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.rt_txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.rt_txt_blk_height);

        getBlock().setThing((IThing) spThings.getSelectedItem());
        getBlock().setName(txtBlkName.getText().toString());
        getBlock().setWidth(txtWidth.getValue());
        getBlock().setHeight(txtHeight.getValue());

        if (onBlockSetListener != null) {
            onBlockSetListener.OnSet(getBlock());
        }
    }

    @Override
    public BaseEditorViewHolder GetEditorViewHolder(View view) {
        return new EditorViewHolder(view);
    }

    public void BindViewHolder(BaseEditorViewHolder viewHolder, int backgroundResourceId, final OnBlockClickListener onBlockClickListener) {
        EditorViewHolder holder = (EditorViewHolder) viewHolder;

        holder.mBaName.setText(getBlock().getName());
        if (getBlock().getThing() != null)
            holder.mBaImg.setImageResource(getBlock().getThing().getDefaultIconResource());

        holder.mBaDevice.setText(getBlock().getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s", getBlock().getWidth(), getBlock().getHeight()));

        @ColorInt final int bgClr = UIHelper.getThingColour(getBlock().getThing(), getBlock().getBackgroundColourOff(), getBlock().getBackgroundColourOn());
        @ColorInt int fgClr = UIHelper.getThingColour(getBlock().getThing(), getBlock().getForeColourOff(), getBlock().getForeColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);

        if (backgroundResourceId != 0)
            holder.mContainer.setBackgroundResource(backgroundResourceId);

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBlockClickListener != null)
                    onBlockClickListener.OnEdit(getBlock());
            }
        });
    }

    public class EditorViewHolder extends BaseEditorViewHolder {
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
            mContainer = view.findViewById(R.id.rt_cv_dashboard_block);
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
}
