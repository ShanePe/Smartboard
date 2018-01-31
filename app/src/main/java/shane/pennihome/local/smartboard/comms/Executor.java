package shane.pennihome.local.smartboard.comms;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import shane.pennihome.local.smartboard.data.NameValuePair;

/**
 * Created by shane on 29/01/18.
 */

public class Executor extends AsyncTask<ExecutorRequest, Integer, ExecutorResult> {
    private static boolean isOnUIThread()
    {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static ExecutorResult fulfil(ExecutorRequest request)
    {
        Executor executor = new Executor();
        if(isOnUIThread())
            try {
                return executor.execute(request).get();
            } catch (Exception e) {
                return new ExecutorResult(e);
            }
        else
            return executor.executeRequest(request);
    }

    private ExecutorResult executeRequest(ExecutorRequest request) {
        try {
            Log.d("Url : ", request.getUrl().toString());

            HttpURLConnection connection;

            if (request.getUrl().getProtocol().toLowerCase().equals("https"))
                connection = NetCipher.getHttpsURLConnection(request.getUrl());
            else
                connection = (HttpURLConnection) request.getUrl().openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);

            if (request.getType() == ExecutorRequest.Types.GET)
                return new ExecutorResult(doGET(connection, request));
            else if (request.getType() == ExecutorRequest.Types.POST)
                return new ExecutorResult(doPOST(connection, request));
            else if (request.getType() == ExecutorRequest.Types.PUT)
                return new ExecutorResult(doPUT(connection, request));
            else
                throw new Exception("Type not supported" + request.getType().toString());
        } catch (Exception ex) {
            return new ExecutorResult(ex);
        }
    }

    private String doPUT(HttpURLConnection connection, ExecutorRequest request) throws IOException {
        if (request.getPutBody() != null) {
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setRequestProperty("Connection", "close");
        }

        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("PUT");
        connection.setDoInput(true);

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPresend(connection);

        if (request.getPutBody() != null) {
            OutputStream os = connection.getOutputStream();
            os.write((request.getPutBody() + "\r\n").getBytes("UTF-8"));
            os.close();
        }

        int resCode = connection.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_NO_CONTENT && resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Message not sent.");

        return "";
    }

    private String doPOST(HttpURLConnection connection, ExecutorRequest request) throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Connection", "close");

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPresend(connection);

        OutputStream os = connection.getOutputStream();

        if (request.getQueryStringParameters().size() > 0) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(buildQueryString(request.getQueryStringParameters()));
            writer.flush();
            writer.close();
        }

        if (request.getPostJson() != null)
            os.write((request.getPostJson().toString() + "\r\n").getBytes("UTF-8"));

        os.close();

        if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private String doGET(HttpURLConnection connection, ExecutorRequest request) throws IOException {
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        if (request.getOnExecutorRequestActionListener() != null)
            request.getOnExecutorRequestActionListener().OnPresend(connection);

        int resCode = connection.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
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
    protected ExecutorResult doInBackground(ExecutorRequest... executorRequests) {
        return executeRequest(executorRequests[0]);
    }
}
