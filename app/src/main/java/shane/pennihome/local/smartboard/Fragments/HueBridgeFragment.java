package shane.pennihome.local.smartboard.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.Adapters.HueBridgeViewAdapter;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.PhilipsHue.PHBridgeDiscoverer;
import shane.pennihome.local.smartboard.Data.HueBridge;
import shane.pennihome.local.smartboard.Fragments.Interface.IFragment;
import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
@SuppressWarnings("unused")
public class HueBridgeFragment extends IFragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

            PHBridgeDiscoverer discoverer = new PHBridgeDiscoverer(getActivity(), new OnProcessCompleteListener<PHBridgeDiscoverer>() {
                @Override
                public void complete(boolean success, final PHBridgeDiscoverer source) {
                    if (success)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new HueBridgeViewAdapter(source.getBridgeDiscoveryResults(), (OnListFragmentInteractionListener) act));
                            }
                        });
                }
            });
            discoverer.execute();
        }
        return view;
    }

    public List<shane.pennihome.local.smartboard.Data.HueBridge> getHueBridge() {
        return mHueBridge;
    }

    public void setHueBridge(List<shane.pennihome.local.smartboard.Data.HueBridge> hueBridge) {
        mHueBridge = hueBridge;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(HueBridge item);
    }
}
