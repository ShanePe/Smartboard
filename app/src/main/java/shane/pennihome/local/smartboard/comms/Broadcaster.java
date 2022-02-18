package shane.pennihome.local.smartboard.comms;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.data.Globals;

/**
 * Created by SPennicott on 01/02/2018.
 */

public class Broadcaster {
    public static void broadcastMessage(@SuppressWarnings("rawtypes") final IMessage message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("Broadcaster", "I'm sending a message " + message.getValue().toString());
                    Intent intent = new Intent(message.getMessageType());
                    intent.putExtra("message", message.toJson());
                    LocalBroadcastManager.getInstance(Globals.getContext()).sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
