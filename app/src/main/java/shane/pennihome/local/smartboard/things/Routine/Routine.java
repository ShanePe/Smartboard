package shane.pennihome.local.smartboard.things.Routine;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.blocks.routineblock.RoutineBlock;
import shane.pennihome.local.smartboard.things.Adapters.ThingSelectionAdapter;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.Things;

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

    @Override
    public String getFriendlyName() {
        return "Routines";
    }

    @Override
    public Things getFilteredView(Things source) {
        Things things = new Things();
        things.addAll(source.getOfType(Routine.class));
        return things;
    }

    @Override
    public Class getBlockType() {
        return RoutineBlock.class;
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_routine;
    }
}
