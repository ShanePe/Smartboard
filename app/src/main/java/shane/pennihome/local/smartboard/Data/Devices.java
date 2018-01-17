package shane.pennihome.local.smartboard.Data;

import shane.pennihome.local.smartboard.Data.Interface.IThingCollection;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Devices extends IThingCollection<Device> {
    @Override
    public int SortCompare(Device a, Device b) {
        return a.getName().compareTo(b.getName());
    }
}
