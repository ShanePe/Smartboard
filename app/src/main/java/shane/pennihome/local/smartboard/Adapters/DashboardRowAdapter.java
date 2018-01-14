package shane.pennihome.local.smartboard.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import shane.pennihome.local.smartboard.Adapters.Interface.OnDashboardAdapterListener;
import shane.pennihome.local.smartboard.Adapters.Interface.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Row;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

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
       return mDashboard.getRowAt(groupPosition).getBlocks().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDashboard.getRowAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDashboard.getRowAt(groupPosition).getBlockAt(childPosition);
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

    private void RotateImage(View v, final boolean expanded)
    {
        final ImageButton image= (ImageButton) v.findViewById(R.id.btn_add_expanded);

        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(expanded)
                    image.setRotation(180);
                else
                    image.setRotation(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        image.startAnimation(rotate);
    }
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final DashboardRowAdapter me = this;
        final Row row = mDashboard.getRowAt(groupPosition);
        final ExpandableListView listView = (ExpandableListView) parent;

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_row_list, null);
            final View meView = convertView;
            listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    if(!row.isExpanded())
                        me.RotateImage(meView, true);
                    row.setExpanded(true);
                }
            });
            listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    if(row.isExpanded())
                        me.RotateImage(meView, false);
                    row.setExpanded(false);
                }
            });
        }

        ImageButton btnExpand = (ImageButton)convertView.findViewById(R.id.btn_add_expanded);
        TextView txtName = (TextView)convertView.findViewById(R.id.txt_row_name);
        ImageButton btnProps = (ImageButton)convertView.findViewById(R.id.btn_add_prop);
        ImageButton btnAdd = (ImageButton)convertView.findViewById(R.id.btn_add_block);

        txtName.setText(row.getName());
        btnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartboardActivity.showPropertyWindow("Row Properties", R.layout.prop_row, new OnPropertyWindowListener() {
                    @Override
                    public void onWindowShown(View view) {
                        EditText txtName = (EditText)view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch)view.findViewById(R.id.sw_row_dl_dispname);

                        txtName.setText(row.getName());
                        swDispName.setChecked(row.getDisplayName());
                    }

                    @Override
                    public void onOkSelected(View view) {
                        EditText txtName = (EditText)view.findViewById(R.id.txt_row_dl_name);
                        Switch swDispName = (Switch)view.findViewById(R.id.sw_row_dl_dispname);

                        row.setName(txtName.getText().toString());
                        row.setDisplayName(swDispName.isChecked());
                    }
                });
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDashboardListener!=null) {
                    final Block block = createBlockInstance();
                    mSmartboardActivity.showPropertyWindow("Add Block", R.layout.prop_block, new OnPropertyWindowListener() {
                        @Override
                        public void onWindowShown(View view) {
                            EditText txtWidth = (EditText)view.findViewById(R.id.txt_blk_width);
                            EditText txtHeight = (EditText)view.findViewById(R.id.txt_blk_height);

                            Button btnBGOff = (Button)view.findViewById(R.id.btn_clr_bg_Off);
                            Button btnBGOn = (Button)view.findViewById(R.id.btn_clr_bg_On);
                            Button btnFGOff = (Button)view.findViewById(R.id.btn_clr_fg_Off);
                            Button btnFGOn = (Button)view.findViewById(R.id.btn_clr_fg_On);

                            txtWidth.setText(String.valueOf(block.getWidth()));
                            txtHeight.setText(String.valueOf(block.getHeight()));

                            btnBGOff.setBackgroundColor(block.getBackgroundColourOff());
                            btnBGOn.setBackgroundColor(block.getBackgroundColourOn());
                            btnFGOff.setBackgroundColor(block.getForeColourOff());
                            btnFGOn.setBackgroundColor(block.getForeColourOn());

                        }

                        @Override
                        public void onOkSelected(View view) {

                        }
                    });

                    mDashboardListener.AddBlock(row);
                }
            }
        });

        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(row.isExpanded())
                    listView.collapseGroup(groupPosition);
                else
                    listView.expandGroup(groupPosition);
            }
        });
        //listView.expandGroup(groupPosition);

        return convertView;
    }

    private Block createBlockInstance()
    {
        Block block = new Block();
        block.setWidth(1);
        block.setHeight(1);

        block.setBackgroundColourOff(Color.parseColor("#ff5a595b"));
        block.setBackgroundColourOn(Color.parseColor("#FF4081"));

        block.setForeColourOff(Color.parseColor("white"));
        block.setForeColourOn(Color.parseColor("black"));

        return block;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Row row = mDashboard.getRowAt(groupPosition);
        final Block block = row.getBlockAt(childPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_block, null);
        }

        TextView txtName = (TextView)convertView.findViewById(R.id.txt_block_name);
        txtName.setText(block.getThing() == null?"Not Set.":block.getThing().getName());

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
