package shane.pennihome.local.smartboard.comms;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import shane.pennihome.local.smartboard.comms.interfaces.IMessage;
import shane.pennihome.local.smartboard.data.Globals;

/**
 * Created by SPennicott on 01/02/2018.
 */

public class Broadcaster {
    private static boolean mPause;

    public static boolean isPause() {
        return mPause;
    }

    public static void setPause(boolean pause) {
        Broadcaster.mPause = pause;
    }

    public static void broadcastMessage(final IMessage message) {
        if(isPause())
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
