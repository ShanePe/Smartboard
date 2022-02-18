package shane.pennihome.local.smartboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Options;
import shane.pennihome.local.smartboard.data.sql.DBEngine;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;
import shane.pennihome.local.smartboard.fragments.listeners.OnOptionsChangedListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

public class OptionsFragment extends IFragment {
    private SwitchCompat mSwKeepOn;
    private SwitchCompat mSwFadeOut;
    private AppCompatEditText mFadeOutIn;
    private Options mOptions;
    private Thread mSaveThread;
    private OnOptionsChangedListener mOnOptionChangeListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        AppCompatButton btnClear = view.findViewById(R.id.btn_set_clr_data);
        mSwKeepOn = view.findViewById(R.id.set_keepOn);
        mSwFadeOut = view.findViewById(R.id.set_fadeOut);
        mFadeOutIn = view.findViewById(R.id.set_fadeOutMin);

        mOptions = Options.getFromDataStore(getContext());
        doOptionChange(false);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showConfirm(getContext(), "Confirm", "Are you sure you want to delete the data store, this will reset smartboard to it's default settings?", new OnProcessCompleteListener() {
                    @Override
                    public void complete(boolean success, Object source) {
                        if (success) {
                            DBEngine db = new DBEngine(getContext());
                            db.cleanDataStore();
                        }
                    }
                });
            }
        });

        mSwKeepOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptions.setKeepScreenOn(mSwKeepOn.isChecked());
                doOptionChange(true);
            }
        });

        mSwFadeOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mOptions.setFadeOut(mSwFadeOut.isChecked());
                doOptionChange(true);
            }
        });

        mFadeOutIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (TextUtils.isEmpty(editable.toString()))
                        return;

                    int iVal = Integer.valueOf(editable.toString());
                    if (iVal < 1)
                        throw new Error("Cannot be smaller than 1");

                    mOptions.setFadeOutInMinutes(iVal);
                    saveOptions();
                } catch (Exception ex) {
                    Toast.makeText(getContext(), "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    mFadeOutIn.setText("5");
                }
            }
        });

        return view;
    }

    private OnOptionsChangedListener getOnOptionChangeListener() {
        return mOnOptionChangeListener;
    }

    public void setOnOptionChangeListener(OnOptionsChangedListener onOptionChangeListener) {
        mOnOptionChangeListener = onOptionChangeListener;
    }

    private void doOptionChange(boolean save) {
        mSwKeepOn.setChecked(mOptions.isKeepScreenOn());
        mSwFadeOut.setChecked(mOptions.isFadeOut());
        mFadeOutIn.setText(String.valueOf(mOptions.getFadeOutInMinutes()));

        mSwFadeOut.setEnabled(mOptions.isKeepScreenOn());
        mFadeOutIn.setEnabled(mOptions.isFadeOut());

        if (save)
            saveOptions();
    }

    private void saveOptions() {
        if (mSaveThread != null) {
            mSaveThread.interrupt();
            if (mSaveThread != null)
                try {
                    mSaveThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        mSaveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    DBEngine db = new DBEngine(getContext());
                    db.writeToDatabase(mOptions);
                    if (getOnOptionChangeListener() != null)
                        getOnOptionChangeListener().OnChange(mOptions);
                    mSaveThread = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mSaveThread = null;
                }
            }
        });

        mSaveThread.start();
    }

}
