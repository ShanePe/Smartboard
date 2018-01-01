package shane.pennihome.local.smartboard.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Comms.SmartThings.STTokenGetter;
import shane.pennihome.local.smartboard.Data.Globals;
import shane.pennihome.local.smartboard.Data.TokenSmartThings;
import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SmartThingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
@SuppressWarnings("unused")
public class SmartThingsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private OnProcessCompleteListener<Activity> mProcessComplete;

    public SmartThingsFragment() {
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

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_smart_things, container, false);
        Log.i(Globals.ACTIVITY, "authorise");

        WebView web = view.findViewById(R.id.st_webview);
        web.getSettings().setJavaScriptEnabled(true);

        // check request
        String requestUrl = (Globals.ST_OAUTH_URL +
                "?redirect_uri=" + Globals.ST_REDIRECT_URI +
                "&response_type=code&client_id=" + Globals.ST_CLIENT_ID +
                "&scope=" + Globals.ST_OAUTH_SCOPE +
                "&redirect_uri=" + Globals.ST_SERVER_URI);

        // Loading of the Smartthing Webside : For authorization
        web.loadUrl(requestUrl);
        web.setWebViewClient(new WebViewClient() {
            final Intent resultIntent = new Intent();
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i(Globals.ACTIVITY, "onPageStarted");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(Globals.ACTIVITY, "onPageFinished");
                super.onPageFinished(view, url);
                // Check if the answer contains a code
                if (url.contains("?code=") && !authComplete) {
                    // check answer

                    Uri uri = Uri.parse(url);

                    // Code recovery
                    TokenSmartThings tokenSmartThingsInfo = new TokenSmartThings();
                    tokenSmartThingsInfo.setAuthCode(uri.getQueryParameter("code"));
                    Log.i(Globals.ACTIVITY, "Auth Code :" + tokenSmartThingsInfo.getAuthCode());

                    authComplete = true;
                    resultIntent.putExtra("code", tokenSmartThingsInfo.getAuthCode());

                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.setResult(Activity.RESULT_CANCELED, resultIntent);

                    tokenSmartThingsInfo.Save();
                    activity.setToMainActivity();

                    // Application by Token
                    STTokenGetter tokenGetter = new STTokenGetter(activity, new OnProcessCompleteListener<STTokenGetter>() {
                        @Override
                        public void complete(boolean success, STTokenGetter source) {
                            if (mProcessComplete != null)
                                mProcessComplete.complete(success, activity);
                        }
                    });
                    tokenGetter.execute();

                } else if (url.contains("error=access_denied")) {
                    authComplete = true;
                    activity.setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(activity, "Error Occured", Toast.LENGTH_SHORT).show();

                    if (mProcessComplete != null)
                        mProcessComplete.complete(false, activity);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setmProcessComplete(OnProcessCompleteListener<Activity> mProcessComplete) {
        this.mProcessComplete = mProcessComplete;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }
}
