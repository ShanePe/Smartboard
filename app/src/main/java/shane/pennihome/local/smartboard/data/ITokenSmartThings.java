package shane.pennihome.local.smartboard.data;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import shane.pennihome.local.smartboard.data.interfaces.ITokenInfo;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ITokenSmartThings extends ITokenInfo {
    private String mToken;
    private Date mExpires;
    private String mType;
    private String mAuthCode;
    private String mRequestUrl;

    public static ITokenSmartThings Load() {
        try {
            return ITokenInfo.Load(ITokenSmartThings.class);
        } catch (Exception e) {
            return new ITokenSmartThings();
        }
    }

    public String getToken() {
        if (mToken == null)
            return "";

        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    private Date getExpires() {
        if (mExpires == null) {
            Calendar c = Calendar.getInstance();
            return c.getTime();
        } else
            return mExpires;
    }

    public void setExpires(Date expires) {
        this.mExpires = expires;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public String getRequestUrl() {
        if (mRequestUrl == null)
            return "";
        return mRequestUrl;
    }

    public void setRequestUrl(String _requestUrl) {
        this.mRequestUrl = _requestUrl;
    }

    @Override
    protected String getKey() {
        return "tokenInfo";
    }

    @Override
    public boolean isAuthorised() {
        Calendar c = Calendar.getInstance();
        return (!Objects.equals(getToken(), "") && getExpires().after(c.getTime()) && !getRequestUrl().equals(""));
    }

    @Override
    public boolean isAwaitingAuthorisation() {
        Calendar c = Calendar.getInstance();
        return (!Objects.equals(getToken(), "") && getExpires().after(c.getTime()) && getRequestUrl().equals(""));
    }
}
