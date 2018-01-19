package shane.pennihome.local.smartboard.things.routines;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThingUIHandler;
import shane.pennihome.local.smartboard.things.routines.block.RoutineBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.things.routines.block.RoutineThingHandler;
import shane.pennihome.local.smartboard.thingsframework.Things;

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
        things.sort();
        return things;
    }

    @Override
    public Class getBlockType() {
        return RoutineBlock.class;
    }

    @Override
    public Types getThingType() {
        return Types.Routine;
    }

    @Override
    public IThingUIHandler getUIHandler() {
        return new RoutineThingHandler(this);
    }

    @Override
    public int getDefaultIconResource() {
        return R.drawable.icon_def_routine;
    }

    @Override
    public IDatabaseObject.Types getDatabaseType() {
        return IDatabaseObject.Types.Thing;
    }
}
