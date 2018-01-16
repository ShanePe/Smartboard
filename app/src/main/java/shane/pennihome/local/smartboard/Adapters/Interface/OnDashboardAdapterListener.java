package shane.pennihome.local.smartboard.Adapters.Interface;

import shane.pennihome.local.smartboard.Data.Row;

/**
 * Created by shane on 13/01/18.
 */

public interface OnDashboardAdapterListener {
    void AddBlock(Row row);

    void RowDisplayNameChanged(Row row, boolean displayName);
}
