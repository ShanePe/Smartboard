package shane.pennihome.local.smartboard.Listeners;

import shane.pennihome.local.smartboard.Data.Group;

/**
 * Created by shane on 13/01/18.
 */

public interface OnDashboardAdapterListener {
    void AddBlock(Group group);

    void RowDisplayNameChanged(Group group, boolean displayName);
}
