package shane.pennihome.local.smartboard.comms;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import shane.pennihome.local.smartboard.comms.interfaces.OnExecutorRequestActionListener;
import shane.pennihome.local.smartboard.data.NameValuePair;

/**
 * Created by shane on 29/01/18.
 */

public class ExecutorRequest {
    private URL mUrl;
    private OnExecutorRequestActionListener mOnExecutorRequestActionListener;
    private Types mType;
    private ArrayList<NameValuePair> mQueryStringParameters;
    private JSONObject mPostJson;
    private String mPutBody;

    public ExecutorRequest(URL mUrl, Types mType) {
        this.mUrl = mUrl;
        this.mType = mType;
        mQueryStringParameters = new ArrayList<>();
    }

    public ExecutorRequest(URL mUrl, Types mType, OnExecutorRequestActionListener mOnExecutorRequestActionListener) {
        this.mUrl = mUrl;
        this.mOnExecutorRequestActionListener = mOnExecutorRequestActionListener;
        this.mType = mType;
        mQueryStringParameters = new ArrayList<>();
    }

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL url) {
        mUrl = url;
    }

    public OnExecutorRequestActionListener getOnExecutorRequestActionListener() {
        return mOnExecutorRequestActionListener;
    }

    public void setOnExecutorRequestActionListener(OnExecutorRequestActionListener onExecutorRequestActionListener) {
        mOnExecutorRequestActionListener = onExecutorRequestActionListener;
    }

    public Types getType() {
        return mType;
    }

    public ArrayList<NameValuePair> getQueryStringParameters() {
        return mQueryStringParameters;
    }

    public JSONObject getPostJson() {
        return mPostJson;
    }

    public void setPostJson(JSONObject postJson) {
        mPostJson = postJson;
    }

    public String getPutBody() {
        return mPutBody;
    }

    public void setPutBody(String putBody) {
        mPutBody = putBody;
    }

    public enum Types {GET, POST, PUT}
}
