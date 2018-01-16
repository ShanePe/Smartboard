package shane.pennihome.local.smartboard.Fragments.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;

public class SmartboardFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SmartboardActivity mSmartboardActivity;

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

        mSmartboardActivity = (SmartboardActivity) getContext();

        View rootView = inflater.inflate(R.layout.smartboard_tab_dash, container, false);
        final EditText editText = (EditText) rootView.findViewById(R.id.txt_db_name);
        editText.setText(mSmartboardActivity.getDashboard().getName());

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    mSmartboardActivity.getDashboard().setName(editText.getText().toString());
                    mSmartboardActivity.DataChanged();
                }
            }
        });
        return rootView;
    }
}
