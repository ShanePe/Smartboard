package shane.pennihome.local.smartboard.fragments.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBackgroundActionListener;
import shane.pennihome.local.smartboard.ui.BackgroundSelector;
import shane.pennihome.local.smartboard.ui.LabelTextbox;
import shane.pennihome.local.smartboard.ui.UIHelper;

import static android.app.Activity.RESULT_OK;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String sFile = UIHelper.handleImageResult(this.getContext(), requestCode, data,
                    null, mSbAct.getDashboard().getBackgroundImageTransparency());

            if (!TextUtils.isEmpty(sFile)) {
                mSbAct.getDashboard().setBackgroundImage(sFile);
                mSbAct.getDashboard().setBackgroundImageTransparency(100);
                mBGSelector.setImageValues(sFile, 100);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSbAct = (SmartboardActivity) getContext();

        View rootView = inflater.inflate(R.layout.tab_smartboard_dash, container, false);
        LabelTextbox editText = rootView.findViewById(R.id.txt_db_name);
        mBGSelector = rootView.findViewById(R.id.dash_bgselector);
        mBGSelector.setImageCallbackFragment(this);
        mBGSelector.setInitialValues(mSbAct.getDashboard().getBackgroundColour(),
                mSbAct.getDashboard().getBackgroundColourTransparency(),
                mSbAct.getDashboard().getBackgroundImage(),
                mSbAct.getDashboard().getBackgroundImageTransparency());

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
        });
//        final Button btnBGClr = rootView.findViewById(R.id.dash_btn_bg);
//        mBtnBGImg = rootView.findViewById(R.id.dash_btn_img);
//        final SeekBar sbBGClr = rootView.findViewById(R.id.dash_sb_bg);
//        msbBGImg = rootView.findViewById(R.id.dash_sb_img);

        editText.setText(mSbAct.getDashboard().getName());
//
//        btnBGClr.setBackgroundColor(mSbAct.getDashboard().getBackgroundColourWithAlpha());
//
//        if(!TextUtils.isEmpty(mSbAct.getDashboard().getBackgroundImage()))
//            mBtnBGImg.setBackground(mSbAct.getDashboard().getBackgroundImageWithAlpha(mSbAct));
//
//        sbBGClr.setProgress(mSbAct.getDashboard().getBackgroundColourTransparency());
//        msbBGImg.setProgress(mSbAct.getDashboard().getBackgroundImageTransparency());
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                mSbAct.getDashboard().setName(s.toString());
//            }
//        });
//
//        btnBGClr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UIHelper.showColourPicker(mSbAct, mSbAct.getDashboard().getBackgroundColour(), new OnProcessCompleteListener() {
//                    @Override
//                    public void complete(boolean success, Object source) {
//                        @ColorInt int clr = (int) source;
//                        mSbAct.getDashboard().setBackgroundColour(clr);
//                        mSbAct.getDashboard().setBackgroundColourTransparency(100);
//                        sbBGClr.setProgress(100);
//                        btnBGClr.setBackgroundColor(mSbAct.getDashboard().getBackgroundColourWithAlpha());
//                        doBGImage();
//                    }
//                });
//            }
//        });
//
//        sbBGClr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                mSbAct.getDashboard().setBackgroundColourTransparency(i);
//                btnBGClr.setBackgroundColor(mSbAct.getDashboard().getBackgroundColourWithAlpha());
//                doBGImage();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//
//        msbBGImg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                mSbAct.getDashboard().setBackgroundImageTransparency(i);
//                doBGImage();
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//
//        final Fragment me = this;
//        mBtnBGImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                UIHelper.showImageImport(me);
//            }
//        });
        return rootView;
    }
}
