package shane.pennihome.local.smartboard.Fragments.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

public class SmartboardFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SmartboardActivity mContext;

    public SmartboardFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SmartboardFragment newInstance(int sectionNumber) {
        SmartboardFragment fragment = new SmartboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.smartboard_tab_dash, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.txt_db_name);
        return rootView;
    }
}
