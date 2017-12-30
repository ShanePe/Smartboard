package shane.pennihome.local.smartboard.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shane on 28/12/17.
 */

public class NameValuePair {
    private String mName;
    private String mValue;

    public NameValuePair(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        this.mName = _name;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String _value) {
        this.mValue = _value;
    }

    public String toParam() throws UnsupportedEncodingException {
        String result = URLEncoder.encode(getName(), "UTF-8") +
                "=" +
                URLEncoder.encode(getValue(), "UTF-8");
        return result;
    }
}
