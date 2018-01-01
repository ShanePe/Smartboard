package shane.pennihome.local.smartboard.Comms;

import org.json.JSONObject;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class RESTCommunicatorResult {
    private JSONObject mResult;
    private Exception mException;
    private boolean mSuccess;

    public JSONObject getResult() {
        return mResult;
    }

    public void setResult(JSONObject result) {
        this.mResult = result;
        this.mSuccess = true;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception _exception) {
        this.mException = _exception;
        this.mSuccess = false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSuccess() {
        return mSuccess;
    }
}
