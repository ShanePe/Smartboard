package shane.pennihome.local.smartboard.services.harmony;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.JsonExecutor;
import shane.pennihome.local.smartboard.comms.JsonExecutorRequest;
import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.NameValuePair;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.ui.LabelTextbox;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;

public class HarmonyHubFragment extends IRegisterServiceFragment {
    private LabelTextbox mTxtIp;
    private LabelTextbox mTxtPort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_harmonyhub, container, false);

        mTxtIp = view.findViewById(R.id.srv_harm_ip);
        mTxtPort = view.findViewById(R.id.srv_harm_port);

        mTxtIp.setAutoTextListener();
        mTxtPort.setAutoTextListener();
        mTxtPort.setText(String.valueOf(getService(HarmonyHubService.class).getPort()));

        view.findViewById(R.id.btn_srv_harm_cnl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnProcessCompleteListener().complete(false, getService());
            }
        });

        view.findViewById(R.id.btn_srv_harm_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mTxtIp.getText()) || TextUtils.isEmpty(mTxtPort.getText())) {
                    Toast.makeText(view.getContext(), "Please supply the IP Address and Port of your Harmony Hub", Toast.LENGTH_SHORT).show();
                    return;
                }

                getService(HarmonyHubService.class).setIp(mTxtIp.getText());
                getService(HarmonyHubService.class).setPort(Integer.parseInt(mTxtPort.getText()));

                view.setVisibility(View.GONE);
                new RemoteGetter(getActivity(), mTxtIp.getText(), String.valueOf(mTxtPort.getText()), new OnProcessCompleteListener<Long>() {
                    @Override
                    public void complete(boolean success, Long source) {
                        if (success)
                            getService(HarmonyHubService.class).setRemoteId(source.toString());
                        getOnProcessCompleteListener().complete(success, getService());
                    }
                }).execute();
            }
        });

        return view;
    }

    private static class RemoteGetter extends AsyncTask<Void, Void, Long> {
        private final WeakReference<Context> mContext;
        ProgressDialog mProgressDialog;
        private final OnProcessCompleteListener<Long> mOnProcessCompleteListener;
        private final String mIp;
        private final String mPort;

        private RemoteGetter(Context context, String ip, String port, OnProcessCompleteListener<Long> onProcessCompleteListener) {
            this.mContext = new WeakReference<>(context);
            this.mIp = ip;
            this.mPort = port;
            mOnProcessCompleteListener = onProcessCompleteListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog();
            mProgressDialog.setMessage("Verifying Harmony Hub");
            mProgressDialog.show(mContext.get());
        }

        @Override
        protected Long doInBackground(Void... voids) {
            try {
                JsonExecutorRequest request = new JsonExecutorRequest(new URL("http://" + mIp + ":" + mPort), JsonExecutorRequest.Types.POST);
                request.getHeaders().add(new NameValuePair("Host", mIp + ":" + mPort));
                request.getHeaders().add(new NameValuePair("Origin", HarmonyHubService.WS_ORIGIN));
                request.getHeaders().add(new NameValuePair("Content-Type", "application/json"));
                request.getHeaders().add(new NameValuePair("Charset", "utf-8"));
                request.setOnExecutorRequestActionListener(new OnExecutorRequestActionListener() {
                    @Override
                    public void OnPreExecute(HttpURLConnection connection) {
                        connection.setReadTimeout(120000);
                        connection.setConnectTimeout(120000);
                    }
                });

                request.setPostJson(new JSONObject("{\"id \": 1,\"cmd\": \"setup.account?getProvisionInfo\",\"params\": {}}"));

                JsonExecutorResult result = JsonExecutor.fulfil(request);
                if (result.isSuccess())
                    return result.getResultAsJsonObject().getJSONObject("data").getLong("activeRemoteId");

            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            mProgressDialog.dismiss();
            if (aLong != null)
                mOnProcessCompleteListener.complete(true, aLong);
            else {
                Toast.makeText(mContext.get(), "Could not verify Harmony hub", Toast.LENGTH_LONG).show();
                mOnProcessCompleteListener.complete(false, null);
            }
        }
    }
}
