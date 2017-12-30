package shane.pennihome.local.smartboard.Comms;

import org.json.JSONObject;

/**
 * Created by shane on 28/12/17.
 */

public class ComResult {
    private JSONObject _result;
    private Exception _exception;
    private boolean _success;

    public JSONObject getResult() {
        return _result;
    }

    public void setResult(JSONObject result) {
        this._result = result;
        this._success = true;
    }

    public Exception getException() {
        return _exception;
    }

    public void setException(Exception _exception) {
        this._exception = _exception;
        this._success = false;
    }

    public boolean isSuccess() {
        return _success;
    }
}
