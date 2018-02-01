package shane.pennihome.local.smartboard.services.interfaces;

import java.lang.reflect.Type;

import shane.pennihome.local.smartboard.thingsframework.Things;

/**
 * Created by SPennicott on 30/01/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface IThingsGetter {
    String getLoadMessage();
    Things getThings() throws Exception;
    int getUniqueId();

    Type getThingType();
}
