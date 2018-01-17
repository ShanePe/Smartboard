package shane.pennihome.local.smartboard.things.Routine;

import shane.pennihome.local.smartboard.things.Interface.IThing;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Routine extends IThing {
    public static Routine Load(String json) {
        try {
            return IThing.Load(Routine.class, json);
        } catch (Exception e) {
            return new Routine();
        }
    }

    @Override
    public void successfulToggle(IThing thing) {

    }
}
