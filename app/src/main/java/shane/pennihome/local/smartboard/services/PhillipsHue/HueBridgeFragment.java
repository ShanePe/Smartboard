package shane.pennihome.local.smartboard.services.PhillipsHue;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
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
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
@SuppressWarnings("unused")
public class HueBridgeFragment extends IRegisterServiceFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<HueBridge> mHueBridge = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HueBridgeFragment() {
    }

    @SuppressWarnings("unused")
    public static HueBridgeFragment newInstance(int columnCount) {
        HueBridgeFragment fragment = new HueBridgeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

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
        d.setTitle("Please select you Phillips hue bridge");
        return d;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_huebridge_list, container, false);

        final Activity act = getActivity();
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            new DoDiscovery(getActivity(), new OnProcessCompleteListener<ArrayList<HueBridge>>() {
                @Override
                public void complete(boolean success, ArrayList<HueBridge> source) {
                    if(success)
                        recyclerView.setAdapter(new HueBridgeViewAdapter(source, new OnListFragmentInteractionListener() {
                            @Override
                            public void onListFragmentInteraction(HueBridge item) {
                                getService(PhillipsHueService.class).setAddress(item.getIp());
                                dismiss();
                                if (getOnProcessCompleteListener() != null)
                                    getOnProcessCompleteListener().complete(true, getService());

                            }
                        }));
                    else {
                        Toast.makeText(getActivity(), "Could not get Pillip Hue Bridges.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(getService(PhillipsHueService.class));
        }

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

    private static class DoDiscovery extends AsyncTask<PhillipsHueService, Void, ArrayList<HueBridge>>
    {
        ProgressDialog mDialog;
        private WeakReference<Context> mContext;
        private OnProcessCompleteListener<ArrayList<HueBridge>> mOnProcessCompleteListener;
        public DoDiscovery(Context context, OnProcessCompleteListener<ArrayList<HueBridge>> onProcessCompleteListener) {
            this.mContext = new WeakReference<Context>(context);
            this.mOnProcessCompleteListener = onProcessCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(mContext.get());
            mDialog.setMessage("Scanning for Phillips Hue Bridges.");
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<HueBridge> hueBridges) {
            mOnProcessCompleteListener.complete(hueBridges != null, hueBridges);
            mDialog.dismiss();
        }

        @Override
        protected ArrayList<HueBridge> doInBackground(PhillipsHueService... services) {
            try {
                return services[0].Discover();
            } catch (Exception e) {
               return null;
            }
        }
    }

}
