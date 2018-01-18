package shane.pennihome.local.smartboard.blocks.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shane on 17/01/18.
 */

public class IBlocks extends ArrayList<IBlock> {
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
