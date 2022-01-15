package shane.pennihome.local.smartboard.comms.interfaces;

import java.net.HttpURLConnection;

/**
 * Created by shane on 29/01/18.
 */

public interface OnExecutorRequestActionListener {
    void OnPreExecute(HttpURLConnection connection);
}
