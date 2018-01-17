package shane.pennihome.local.smartboard.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.Listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.UI.GroupViewHandler;
import shane.pennihome.local.smartboard.UI.UIHelper;

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

        ImageButton btnExpand = convertView.findViewById(R.id.btn_add_expanded);
        final TextView txtName = convertView.findViewById(R.id.txt_row_name);
        ImageButton btnProps = convertView.findViewById(R.id.btn_add_prop);
        ImageButton btnAdd = convertView.findViewById(R.id.btn_add_block);

        txtName.setText(group.getName());
        btnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showPropertyWindow(mSmartboardActivity, "Group Properties", R.layout.prop_group, new OnPropertyWindowListener() {
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
                UIHelper.showBlockPropertyWindow(mSmartboardActivity, mSmartboardActivity.getThings(), createBlockInstance(group), group, new OnBlockSetListener() {
                    @Override
                    public void OnSet(Block block) {
                        group.getBlocks().add(block);
                        listView.expandGroup(groupPosition);
                        mSmartboardActivity.DataChanged();
                    }
                });
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

        if (group.getBlocks().size() > 0)
            listView.expandGroup(groupPosition);

        return convertView;
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
            group.setRowViewHandler(new GroupViewHandler(mSmartboardActivity, convertView, group));
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
