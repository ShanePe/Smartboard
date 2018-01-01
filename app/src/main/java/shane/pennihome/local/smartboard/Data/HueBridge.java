package shane.pennihome.local.smartboard.Data;

/**
 * Created by shane on 31/12/17.
 */

@SuppressWarnings("ALL")
public class HueBridge {
    private String mIp;
    private String mId;
    private String mToken;

    public static HueBridge FromTokenInfo(TokenHueBridge token) {
        HueBridge b = new HueBridge();
        b.setId(token.getId());
        b.setIp(token.getAddress());
        b.setToken(token.getToken());
        return b;
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
