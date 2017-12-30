package shane.pennihome.local.smartboard.Comms.Interface;

import org.json.JSONObject;

/**
 * Created by shane on 27/12/17.
 */

public interface CommResponseListener {
    void Process(JSONObject obj);
}
