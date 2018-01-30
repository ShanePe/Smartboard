package shane.pennihome.local.smartboard.services.interfaces;

import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by SPennicott on 30/01/2018.
 */

public abstract class IThingsGetter {
    public abstract String getLoadMessage();
    public abstract Things getThings() throws Exception;
}
