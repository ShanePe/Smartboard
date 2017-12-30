package shane.pennihome.local.smartboard.Comms;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import shane.pennihome.local.smartboard.Comms.Interface.CommResponseListener;
import shane.pennihome.local.smartboard.Data.NameValuePair;

/**
 * Created by shane on 27/12/17.
 */

public class HttpCommunicator {
    public JSONObject postJson(String address, List<NameValuePair> params) throws JSONException, IOException {
        URL url = new URL(address);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "n");
        }

        String json = sb.toString();
        Log.i("JSONStr", json);

        return new JSONObject(json);
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
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

    public JSONObject getJson(String address, String token, CommResponseListener comRes) throws IOException, JSONException {
        Log.d("Url : ", address);
        URL url = new URL(address);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int resCode = conn.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "n");
        }

        String json = sb.toString();
        // Remove the "n" useless sent by the smartapp (Changes according to the received)
        Log.i("JSONStr before", json);
        json = json.replace("n ", "");
        json = json.replace("}n]n", "}]");

        Log.i("JSONStr", json);

        JSONObject jObj = null;
        JSONArray jObjURI = new JSONArray(json);
        for (int i = 0; i < jObjURI.length(); i++) {
            jObj = jObjURI.getJSONObject(i);
            if (comRes != null)
                comRes.Process(jObj);
        }
        // Return JSON String of last index
        return jObj;

    }

    public void putJson(String address, String token, CommResponseListener comRes) throws IOException, JSONException {
        Log.d("Url : ", address);
        URL url = new URL(address);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("PUT");
        conn.setDoInput(true);
        conn.setRequestProperty("Authorization", "Bearer " + token);

        int resCode = conn.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_NO_CONTENT)
            throw new IOException("Message not sent.");
    }
}