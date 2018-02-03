package shane.pennihome.local.smartboard.services.PhilipsHue;

/**
 * Created by shane on 31/12/17.
 */

@SuppressWarnings("ALL")
public class HueBridge {
    private String mIp;
    private String mId;
    private String mToken;

    public HueBridge() {
    }

    public HueBridge(String id, String ip) {
        this.mIp = ip;
        this.mId = id;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getToken() {
        return mToken;
    }

    private void setToken(String mToken) {
        this.mToken = mToken;
    }
}
