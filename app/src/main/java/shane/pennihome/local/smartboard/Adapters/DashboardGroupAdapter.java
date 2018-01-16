package shane.pennihome.local.smartboard.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import shane.pennihome.local.smartboard.Adapters.Interface.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.UI.GroupViewHandler;

/**
 * Created by shane on 13/01/18.
 */

public class DashboardGroupAdapter extends BaseExpandableListAdapter {
    private SmartboardActivity mSmartboardActivity;

    public DashboardGroupAdapter(SmartboardActivity smartboardActivity) {
        mSmartboardActivity = smartboardActivity;
    }

    @Override
    public int getGroupCount() {
        return mSmartboardActivity.getDashboard().getRows().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSmartboardActivity.getDashboard().getRowAt(groupPosition).getBlocks().size() == 0 ? 0 : 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSmartboardActivity.getDashboard().getRowAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSmartboardActivity.getDashboard().getRowAt(groupPosition).getBlocks();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mSmartboardActivity.getDashboard().getRowAt(groupPosition).getIdAsLong();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mSmartboardActivity.getDashboard().getRowAt(groupPosition).getBlockAt(childPosition).getIdAsLong();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void RotateImage(View v, final boolean expanded) {
        final ImageButton image = (ImageButton) v.findViewById(R.id.btn_add_expanded);

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (expanded)
                    image.setRotation(180);
                else
                    image.setRotation(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        image.startAnimation(rotate);
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final DashboardGroupAdapter me = this;
        final Group group = mSmartboardActivity.getDashboard().getRowAt(groupPosition);
        final ExpandableListView listView = (ExpandableListView) parent;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_group_listitem, null);
            final View meView = convertView;
            final GridLayout container = convertView.findViewById(R.id.group_container);
            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (!group.isExpanded())
                        me.RotateImage(meView, true);
                    group.setExpanded(true);
                    container.setBackground(mSmartboardActivity.getDrawable(R.drawable.expanded_list_expanded));
                }
            });

            listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    if (group.isExpanded())
                        me.RotateImage(meView, false);
                    group.setExpanded(false);
                    container.setBackground(mSmartboardActivity.getDrawable(R.drawable.round_btn));
                }
            });
        }

        ImageButton btnExpand =  convertView.findViewById(R.id.btn_add_expanded);
        final TextView txtName = convertView.findViewById(R.id.txt_row_name);
        ImageButton btnProps = convertView.findViewById(R.id.btn_add_prop);
        ImageButton btnAdd = convertView.findViewById(R.id.btn_add_block);

        txtName.setText(group.getName());
        btnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartboardActivity.showPropertyWindow("Group Properties", R.layout.prop_group, new OnPropertyWindowListener() {
                    @Override
                    public void onWindowShown(View view) {
                        EditText txtName = (EditText) view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch) view.findViewById(R.id.sw_row_dl_dispname);

                        txtName.setText(group.getName());
                        swDispName.setChecked(group.getDisplayName());
                    }

                    @Override
                    public void onOkSelected(View view) {
                        EditText txtName = (EditText) view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch) view.findViewById(R.id.sw_row_dl_dispname);

                        group.setName(txtName.getText().toString());
                        group.setDisplayName(swDispName.isChecked());
                        mSmartboardActivity.DataChanged();
                    }
                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Block block = createBlockInstance(group);

                mSmartboardActivity.showPropertyWindow("Add Block", R.layout.prop_block, new OnPropertyWindowListener() {
                    @Override
                    public void onWindowShown(View view) {
                        Spinner spThings =  view.findViewById(R.id.sp_thing);
                        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
                        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
                        NumberPicker txtHeight =  view.findViewById(R.id.txt_blk_height);

                        final Button btnBGOff = view.findViewById(R.id.btn_clr_bg_Off);
                        final Button btnBGOn = view.findViewById(R.id.btn_clr_bg_On);
                        final Button btnFGOff = view.findViewById(R.id.btn_clr_fg_Off);
                        final Button btnFGOn = view.findViewById(R.id.btn_clr_fg_On);

                        SpinnerThingAdapter aptr = new SpinnerThingAdapter(mSmartboardActivity);
                        aptr.setThings(mSmartboardActivity.getThings());
                        spThings.setAdapter(aptr);

                        spThings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Thing thing = (Thing) parent.getItemAtPosition(position);
                                txtBlkName.setText(thing.getName());
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

                        txtWidth.setValue(block.getWidth());
                        txtHeight.setValue(block.getHeight());

                        btnBGOff.setBackgroundColor(block.getBackgroundColourOff());
                        btnBGOn.setBackgroundColor(block.getBackgroundColourOn());
                        btnFGOff.setBackgroundColor(block.getForeColourOff());
                        btnFGOn.setBackgroundColor(block.getForeColourOn());

                        btnBGOff.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GetColour(block.getBackgroundColourOff(), new OnProcessCompleteListener() {
                                    @Override
                                    public void complete(boolean success, Object source) {
                                        @ColorInt int clr = (int) source;
                                        block.setBackgroundColourOff(clr);
                                        group.setDefaultBlockBackgroundColourOff(clr);
                                        btnBGOff.setBackgroundColor(block.getBackgroundColourOff());
                                    }
                                });
                            }
                        });

                        btnFGOff.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GetColour(block.getForeColourOn(), new OnProcessCompleteListener() {
                                    @Override
                                    public void complete(boolean success, Object source) {
                                        @ColorInt int clr = (int) source;
                                        block.setForeColourOff(clr);
                                        group.setDefaultBlockForeColourOff(clr);
                                        btnFGOff.setBackgroundColor(block.getForeColourOff());

                                    }
                                });
                            }
                        });

                        btnBGOn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GetColour(block.getBackgroundColourOn(), new OnProcessCompleteListener() {
                                    @Override
                                    public void complete(boolean success, Object source) {
                                        @ColorInt int clr = (int) source;
                                        block.setBackgroundColourOn(clr);
                                        group.setDefaultBlockBackgroundColourOn(clr);
                                        btnBGOn.setBackgroundColor(block.getBackgroundColourOn());
                                    }
                                });
                            }
                        });

                        btnFGOn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GetColour(block.getForeColourOn(), new OnProcessCompleteListener() {
                                    @Override
                                    public void complete(boolean success, Object source) {
                                        @ColorInt int clr = (int) source;
                                        block.setForeColourOn(clr);
                                        group.setDefaultBlockForeColourOn(clr);
                                        btnFGOn.setBackgroundColor(block.getForeColourOn());
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onOkSelected(View view) {
                        Spinner spThings = view.findViewById(R.id.sp_thing);
                        final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
                        NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
                        NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

                        block.setThing((Thing) spThings.getSelectedItem());
                        block.setName(txtBlkName.getText().toString());
                        block.setWidth(txtWidth.getValue());
                        block.setHeight(txtHeight.getValue());

                        group.getBlocks().add(block);
                        listView.expandGroup(groupPosition);
                        mSmartboardActivity.DataChanged();
                    }
                });

                if(group.getBlocks().size() > 0)
                    listView.expandGroup(groupPosition);
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (group.isExpanded())
                    listView.collapseGroup(groupPosition);
                else
                    listView.expandGroup(groupPosition);
            }
        });
        //listView.expandGroup(groupPosition);

        return convertView;
    }

    private void GetColour(@ColorInt int colour, final OnProcessCompleteListener onProcessCompleteListener) {
        ColorPickerDialogBuilder
                .with(mSmartboardActivity)
                .setTitle("Choose colour")
                .initialColor(colour)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (onProcessCompleteListener != null)
                            onProcessCompleteListener.complete(true, selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    private Block createBlockInstance(Group group) {
        Block block = new Block();
        block.setWidth(1);
        block.setHeight(1);

        block.setBackgroundColourOff(group.getDefaultBlockBackgroundColourOff() != 0 ?
                group.getDefaultBlockBackgroundColourOff() :
                Color.parseColor("#ff5a595b"));
        block.setBackgroundColourOn(group.getDefaultBlockBackgroundColourOn() != 0 ?
                group.getDefaultBlockBackgroundColourOn() :
                Color.parseColor("#FF4081"));

        block.setForeColourOff(group.getDefaultBlockForeColourOff() != 0 ?
                group.getDefaultBlockForeColourOff() :
                Color.parseColor("white"));
        block.setForeColourOn(group.getDefaultBlockForeColourOn() != 0 ?
                group.getDefaultBlockForeColourOn() :
                Color.parseColor("black"));

        block.setGroupId(group.getBlocks().size() + 1);
        return block;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Group group = mSmartboardActivity.getDashboard().getRowAt(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_block_list, null);
        }

        if (group.getRowViewHandler() == null)
            group.setRowViewHandler(new GroupViewHandler(mSmartboardActivity, convertView, group.getBlocks()));
        else {
            group.getRowViewHandler().getDashboardBlockAdapter().setValues(group.getBlocks());
            mSmartboardActivity.DataChanged();
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
