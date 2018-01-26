package shane.pennihome.local.smartboard.fragments.tabs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.SmartboardActivity;
import shane.pennihome.local.smartboard.ui.UIHelper;

import static android.app.Activity.RESULT_OK;

@SuppressWarnings("ALL")
public class SmartboardFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private SmartboardActivity mSmartboardActivity;
    private ImageView mBGImage;

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
            String sFile = UIHelper.handleImageResult(this.getContext(), requestCode, data, mBGImage);
            if (!TextUtils.isEmpty(sFile))
                mSmartboardActivity.getDashboard().setBackgroundImage(sFile);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSmartboardActivity = (SmartboardActivity) getContext();

        View rootView = inflater.inflate(R.layout.tab_smartboard_dash, container, false);
        final EditText editText = rootView.findViewById(R.id.txt_db_name);
        final Button btnBGBrowse = rootView.findViewById(R.id.btn_import_dash_img);
        if(mBGImage == null)
            mBGImage = rootView.findViewById(R.id.img_db_bg);

        if(!TextUtils.isEmpty(mSmartboardActivity.getDashboard().getBackgroundImage()))
        {
            Bitmap bitmap = BitmapFactory.decodeFile(mSmartboardActivity.getDashboard().getBackgroundImage());
            mBGImage.setImageBitmap(bitmap);
        }

        editText.setText(mSmartboardActivity.getDashboard().getName());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mSmartboardActivity.getDashboard().setName(s.toString());
            }
        });

        final Fragment me = this;
        btnBGBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showImageImport(me);
            }
        });
        return rootView;
    }
}
