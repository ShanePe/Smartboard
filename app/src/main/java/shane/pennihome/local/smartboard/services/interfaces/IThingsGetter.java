package shane.pennihome.local.smartboard.services.interfaces;

import android.widget.TextView;

import java.lang.reflect.Type;

import shane.pennihome.local.smartboard.comms.JsonExecutorResult;
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
    void setDescriptionTextView(TextView txtDescription);
    Type getThingType();
    JsonExecutorResult execute(IThing thing);
}
