package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Routine extends Thing {
    @Override
    public void successfulToggle(Thing thing) {

    }

    public static Routine Load(String json)
    {
        try {
            return Thing.Load(Routine.class, json);
        } catch (Exception e) {
            return new Routine();
        }
    }
}
