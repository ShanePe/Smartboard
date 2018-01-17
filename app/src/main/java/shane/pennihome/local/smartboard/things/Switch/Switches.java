package shane.pennihome.local.smartboard.things.Switch;

import shane.pennihome.local.smartboard.things.Interface.IThingCollection;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public class Switches extends IThingCollection<Switch> {
    @Override
    public int SortCompare(Switch a, Switch b) {
        return a.getName().compareTo(b.getName());
    }
}
