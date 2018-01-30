package shane.pennihome.local.smartboard.services.SmartThings;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.sql.DBEngine;

/**
 * Created by shane on 29/01/18.
 */

public class RegisterDialog extends DialogFragment {
    Service mService;

    public Service getService() {
        return mService;
    }

    public void setService(Service service) {
        mService = service;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_things, container, false);

        WebView web = view.findViewById(R.id.st_webview);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setGeolocationEnabled(true);

        String requestUrl = (Service.ST_OAUTH_URL +
                "?redirect_uri=" + Service.ST_REDIRECT_URI +
                "&response_type=code&client_id=" + Service.ST_CLIENT_ID +
                "&scope=" + Service.ST_OAUTH_SCOPE);

        Bundle args = getArguments();
        assert args != null;
        getDialog().setTitle(args.getString("title"));

        web.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    super.onPageFinished(view, url);
                    if (url.contains("?code=") && !authComplete) {
                        Uri uri = Uri.parse(url);

                        mService.setAuthorisationCode(uri.getQueryParameter("code"));
                        mService.register();
                        new DBEngine(getActivity()).writeToDatabase(mService);
                        authComplete = true;
                        getDialog().dismiss();

                    } else if (url.contains("error=access_denied"))
                        throw new Exception("Access denied");
                } catch (Exception ex) {
                    authComplete = true;
                    Toast.makeText(getActivity(), "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });
        web.loadUrl(requestUrl);

        return view;
    }
}
