package shane.pennihome.local.smartboard.UI;

import android.content.Context;
import android.provider.Contacts;
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

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import shane.pennihome.local.smartboard.Adapters.DashboardBlockAdapter;
import shane.pennihome.local.smartboard.Adapters.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.Interface.IBlock;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.Switch;
import shane.pennihome.local.smartboard.Data.SwitchBlock;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.UI.Interface.IBlockUI;

/**
 * Created by SPennicott on 17/01/2018.
 */

public class SwitchBlockHandler extends IBlockUI {
    public SwitchBlockHandler(IBlock block) {
        super(block);
    }

    @Override
    public void buildBlockPropertyView(final Context context, View view, Things things, final Group group) {
        Spinner spThings = view.findViewById(R.id.sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

        final Button btnBGOff = view.findViewById(R.id.btn_clr_bg_Off);
        final Button btnBGOn = view.findViewById(R.id.btn_clr_bg_On);
        final Button btnFGOff = view.findViewById(R.id.btn_clr_fg_Off);
        final Button btnFGOn = view.findViewById(R.id.btn_clr_fg_On);

        SpinnerThingAdapter aptr = new SpinnerThingAdapter(context);
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
        btnBGOn.setBackgroundColor(getBlock().getBackgroundColourOn());
        btnFGOff.setBackgroundColor(getBlock().getForeColourOff());
        btnFGOn.setBackgroundColor(getBlock().getForeColourOn());

        btnBGOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(context, getBlock().getBackgroundColourOff(), new OnProcessCompleteListener() {
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
                UIHelper.showColourPicker(context, getBlock().getForeColourOn(), new OnProcessCompleteListener() {
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

        btnBGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(context, getBlock().getBackgroundColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getBlock().setBackgroundColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockBackgroundColourOn(clr);
                        btnBGOn.setBackgroundColor(getBlock().getBackgroundColourOn());
                    }
                });
            }
        });

        btnFGOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showColourPicker(context, getBlock().getForeColourOn(), new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        @ColorInt int clr = (int) source;
                        getBlock().setForeColourOn(clr);
                        if (group != null)
                            group.setDefaultBlockForeColourOn(clr);
                        btnFGOn.setBackgroundColor(getBlock().getForeColourOn());
                    }
                });
            }
        });
    }

    @Override
    public void populateBlockFromView(View view, OnBlockSetListener onBlockSetListener) {
        Spinner spThings = view.findViewById(R.id.sp_thing);
        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
        NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

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
        return null;
    }
/*
    public void Test(View view, DashboardBlockAdapter aptr)
    {
        EditorViewHolder holder =(EditorViewHolder)GetEditorViewHolder(view);
        //holder.mItem = mValues.get(position);

        holder.mBaName.setText(holder.mItem.getName());
        if (holder.mItem.getThing().getSource() == IThing.Source.SmartThings) {
            holder.mBaImg.setImageResource(R.drawable.icon_switch);
        } else if (holder.mItem.getThing().getSource() == IThing.Source.PhilipsHue) {
            holder.mBaImg.setImageResource(R.drawable.icon_phlogo);
        }

        holder.mBaDevice.setText(holder.mItem.getThing().getName());
        holder.mBaSize.setText(String.format("%s x %s o:%s", holder.mItem.getWidth(), holder.mItem.getHeight(), holder.mItem.getGroupId()));
        if (holder.mItem.getThing() instanceof Switch)
            holder.mBaType.setText(R.string.lbl_device);
        else if (holder.mItem.getThing() instanceof Routine)
            holder.mBaType.setText(R.string.lbl_routine);

        @ColorInt final int bgClr = UIHelper.getThingColour(holder.mItem.getThing(), holder.mItem.getBackgroundColourOff(), holder.mItem.getBackgroundColourOn());
        @ColorInt int fgClr = UIHelper.getThingColour(holder.mItem.getThing(), holder.mItem.getForeColourOff(), holder.mItem.getForeColourOn());

        holder.mLayout.setBackgroundColor(bgClr);
        holder.mBaName.setTextColor(fgClr);
        holder.mBaDevice.setTextColor(fgClr);
        holder.mBaSize.setTextColor(fgClr);
        holder.mBaType.setTextColor(fgClr);

        final int dragState = holder.getDragStateFlags();

        if (((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                //DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & DashboardBlockAdapter.Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);

            holder.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showBlockPropertyWindow(mSmartboardActivity, mSmartboardActivity.getThings(), holder.mItem, new OnBlockSetListener() {
                        @Override
                        public void OnSet(SwitchBlock switchBlock) {
                            holder.mItem = switchBlock;
                            mGroup.getRowViewHandler().getDashboardBlockAdapter().notifyDataSetChanged();
                            mSmartboardActivity.DataChanged();
                        }
                    });
                }
            });
        }
    }*/

    public class EditorViewHolder extends IBlockUI.BaseEditorViewHolder
    {
        public final View mView;
        public final LinearLayout mLayout;
        public final TextView mBaName;
        public final ImageView mBaImg;
        public final TextView mBaDevice;
        public final TextView mBaSize;
        public final TextView mBaType;
        public IBlock mItem;
        public FrameLayout mContainer;

        public EditorViewHolder(View view) {
            super(view);
            mView = view;
            mContainer = view.findViewById(R.id.cv_dashboard_block);
            mItem = getBlock();
            mLayout = view.findViewById(R.id.block_area);
            mBaName = view.findViewById(R.id.ba_name);
            mBaImg = view.findViewById(R.id.ba_image);
            mBaDevice = view.findViewById(R.id.ba_device);
            mBaSize = view.findViewById(R.id.ba_size);
            mBaType = view.findViewById(R.id.ba_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBaName.getText() + "'";
        }
    }
}
