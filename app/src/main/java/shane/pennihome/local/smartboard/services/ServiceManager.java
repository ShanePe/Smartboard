package shane.pennihome.local.smartboard.services;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import shane.pennihome.local.smartboard.services.interfaces.IService;

/**
 * Created by shane on 29/01/18.
 */

public class ServiceManager {
    AppCompatActivity mActivity;

    public ServiceManager(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    public <T extends IService> void registerService(Class<T> cls) {
        try {
            T instance = cls.newInstance();
            DialogFragment dialogFragment = instance.getRegisterDialog();
            Bundle args = new Bundle();
            args.putString("title", "Register SmartThingsService");
            dialogFragment.setArguments(args);

            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
            dialogFragment.show(mActivity.getSupportFragmentManager(), "Service_Register");
        } catch (Exception ex) {

            Toast.makeText(mActivity, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
