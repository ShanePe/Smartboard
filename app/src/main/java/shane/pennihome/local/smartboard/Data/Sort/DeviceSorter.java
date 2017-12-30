package shane.pennihome.local.smartboard.Data.Sort;

import java.util.Comparator;

import shane.pennihome.local.smartboard.Data.Device;

/**
 * Created by shane on 28/12/17.
 */

public class DeviceSorter implements Comparator<Device> {
    public int compare(Device o1, Device o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
