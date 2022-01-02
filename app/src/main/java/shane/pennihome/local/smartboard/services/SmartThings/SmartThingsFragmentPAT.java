package shane.pennihome.local.smartboard.services.SmartThings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.ui.LabelTextbox;

/**
 * Created by shane on 29/01/18.
 */

public class SmartThingsFragmentPAT extends IRegisterServiceFragment {
    private LabelTextbox mPAT;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_things_pat, container, false);
        mPAT = view.findViewById(R.id.st_PAT);
        mPAT.SetAutoTextListener();
        mPAT.setText(getService(SmartThingsServicePAT.class).getPersonalAccessToken());

        view.findViewById(R.id.btn_PAT_Cnl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnProcessCompleteListener().complete(false,getService());
            }
        });

        view.findViewById(R.id.btn_PAT_Ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mPAT.getText()))
                    getOnProcessCompleteListener().complete(false,getService());
                getService(SmartThingsServicePAT.class).setPersonalAccessToken(mPAT.getText());
                getOnProcessCompleteListener().complete(true,getService());
            }
        });

        return view;
    }
}
