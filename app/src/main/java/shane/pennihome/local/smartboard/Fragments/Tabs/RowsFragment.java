package shane.pennihome.local.smartboard.Fragments.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Row;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

public class RowsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public RowsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RowsFragment newInstance(int sectionNumber) {
        RowsFragment fragment = new RowsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add) {
            final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
            smartboardActivity.ShowInput("Please supply a row name.", new OnProcessCompleteListener() {
                @Override
                public void complete(boolean success, Object source) {
                    smartboardActivity.getRowAdapter()
                            .getDashboard()
                            .getRows()
                            .add(new Row((String) source));
                    smartboardActivity.getRowAdapter().notifyDataSetChanged();
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
        View rootView = inflater.inflate(R.layout.smartboard_tab_rows, container, false);
        ExpandableListView list = (ExpandableListView) rootView.findViewById(R.id.list_rows);

        final SmartboardActivity smartboardActivity = (SmartboardActivity) getContext();
        list.setAdapter(smartboardActivity.getRowAdapter());

        setHasOptionsMenu(true);
        return rootView;
    }
}
