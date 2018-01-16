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
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import shane.pennihome.local.smartboard.Adapters.Interface.OnDashboardAdapterListener;
import shane.pennihome.local.smartboard.Adapters.Interface.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Interface.Thing;
import shane.pennihome.local.smartboard.Data.Row;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.UI.RowViewHandler;

/**
 * Created by shane on 13/01/18.
 */

public class DashboardRowAdapter extends BaseExpandableListAdapter {
    private Dashboard mDashboard;
    private SmartboardActivity mSmartboardActivity;
    private OnDashboardAdapterListener mDashboardListener;

    public DashboardRowAdapter(SmartboardActivity smartboardActivity, Dashboard mDashboard, OnDashboardAdapterListener mDashboardListener) {
        mSmartboardActivity = smartboardActivity;
        this.mDashboard = mDashboard;
        this.mDashboardListener = mDashboardListener;
    }

    @Override
    public int getGroupCount() {
        return mDashboard.getRows().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDashboard.getRowAt(groupPosition).getBlocks().size() == 0 ? 0 : 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDashboard.getRowAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDashboard.getRowAt(groupPosition).getBlocks();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mDashboard.getRowAt(groupPosition).getIdAsLong();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mDashboard.getRowAt(groupPosition).getBlockAt(childPosition).getIdAsLong();
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
        final DashboardRowAdapter me = this;
        final Row row = mDashboard.getRowAt(groupPosition);
        final ExpandableListView listView = (ExpandableListView) parent;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_row_list, null);
            final View meView = convertView;
            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if (!row.isExpanded())
                        me.RotateImage(meView, true);
                    row.setExpanded(true);
                }
            });
            listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    if (row.isExpanded())
                        me.RotateImage(meView, false);
                    row.setExpanded(false);
                }
            });
        }

        ImageButton btnExpand = (ImageButton) convertView.findViewById(R.id.btn_add_expanded);
        final TextView txtName = (TextView) convertView.findViewById(R.id.txt_row_name);
        ImageButton btnProps = (ImageButton) convertView.findViewById(R.id.btn_add_prop);
        ImageButton btnAdd = (ImageButton) convertView.findViewById(R.id.btn_add_block);

        txtName.setText(row.getName());
        btnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartboardActivity.showPropertyWindow("Row Properties", R.layout.prop_row, new OnPropertyWindowListener() {
                    @Override
                    public void onWindowShown(View view) {
                        EditText txtName = (EditText) view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch) view.findViewById(R.id.sw_row_dl_dispname);

                        txtName.setText(row.getName());
                        swDispName.setChecked(row.getDisplayName());
                    }

                    @Override
                    public void onOkSelected(View view) {
                        EditText txtName = (EditText) view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch) view.findViewById(R.id.sw_row_dl_dispname);

                        row.setName(txtName.getText().toString());
                        row.setDisplayName(swDispName.isChecked());
                    }
                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDashboardListener != null) {
                    final Block block = createBlockInstance(row);

                    mSmartboardActivity.showPropertyWindow("Add Block", R.layout.prop_block, new OnPropertyWindowListener() {
                        @Override
                        public void onWindowShown(View view) {
                            Spinner spThings = (Spinner) view.findViewById(R.id.sp_thing);
                            final EditText txtBlkName = (EditText) view.findViewById(R.id.txt_blk_name);
                            NumberPicker txtWidth = (NumberPicker) view.findViewById(R.id.txt_blk_width);
                            NumberPicker txtHeight = (NumberPicker) view.findViewById(R.id.txt_blk_height);

                            final Button btnBGOff = (Button) view.findViewById(R.id.btn_clr_bg_Off);
                            final Button btnBGOn = (Button) view.findViewById(R.id.btn_clr_bg_On);
                            final Button btnFGOff = (Button) view.findViewById(R.id.btn_clr_fg_Off);
                            final Button btnFGOn = (Button) view.findViewById(R.id.btn_clr_fg_On);

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
                                            row.setDefaultBlockBackgroundColourOff(clr);
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
                                            row.setDefaultBlockForeColourOff(clr);
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
                                            row.setDefaultBlockBackgroundColourOn(clr);
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
                                            row.setDefaultBlockForeColourOn(clr);
                                            btnFGOn.setBackgroundColor(block.getForeColourOn());
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onOkSelected(View view) {
                            Spinner spThings = (Spinner) view.findViewById(R.id.sp_thing);
                            final EditText txtBlkName = (EditText) view.findViewById(R.id.txt_blk_name);
                            NumberPicker txtWidth = (NumberPicker) view.findViewById(R.id.txt_blk_width);
                            NumberPicker txtHeight = (NumberPicker) view.findViewById(R.id.txt_blk_height);

                            block.setThing((Thing) spThings.getSelectedItem());
                            block.setName(txtBlkName.getText().toString());
                            block.setWidth(txtWidth.getValue());
                            block.setHeight(txtHeight.getValue());

                            row.getBlocks().add(block);
                            listView.expandGroup(groupPosition);
                            mSmartboardActivity.getRowAdapter().notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (row.isExpanded())
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

    private Block createBlockInstance(Row row) {
        Block block = new Block();
        block.setWidth(1);
        block.setHeight(1);

        block.setBackgroundColourOff(row.getDefaultBlockBackgroundColourOff() != 0 ?
                row.getDefaultBlockBackgroundColourOff() :
                Color.parseColor("#ff5a595b"));
        block.setBackgroundColourOn(row.getDefaultBlockBackgroundColourOn() != 0 ?
                row.getDefaultBlockBackgroundColourOn() :
                Color.parseColor("#FF4081"));

        block.setForeColourOff(row.getDefaultBlockForeColourOff() != 0 ?
                row.getDefaultBlockForeColourOff() :
                Color.parseColor("white"));
        block.setForeColourOn(row.getDefaultBlockForeColourOn() != 0 ?
                row.getDefaultBlockForeColourOn() :
                Color.parseColor("black"));

        block.setGroupId(row.getBlocks().size() + 1);
        return block;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Row row = mDashboard.getRowAt(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_block_list, null);
        }

        if (row.getRowViewHandler() == null)
            row.setRowViewHandler(new RowViewHandler(mSmartboardActivity, convertView, row.getBlocks()));
        else {
            row.getRowViewHandler().getDashboardBlockAdapter().setValues(row.getBlocks());
            row.getRowViewHandler().getDashboardBlockAdapter().notifyDataSetChanged();
        }

        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public Dashboard getDashboard() {
        return mDashboard;
    }

}
