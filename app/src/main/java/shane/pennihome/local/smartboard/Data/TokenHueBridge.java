package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.TokenInfo;

/**
 * Created by shane on 30/12/17.
 */

@SuppressWarnings("ALL")
public class TokenHueBridge extends TokenInfo {
    private String mAddress;
    private String mId;
    private String mToken;

    public String getAddress() {
        if (mAddress == null)
            return "";
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getId() {
        return mId;
    }
   public void setId(String id) {
        this.mId = id;
    }

    public String getToken() {
        if (mToken == null)
            return "";
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    @Override
    protected String getKey() {
        return "hueHubInfo";
    }

    @Override
    public boolean isAuthorised() {
        return (!getAddress().equals("") && !getToken().equals(""));
    }

    @Override
    public boolean isAwaitingAuthorisation() {
        return (!getAddress().equals("") && getToken().equals(""));
    }

    public static TokenHueBridge Load()
    {
        try {
            return TokenInfo.Load(TokenHueBridge.class);
        } catch (Exception e) {
            return new TokenHueBridge();
        }
    }
}
