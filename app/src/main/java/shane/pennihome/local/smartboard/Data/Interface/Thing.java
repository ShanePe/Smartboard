package shane.pennihome.local.smartboard.Data.Interface;

import shane.pennihome.local.smartboard.Comms.Interface.ProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Device;

/**
 * Created by shane on 29/12/17.
 */

public abstract class Thing {
    public enum Source {SmartThings}

    private String _id;
    private String _name;
    private Source _source;

    public Thing.Source getSource() { return _source; }
    public void setSource(Source _source) { this._source = _source; }

    public String getName() {
        return _name;
    }
    public void setName(String _name) {
        this._name = _name;
    }

    public String getId() {
        return _id;
    }
    public void setId(String _id) {
        this._id = _id;
    }


    public abstract void Toggle(final ProcessCompleteListener processComplete);
}
