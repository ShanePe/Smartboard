package shane.pennihome.local.smartboard.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import shane.pennihome.local.smartboard.R;

/**
 * Created by SPennicott on 09/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class TemplateFragment extends Fragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mnu_add_frag, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_dash_add)
        {
            @SuppressWarnings("unused") String test = "TODO";
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }
}

