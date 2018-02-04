package shane.pennihome.local.smartboard.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.ui.BackgroundSelector;
import shane.pennihome.local.smartboard.ui.LabelTextbox;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.listeners.OnBackgroundActionListener;

@SuppressWarnings("ALL")
public class SmartboardFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SmartboardActivity mSbAct;
    private BackgroundSelector mBGSelector;
//    private Button mBtnBGImg;
//    private SeekBar msbBGImg;

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

        mSbAct = (SmartboardActivity) getContext();

        View rootView = inflater.inflate(R.layout.tab_smartboard_dash, container, false);
        LabelTextbox editText = rootView.findViewById(R.id.txt_db_name);
        mBGSelector = rootView.findViewById(R.id.dash_bgselector);
        //mBGSelector.setImageCallbackFragment(this);
        mBGSelector.setInitialValues(mSbAct.getDashboard().getBackgroundColour(),
                mSbAct.getDashboard().getBackgroundColourTransparency(),
                mSbAct.getDashboard().getBackgroundImage(),
                mSbAct.getDashboard().getBackgroundImageTransparency(),
                mSbAct.getDashboard().getBackgroundImageRenderType());

        mBGSelector.setBackgroundActionListener(new OnBackgroundActionListener() {
            @Override
            public void OnColourSelected(int colour) {
                mSbAct.getDashboard().setBackgroundColour(colour);
            }

            @Override
            public void OnColourTransparencyChanged(int transparent) {
                mSbAct.getDashboard().setBackgroundColourTransparency(transparent);
            }

            @Override
            public void OnImageTransparencyChanged(int transparent) {
                mSbAct.getDashboard().setBackgroundImageTransparency(transparent);
            }

            @Override
            public void OnImageSelected(String imageFile) {
                mSbAct.getDashboard().setBackgroundImage(imageFile);
            }

            @Override
            public void OnImageRenderTypeChanged(UIHelper.ImageRenderTypes imageRenderType) {
                mSbAct.getDashboard().setBackgroundImageRenderType(imageRenderType);
            }
        });

        editText.setText(mSbAct.getDashboard().getName());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSbAct.getDashboard().setName(s.toString());
            }
        });
        return rootView;
    }
}
