package shane.pennihome.local.smartboard.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class NameValuePair {
    private String mName;
    private String mValue;

    public NameValuePair(String name, String value) {
        mName = name;
        mValue = value;
    }

    private String getName() {
        return mName;
    }

    @SuppressWarnings("unused")
    public void setName(String _name) {
        this.mName = _name;
    }

    private String getValue() {
        return mValue;
    }

    @SuppressWarnings("unused")
    public void setValue(String _value) {
        this.mValue = _value;
    }

    public String toParam() throws UnsupportedEncodingException {
        return URLEncoder.encode(getName(), "UTF-8") +
                "=" +
                URLEncoder.encode(getValue(), "UTF-8");
    }
}
