package shane.pennihome.local.smartboard.Data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shane on 28/12/17.
 */

public class NameValuePair {
    private String _name;
    private String _value;

    public NameValuePair(String name, String value) {
        _name = name;
        _value = value;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String _value) {
        this._value = _value;
    }

    public String toParam() throws UnsupportedEncodingException {
        String result = URLEncoder.encode(getName(), "UTF-8") +
                "=" +
                URLEncoder.encode(getValue(), "UTF-8");
        return result;
    }
}
