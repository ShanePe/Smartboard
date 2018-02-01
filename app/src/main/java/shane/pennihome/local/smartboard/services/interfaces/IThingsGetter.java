package shane.pennihome.local.smartboard.services.interfaces;

import java.lang.reflect.Type;

import shane.pennihome.local.smartboard.comms.ExecutorResult;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 30/01/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface IThingsGetter {
    String getLoadMessage();
    Things getThings() throws Exception;
    int getUniqueId();

    Type getThingType();
    ExecutorResult execute(IThing thing);
}
