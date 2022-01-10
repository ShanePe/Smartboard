package shane.pennihome.local.smartboard.services.PhilipsHue;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.ui.LabelTextbox;
import shane.pennihome.local.smartboard.ui.UIHelper;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;
import shane.pennihome.local.smartboard.ui.listeners.OnDialogWindowListener;

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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void complete(boolean success, ArrayList<HueBridge> source) {
                if (success) {
                    aptr.setItems(source);
                    aptr.notifyDataSetChanged();
                } else {
                    UIHelper.showDialogWindow(getContext(), getString(R.string.lbl_hueman), R.layout.dialog_huebridge_man, new OnDialogWindowListener<HueBridge>() {
                        LabelTextbox txtId;
                        LabelTextbox txtIp;

                        @Override
                        public void onWindowShown(View view) {
                            txtId = view.findViewById(R.id.txt_id_hueman);
                            txtIp = view.findViewById(R.id.txt_ip_hueman);
                            txtIp.getTextbox().setInputType(InputType.TYPE_CLASS_PHONE);

                            txtId.setAutoTextListener();
                            txtIp.setAutoTextListener();
                        }

                        @Override
                        public HueBridge Populate(View view) {
                            return (txtId.getText().isEmpty() || txtIp.getText().isEmpty()) ? null : new HueBridge(txtId.getText(), txtIp.getText());
                        }

                        @Override
                        public void OnComplete(HueBridge data) {
                            ArrayList<HueBridge> bridges = new ArrayList<>();
                            bridges.add(data);
                            aptr.setItems(bridges);
                            aptr.notifyDataSetChanged();
                        }
                    });
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

    private static class DoDiscovery extends AsyncTask<HueBridgeService, Void, ArrayList<HueBridge>> {
        private final WeakReference<Context> mContext;
        private final OnProcessCompleteListener<ArrayList<HueBridge>> mOnProcessCompleteListener;
        ProgressDialog mDialog;

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
                ArrayList<HueBridge> bridges = new ArrayList<>();
                //bridges.add(new HueBridge("001788fffe723ce1","192.168.0.21"));
                return services[0].discover();
            } catch (Exception e) {
                return null;
            }
        }


    }
}
