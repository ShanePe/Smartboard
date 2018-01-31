package shane.pennihome.local.smartboard.services.interfaces;

import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by SPennicott on 30/01/2018.
 */

public interface IThingsGetter {
    String getLoadMessage();
    Things getThings() throws Exception;
    int getUniqueId();
}
