package shane.pennihome.local.smartboard.fragments.tabs;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.Objects;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 20/01/18.
 */

public class GroupFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    @SuppressWarnings("SameParameterValue")
    public static GroupFragment newInstance(@SuppressWarnings("SameParameterValue") int sectionNumber) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add) {
            final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
            //noinspection rawtypes
            UIHelper.showInput(smartboardActivity, getString(R.string.lbl_add_group_msg), new OnProcessCompleteListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void complete(boolean success, Object source) {
                    smartboardActivity
                            .getDashboard()
                            .getGroups()
                            .add(new Group((String)source));
                    Objects.requireNonNull(smartboardActivity).getGroupAdapter().notifyDataSetChanged();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_smartboard_groups, container, false);
        //ExpandableListView list = rootView.findViewById(R.id.list_rows);

        final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
        //list.setAdapter(smartboardActivity.getGroupAdapter());

        if (rootView instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) rootView;

            RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

            dragMgr.setInitiateOnMove(false);
            dragMgr.setInitiateOnLongPress(true);
            dragMgr.setLongPressTimeout(750);

            recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            assert smartboardActivity != null;
            recyclerView.setAdapter(dragMgr.createWrappedAdapter(smartboardActivity.getGroupAdapter()));

            dragMgr.attachRecyclerView(recyclerView);
        }

        setHasOptionsMenu(true);
        return rootView;
    }
}
