package shane.pennihome.local.smartboard.comms;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import info.guardianproject.netcipher.NetCipher;
import shane.pennihome.local.smartboard.data.NameValuePair;

/**
 * Created by shane on 29/01/18.
 */

public class JsonExecutor extends AsyncTask<JsonExecutorRequest, Integer, JsonExecutorResult> {

    @SuppressLint("CustomX509TrustManager")
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(X509Certificate[]
                                                       certs, String authType) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(X509Certificate[]
                                                       certs, String authType) {
                }
            }};

    private static boolean isOnUIThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static JsonExecutorResult fulfil(JsonExecutorRequest request) {
        JsonExecutor jsonExecutor = new JsonExecutor();
        if (isOnUIThread())
            try {
                return jsonExecutor.execute(request).get();
            } catch (Exception e) {
                return new JsonExecutorResult(e);
            }
        else
            return jsonExecutor.executeRequest(request);
    }

    private JsonExecutorResult executeRequest(JsonExecutorRequest request) {
        try {
            int tries = 0;
            int maxAttempts = 5;
            int attemptWait = 5000;

            while (true) {
                try {
                    Log.d("Url : ", (tries + 1) + " of " + maxAttempts + " " + request.getUrl().toString());

                    HttpURLConnection connection;

                    if (request.getUrl().getProtocol().equalsIgnoreCase("https")) {
                        connection = NetCipher.getCompatibleHttpsURLConnection(request.getUrl());
                        final SSLContext sslContext = SSLContext.getInstance("SSL");
                        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                        ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                        ((HttpsURLConnection) connection).setHostnameVerifier(new HostnameVerifier() {
                            @SuppressLint("BadHostnameVerifier")
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                    } else
                        connection = (HttpURLConnection) request.getUrl().openConnection();

                    if(tries==0) {
                        connection.setReadTimeout(15000);
                        connection.setConnectTimeout(15000);
                    }else
                    {
                        connection.setReadTimeout(5000);
                        connection.setConnectTimeout(5000);
                    }

                    for (NameValuePair header : request.getHeaders())
                        connection.setRequestProperty(header.getName(), header.getValue());

                    if (request.getType() == JsonExecutorRequest.Types.GET)
                        return new JsonExecutorResult(doGET(connection, request));
                    else if (request.getType() == JsonExecutorRequest.Types.POST)
                        return new JsonExecutorResult(doPOST(connection, request));
                    else if (request.getType() == JsonExecutorRequest.Types.PUT) {
                        doPUT(connection, request);
                        return new JsonExecutorResult("");
                    } else
                        throw new Exception("Type not supported" + request.getType().toString());
                } catch (Exception ex) {
                    Log.d("Url", "Error on Call: " + ex.getMessage());
                    //noinspection BusyWait
                    Thread.sleep(attemptWait);
                    tries++;
                    if (tries >= maxAttempts)
                        throw ex;
                }
            }
        } catch (Exception ex) {
            return new JsonExecutorResult(ex);
        }
    }

    private void doPUT(HttpURLConnection connection, JsonExecutorRequest request) throws IOException {
        if (request.getPutBody() != null) {
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "close");
        }

        connection.setReadTimeout(15000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("PUT");
        connection.setDoInput(true);

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPreExecute(connection);

        if (request.getPutBody() != null) {
            OutputStream os = connection.getOutputStream();
            os.write((request.getPutBody() + "\r\n").getBytes(StandardCharsets.UTF_8));
            os.close();
        }

        int resCode = connection.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_NO_CONTENT && resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Message not sent.");
    }

    private String doPOST(HttpURLConnection connection, JsonExecutorRequest request) throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Connection", "close");

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPreExecute(connection);

        OutputStream os = connection.getOutputStream();

        if (request.getQueryStringParameters().size() > 0) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(buildQueryString(request.getQueryStringParameters()));
            writer.flush();
            writer.close();
        }

        if (request.getPostJson() != null)
            os.write((request.getPostJson().toString() + "\r\n").getBytes(StandardCharsets.UTF_8));

        os.close();

        if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        return InputStreamToString(connection.getInputStream());
    }

    private String doGET(HttpURLConnection connection, JsonExecutorRequest request) throws IOException {
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPreExecute(connection);

        int resCode = connection.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        return InputStreamToString(connection.getInputStream());
    }

    private String InputStreamToString(InputStream stream) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            char[] cbuf = new char[1024];
            int i;
            while ((i = reader.read(cbuf)) >= 0) {
                sb.append(cbuf, 0, i);
            }

            return sb.toString();
        }finally {
            stream.close();
        }
    }

    private String buildQueryString(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(pair.toParam());
        }

        return result.toString();
    }

    @Override
    protected JsonExecutorResult doInBackground(JsonExecutorRequest... jsonExecutorRequests) {
        return executeRequest(jsonExecutorRequests[0]);
    }
}
