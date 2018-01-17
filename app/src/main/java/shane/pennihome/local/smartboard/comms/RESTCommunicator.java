package shane.pennihome.local.smartboard.comms;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import info.guardianproject.netcipher.NetCipher;
import shane.pennihome.local.smartboard.comms.interfaces.OnCommResponseListener;
import shane.pennihome.local.smartboard.data.NameValuePair;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings({"DefaultFileTemplate", "unused"})
public class RESTCommunicator {

    public JSONObject postJson(String address, JSONObject jsonObject) throws JSONException, IOException {
        return postJson(address, null, jsonObject);
    }

    @SuppressWarnings("SameParameterValue")
    public JSONObject postJson(String address, List<NameValuePair> queryStringParameters) throws JSONException, IOException {
        return postJson(address, queryStringParameters, null);
    }

    private JSONObject postJson(String address, List<NameValuePair> queryStringParameters, JSONObject jsonObject) throws JSONException, IOException {
        URL url = new URL(address);
        HttpURLConnection conn;

        if (url.getProtocol().toLowerCase().equals("https"))
            conn = NetCipher.getHttpsURLConnection(url);
        else
            conn = (HttpURLConnection) url.openConnection();

        if (jsonObject != null) {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
        }

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Connection", "close");

        OutputStream os = conn.getOutputStream();
        if (queryStringParameters != null) {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(queryStringParameters));
            writer.flush();
            writer.close();

        }
        if (jsonObject != null)
            os.write((jsonObject.toString() + "\r\n").getBytes("UTF-8"));

        os.close();

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("n");
        }

        String json = sb.toString();
        Log.i("JSONStr", json);

        return buildReturnObject(json);
    }

    private JSONObject buildReturnObject(String data) throws JSONException {
        Object json = new JSONTokener(data).nextValue();
        if (json instanceof JSONObject)
            return (JSONObject) json;
        else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            return array.getJSONObject(0);//return the first object
        }

        throw new JSONException("Invalid JSON Response");
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

    public JSONObject getJson(String address, OnCommResponseListener comRes) throws IOException, JSONException, KeyManagementException, NoSuchAlgorithmException {
        return getJson(address, null, comRes);
    }

    public JSONObject getJson(String address, String token, OnCommResponseListener comRes) throws IOException, JSONException {
        Log.d("Url : ", address);
        URL url = new URL(address);

        HttpURLConnection conn;

        if (url.getProtocol().toLowerCase().equals("https"))
            conn = NetCipher.getHttpsURLConnection(url);
        else
            conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        if (token != null)
            conn.setRequestProperty("Authorization", "Bearer " + token);

        int resCode = conn.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Could not connect.");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("n");
        }

        String json = sb.toString();
        // Remove the "n" useless sent by the smartapp (Changes according to the received)
        Log.i("JSONStr before", json);
        json = json.replace("n ", "");
        json = json.replace("}n]n", "}]");

        Log.i("JSONStr", json);

        JSONObject jObj = null;
        Object jsonToken = new JSONTokener(json).nextValue();
        if (jsonToken instanceof JSONObject)
            return (JSONObject) jsonToken;
        else if (jsonToken instanceof JSONArray) {
            JSONArray jObjURI = (JSONArray) jsonToken;
            for (int i = 0; i < jObjURI.length(); i++) {
                jObj = jObjURI.getJSONObject(i);
                if (comRes != null)
                    comRes.process(jObj);
            }
        }

        // Return JSON String of last index
        return jObj;

    }

    public void putJson(String address) throws IOException {
        putJson(address, null, null);
    }

    void putJson(String address, String token) throws IOException {
        putJson(address, token, null);
    }

    void putJson(String address, JSONObject body) throws IOException {
        putJson(address, null, body);
    }

    private void putJson(String address, String token, JSONObject body) throws IOException {
        Log.d("Url : ", address);
        URL url = new URL(address);

        HttpURLConnection conn;

        if (url.getProtocol().toLowerCase().equals("https"))
            conn = NetCipher.getHttpsURLConnection(url);
        else
            conn = (HttpURLConnection) url.openConnection();

        if (body != null) {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "close");
        }

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("PUT");
        conn.setDoInput(true);

        if (token != null)
            conn.setRequestProperty("Authorization", "Bearer " + token);

        if (body != null) {
            OutputStream os = conn.getOutputStream();
            os.write((body.toString() + "\r\n").getBytes("UTF-8"));
            os.close();
        }

        int resCode = conn.getResponseCode();
        if (resCode != HttpsURLConnection.HTTP_NO_CONTENT && resCode != HttpsURLConnection.HTTP_OK)
            throw new IOException("Message not sent.");
    }
}