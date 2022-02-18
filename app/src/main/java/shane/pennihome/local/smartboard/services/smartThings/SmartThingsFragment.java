package shane.pennihome.local.smartboard.services.smartThings;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.interfaces.IRegisterServiceFragment;
import shane.pennihome.local.smartboard.ui.dialogs.ProgressDialog;

/**
 * Created by shane on 29/01/18.
 */

public class SmartThingsFragment extends IRegisterServiceFragment {
    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart_things, container, false);

        WebView web = view.findViewById(R.id.st_webview);
        web.setWebViewClient(new WebViewClient());

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setGeolocationEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);

        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setSupportZoom(false);
        WebView.setWebContentsDebuggingEnabled(true);
        web.setBackgroundColor(Color.TRANSPARENT);

        String requestUrl = (SmartThingsService.ST_OAUTH_URL +
                "?redirect_uri=" + SmartThingsService.ST_REDIRECT_URI +
                "&response_type=code&client_id=" + SmartThingsService.ST_CLIENT_ID +
                "&scope=" + SmartThingsService.ST_OAUTH_SCOPE);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final ProgressDialog dialog = new ProgressDialog();

        dialog.setMessage("Communicating with SmartThings");

        web.setWebViewClient(new WebViewClient() {
            boolean authComplete = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //dialog.show(getContext());
                //view.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    //  dialog.dismiss();
                    super.onPageFinished(view, url);
                    view.setVisibility(View.VISIBLE);
                    if (url.contains("?code=") && !authComplete) {
                        Uri uri = Uri.parse(url);

                        getService(SmartThingsService.class).setAuthorisationCode(uri.getQueryParameter("code"));
                        authComplete = true;
                        if (getOnProcessCompleteListener() != null)
                            getOnProcessCompleteListener().complete(true, getService());

                    } else if (url.contains("error=access_denied"))
                        throw new Exception("Access denied");

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    authComplete = true;
                    if (getOnProcessCompleteListener() != null)
                        getOnProcessCompleteListener().complete(false, getService());
                }
            }
        });
        web.loadUrl(requestUrl);

        return view;
    }
}
