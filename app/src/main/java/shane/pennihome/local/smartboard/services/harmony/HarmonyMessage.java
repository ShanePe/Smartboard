package shane.pennihome.local.smartboard.services.harmony;

import static shane.pennihome.local.smartboard.services.harmony.HarmonyHubService.WS_ORIGIN;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import tech.gusavila92.websocketclient.WebSocketClient;

public class HarmonyMessage {
    private static final Object mLock = new Object();

    private static String getMessagePayload(int msgId, String remoteId, String command, JSONObject params) throws JSONException {

        JSONObject hub = new JSONObject();
        hub.put("cmd", command);
        hub.put("id", msgId);
        hub.put("params", params);

        JSONObject root = new JSONObject();
        root.put("hubId", remoteId);
        root.put("timeout", 30);
        root.put("hbus", hub);

        return root.toString();

    }

    public static String sendMessage(URI wsURL, int msgId, String remoteId, String command, boolean noResponse) throws Exception {
        JSONObject def = new JSONObject();
        def.put("verb", "get");
        def.put("format", "json");

        return sendMessage(wsURL, msgId, remoteId, command, def, noResponse);
    }

    public static String sendMessage(final URI wsURL, final int msgId, final String remoteId, final String command, final JSONObject params, boolean noResponse) throws Exception {
        final Object threadLock = new Object();

        final String[] response = {""};

        final WebSocketClient client = new WebSocketClient(wsURL) {
            @Override
            public void onOpen() {
                synchronized (threadLock) {
                    threadLock.notify();
                }
            }

            @Override
            public void onTextReceived(String message) {
                synchronized (threadLock) {
                    response[0] = message;
                    threadLock.notify();
                }
            }

            @Override
            public void onBinaryReceived(byte[] data) {

            }

            @Override
            public void onPingReceived(byte[] data) {

            }

            @Override
            public void onPongReceived(byte[] data) {

            }

            @Override
            public void onException(Exception e) {
                synchronized (threadLock) {
                    Log.e("SmartBoard", "Error sending Harmony Message", e);
                    threadLock.notify();
                }
            }

            @Override
            public void onCloseReceived() {

            }
        };
        try {

            synchronized (threadLock) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.setConnectTimeout(10000);
                        client.setReadTimeout(60000);
                        client.addHeader("Origin", WS_ORIGIN);
                        client.enableAutomaticReconnection(5000);
                        client.connect();
                    }
                }).start();

                threadLock.wait(10000);
            }

            synchronized (threadLock) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.i("Url", command);
                            client.send(getMessagePayload(msgId, remoteId, command, params));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                if (!noResponse)
                    threadLock.wait(10000);
                else
                    Thread.sleep(200);
            }

        } finally {
            client.close();
        }

        return response[0];
    }
}
