package shane.pennihome.local.smartboard.things.switches;

import shane.pennihome.local.smartboard.thingsframework.interfaces.IThings;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Switches extends IThings<Switch> {
    @Override
    public int SortCompare(Switch a, Switch b) {
        return a.getName().compareTo(b.getName());
    }
}
