package shane.pennihome.local.smartboard.thingsframework.interfaces;

import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by shane on 20/02/18.
 */

public interface IGroup {
    Things getChildThings();

    void setChildThings(Things things);
}
