package shane.pennihome.local.smartboard.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import shane.pennihome.local.smartboard.MainActivity;
import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.comms.smartthings.STTokenGetter;
import shane.pennihome.local.smartboard.data.Globals;
import shane.pennihome.local.smartboard.data.ITokenSmartThings;
import shane.pennihome.local.smartboard.fragments.interfaces.IFragment;

@SuppressWarnings("unused")
public class SmartThingsFragment extends IFragment {
    private OnProcessCompleteListener<AppCompatActivity> mProcessComplete;

    public SmartThingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setGeolocationEnabled(true);

        // check request
        String requestUrl = (Globals.ST_OAUTH_URL +
                "?redirect_uri=" + Globals.ST_REDIRECT_URI +
                "&response_type=code&client_id=" + Globals.ST_CLIENT_ID +
                "&scope=" + Globals.ST_OAUTH_SCOPE +
                "&redirect_uri=" + Globals.ST_SERVER_URI);

        // Loading of the Smartthing Webside : For authorization
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
                    ITokenSmartThings tokenSmartThingsInfo = new ITokenSmartThings();
                    tokenSmartThingsInfo.setAuthCode(uri.getQueryParameter("code"));
                    Log.i(Globals.ACTIVITY, "Auth Code :" + tokenSmartThingsInfo.getAuthCode());

                    authComplete = true;
                    resultIntent.putExtra("code", tokenSmartThingsInfo.getAuthCode());

                    activity.setResult(Activity.RESULT_OK, resultIntent);
                    activity.setResult(Activity.RESULT_CANCELED, resultIntent);

                    tokenSmartThingsInfo.Save();
                    activity.backToMainActivity();

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
        web.loadUrl(requestUrl);

        return view;
    }

    public void setmProcessComplete(OnProcessCompleteListener<AppCompatActivity> mProcessComplete) {
        this.mProcessComplete = mProcessComplete;
    }
}
