package shane.pennihome.local.smartboard.thingsframework;

import java.util.ArrayList;
import java.util.Collections;

import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.routines.RoutineBlock;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingMode;
import shane.pennihome.local.smartboard.things.stmodes.SmartThingModeBlock;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.things.switches.SwitchBlock;
import shane.pennihome.local.smartboard.things.temperature.Temperature;
import shane.pennihome.local.smartboard.things.temperature.TemperatureBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;

/**
 * Created by shane on 01/02/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Blocks extends ArrayList<IBlock> {
    public static Blocks getAvailableTypes() {
        Blocks blocks = new Blocks();
        if (Monitor.getMonitor().getThings().containsType(Switch.class))
            blocks.add(new SwitchBlock());
        if (Monitor.getMonitor().getThings().containsType(Routine.class))
            blocks.add(new RoutineBlock());
        if(Monitor.getMonitor().getThings().containsType(Temperature.class))
            blocks.add((new TemperatureBlock()));
        if (Monitor.getMonitor().getThings().containsType(SmartThingMode.class))
            blocks.add(new SmartThingModeBlock());
        return blocks;
    }

    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final IBlock item = this.remove(fromPosition);
        this.add(toPosition, item);
    }

    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(this, toPosition, fromPosition);
    }
}
