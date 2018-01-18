package shane.pennihome.local.smartboard.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.listeners.OnThingSelectListener;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.ui.GroupViewHandler;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 13/01/18.
 */

public class EditGroupAdapter extends BaseExpandableListAdapter {
    private SmartboardActivity mSmartboardActivity;

    public EditGroupAdapter(SmartboardActivity smartboardActivity) {
        mSmartboardActivity = smartboardActivity;
    }

    @Override
    public int getGroupCount() {
        return mSmartboardActivity.getDashboard().getGroups().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mSmartboardActivity.getDashboard().getGroupAt(groupPosition).getThings().size() == 0 ? 0 : 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSmartboardActivity.getDashboard().getGroupAt(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mSmartboardActivity.getDashboard().getGroupAt(groupPosition).getThings();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mSmartboardActivity.getDashboard().getGroupAt(groupPosition).getIdAsLong();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mSmartboardActivity.getDashboard().getGroupAt(groupPosition).getThingsAt(childPosition).getIdAsLong();
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
        final EditGroupAdapter me = this;
        final Group group = mSmartboardActivity.getDashboard().getGroupAt(groupPosition);
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
        ImageButton btnDelete = convertView.findViewById(R.id.btn_delete_item);

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
            UIHelper.ShowThingsSelectionWindow(mSmartboardActivity, new OnThingSelectListener() {
                @Override
                public void ThingSelected(IThing thing) {
                    createBlockInstance(thing, group);
                    UIHelper.showThingPropertyWindow(mSmartboardActivity, thing.getFilteredView(mSmartboardActivity.getThings()),
                            thing, group, new OnThingSetListener() {
                        @Override
                        public void OnSet(IThing thing) {
                                group.getThings().add(thing);
                                listView.expandGroup(groupPosition);
                                mSmartboardActivity.DataChanged();
                        }
                    });
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

        if (group.getThings().size() > 0)
            listView.expandGroup(groupPosition);

        return convertView;
    }

    private void createBlockInstance(IThing thing,Group group) {
        try {
            thing.CreateBlock();
            thing.getBlock().setWidth(1);
            thing.getBlock().setHeight(1);

            thing.getBlock().setBackgroundColourOff(group.getDefaultBlockBackgroundColourOff() != 0 ?
                    group.getDefaultBlockBackgroundColourOff() :
                    Color.parseColor("#ff5a595b"));
            thing.getBlock().setBackgroundColourOn(group.getDefaultBlockBackgroundColourOn() != 0 ?
                    group.getDefaultBlockBackgroundColourOn() :
                    Color.parseColor("#FF4081"));

            thing.getBlock().setForeColourOff(group.getDefaultBlockForeColourOff() != 0 ?
                    group.getDefaultBlockForeColourOff() :
                    Color.parseColor("white"));
            thing.getBlock().setForeColourOn(group.getDefaultBlockForeColourOn() != 0 ?
                    group.getDefaultBlockForeColourOn() :
                    Color.parseColor("black"));

            thing.setGroupId(group.getThings().size() + 1);

        }
        catch (Exception ex)
        {
            Toast.makeText(mSmartboardActivity, "Error creating block instance", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Group group = mSmartboardActivity.getDashboard().getGroupAt(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mSmartboardActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_block_list, null);
        }

        if (group.getGroupViewHandler() == null)
            group.setGroupViewHandler(new GroupViewHandler(mSmartboardActivity, parent, convertView, group));
        else {
            group.getGroupViewHandler().getDashboardBlockAdapter().setThings(group.getThings());
            mSmartboardActivity.DataChanged();
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
