package shane.pennihome.local.smartboard.services.PhilipsHue;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
@SuppressWarnings("unused")
public class HueBridgeFragment extends IRegisterServiceFragment {

    private List<HueBridge> mHueBridge = new ArrayList<>();
    private HueBridgeViewAdapter aptr;

    public HueBridgeFragment() {
    }

    @SuppressWarnings("unused")
    public static HueBridgeFragment newInstance(int columnCount) {
        HueBridgeFragment fragment = new HueBridgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null)
                actionBar.show();
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setTitle("Please select you Philips hue bridge");
        return d;
    }

    @Override
    public void onStart() {
        super.onStart();
        new DoDiscovery(getContext(), new OnProcessCompleteListener<ArrayList<HueBridge>>() {
            @Override
            public void complete(boolean success, ArrayList<HueBridge> source) {
                if(success) {
                    aptr.setItems(source);
                    aptr.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getActivity(), "Could not get Philips Hue Bridges.", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(getService(HueBridgeService.class));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_huebridge_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        aptr = new HueBridgeViewAdapter(new ArrayList<HueBridge>(), new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(HueBridge item) {
                getService(HueBridgeService.class).setAddress(item.getIp());
                if (getOnProcessCompleteListener() != null)
                    getOnProcessCompleteListener().complete(true, getService());
            }
        });
        recyclerView.setAdapter(aptr);
        return view;
    }

    public List<HueBridge> getHueBridge() {
        return mHueBridge;
    }

    public void setHueBridge(List<HueBridge> hueBridge) {
        mHueBridge = hueBridge;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HueBridge item);
    }

    private static class DoDiscovery extends AsyncTask<HueBridgeService, Void, ArrayList<HueBridge>>
    {
        ProgressDialog mDialog;
        private final WeakReference<Context> mContext;
        private final OnProcessCompleteListener<ArrayList<HueBridge>> mOnProcessCompleteListener;
        DoDiscovery(Context context, OnProcessCompleteListener<ArrayList<HueBridge>> onProcessCompleteListener) {
            this.mContext = new WeakReference<>(context);
            this.mOnProcessCompleteListener = onProcessCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog();
            mDialog.setMessage("Scanning for Philips Hue Bridges.");
            mDialog.show(mContext.get());
        }

        @Override
        protected void onPostExecute(ArrayList<HueBridge> hueBridges) {
            mOnProcessCompleteListener.complete(hueBridges != null, hueBridges);
            mDialog.dismiss();
        }

        @Override
        protected ArrayList<HueBridge> doInBackground(HueBridgeService... services) {
            try {
                return services[0].discover();
            } catch (Exception e) {
               return null;
            }
        }
    }
}
