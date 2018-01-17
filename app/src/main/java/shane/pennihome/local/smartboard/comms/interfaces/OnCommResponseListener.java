package shane.pennihome.local.smartboard.comms.interfaces;

import org.json.JSONObject;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnCommResponseListener {
    void process(JSONObject obj);
}
