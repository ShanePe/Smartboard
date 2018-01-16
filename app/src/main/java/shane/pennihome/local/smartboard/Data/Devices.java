package shane.pennihome.local.smartboard.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import shane.pennihome.local.smartboard.Data.Interface.Thing;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Devices extends ArrayList<Device> {
    public void remove(Thing.Source source) {
        for (Device d : this)
            if (d.getSource() == source)
                remove(d);
    }

    public Device getbyId(String Id) {
        for (Device d : this)
            if (d.getId().equals(Id))
                return d;

        return null;
    }

    public void sort() {
        Collections.sort(this, new Comparator<Device>() {
            @Override
            public int compare(Device o1, Device o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
