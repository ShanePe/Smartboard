package shane.pennihome.local.smartboard.fragments.interfaces;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import shane.pennihome.local.smartboard.MainActivity;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public abstract class IFragment extends Fragment {
    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }

        super.onResume();
    }

    @SuppressWarnings("SameReturnValue")
    public boolean onBackPressed(MainActivity activity) {
        activity.goHome();
        return true;
    }
}
