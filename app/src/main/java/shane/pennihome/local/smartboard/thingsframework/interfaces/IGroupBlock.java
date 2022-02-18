package shane.pennihome.local.smartboard.thingsframework.interfaces;

import java.util.ArrayList;

/**
 * Created by shane on 20/02/18.
 */

public interface IGroupBlock {
    void loadChildThings();

    ArrayList<String> getThingKeys();

    void setThingKeys(ArrayList<String> thingKeys);
}
