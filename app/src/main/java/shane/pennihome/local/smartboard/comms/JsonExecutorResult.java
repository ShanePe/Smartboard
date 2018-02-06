package shane.pennihome.local.smartboard.comms;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by shane on 29/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class JsonExecutorResult {
    private Exception mError;
    private String mResult;
    private JSONObject mJsonResult;

    private String fixFaultyResultString(String fix)
    {
        String result = fix.trim();
        if(result.startsWith("[") && !result.endsWith("]"))
            return result + "]";
        else
            return result;
    }
    public JsonExecutorResult(Exception mError) {
        this.mError = mError;
    }

    JsonExecutorResult(String mResult) throws JSONException {
        this.mResult = fixFaultyResultString(mResult);
        if(!TextUtils.isEmpty(this.mResult))
            mJsonResult = buildJsonResponse(this.mResult);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isSuccess() {
        return mError == null;
    }

    public Exception getError() {
        return mError;
    }

    public String getResult() {
        return mResult;
    }

    public JSONObject getResultAsJsonObject()
    {
        return mJsonResult;
    }

    private JSONObject buildJsonResponse(String data) throws JSONException {
        Object json = new JSONTokener(data).nextValue();
        if (json instanceof JSONObject)
            return (JSONObject) json;
        else if (json instanceof JSONArray) {
            JSONArray array = (JSONArray) json;
            return array.getJSONObject(0);//return the first object
        }

        throw new JSONException("Invalid JSON Response");
    }
}
