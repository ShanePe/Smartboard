package shane.pennihome.local.smartboard.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.zip.Inflater;

import shane.pennihome.local.smartboard.Adapters.Interface.OnDashboardAdapterListener;
import shane.pennihome.local.smartboard.Data.Block;
import shane.pennihome.local.smartboard.Data.Dashboard;
import shane.pennihome.local.smartboard.Data.Row;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 13/01/18.
 */

public class DashboardRowAdapter extends BaseExpandableListAdapter {
    private Dashboard mDashboard;
    private Context mContext;
    private OnDashboardAdapterListener mDashboardListener;

    public DashboardRowAdapter(Context context, Dashboard mDashboard, OnDashboardAdapterListener mDashboardListener) {
        mContext = context;
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Row row = mDashboard.getRowAt(groupPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.dashboard_row_list, null);
        }
        ExpandableListView mExpandableListView = (ExpandableListView) parent;

        TextView txtName = (TextView)convertView.findViewById(R.id.txt_row_name);
            ImageButton btnProps = (ImageButton)convertView.findViewById(R.id.btn_add_prop);
        ImageButton btnAdd = (ImageButton)convertView.findViewById(R.id.btn_add_block);

        txtName.setText(row.getName());
        btnProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Row Properties");

                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = (View)inflater.inflate(R.layout.dashboard_row, null);
                final EditText txtName = (EditText)view.findViewById(R.id.txt_row_dl_name);
                final Switch swDispName = (Switch)view.findViewById(R.id.sw_row_dl_dispname);

                txtName.setText(row.getName());
                swDispName.setChecked(row.getDisplayName());

                builder.setView(view);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        row.setName(txtName.getText().toString());
                        row.setDisplayName(swDispName.isChecked());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDashboardListener!=null)
                    mDashboardListener.AddBlock(row);
            }
        });

        mExpandableListView.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Row row = mDashboard.getRowAt(groupPosition);
        final Block block = row.getBlockAt(childPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
