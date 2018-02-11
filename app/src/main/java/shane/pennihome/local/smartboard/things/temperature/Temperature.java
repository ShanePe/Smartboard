package shane.pennihome.local.smartboard.things.temperature;

import shane.pennihome.local.smartboard.comms.Broadcaster;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.ThingChangedMessage;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;

/**
 * Created by SPennicott on 10/02/2018.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Temperature extends IThing {
    private int mTemperature;

    int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature, boolean fireBroadcast) {
        int pre = mTemperature;

        this.mTemperature = temperature;
        if(pre != mTemperature && fireBroadcast)
           Broadcaster.broadcastMessage(new ThingChangedMessage(getKey(), ThingChangedMessage.What.State));
    }

    public static Temperature Load(String json) {
        try {
            return IThing.Load(Temperature.class, json);
        } catch (Exception e) {
            return new Temperature();
        }
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }

    @Override
    public void verifyState(IThing compare) {
        Temperature newTemp = (Temperature)compare;
        if(getTemperature() != newTemp.getTemperature())
            setTemperature(newTemp.getTemperature(), true);
    }

    @Override
    public Types getThingType() {
        return Types.Temperature;
    }
}