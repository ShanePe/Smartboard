package shane.pennihome.local.smartboard.Comms.Interface;

import org.json.JSONObject;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnCommResponseListener {
    void Process(JSONObject obj);
}
