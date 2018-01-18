package shane.pennihome.local.smartboard.fragments.tabs;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.ui.UIHelper;

public class GroupsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public GroupsFragment() {
    }



    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GroupsFragment newInstance(int sectionNumber) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add) {
            final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
            UIHelper.ShowInput(smartboardActivity, getString(R.string.lbl_add_group_msg), new OnProcessCompleteListener() {
                @Override
                public void complete(boolean success, Object source) {
                    smartboardActivity
                            .getDashboard()
                            .getGroups()
                            .add(new Group((String) source));
                    smartboardActivity.getGroupAdapter().notifyDataSetChanged();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_add_frag, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.smartboard_tab_groups, container, false);
        ExpandableListView list = (ExpandableListView) rootView.findViewById(R.id.list_rows);

        final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
        list.setAdapter(smartboardActivity.getGroupAdapter());

        setHasOptionsMenu(true);
        return rootView;
    }
}
