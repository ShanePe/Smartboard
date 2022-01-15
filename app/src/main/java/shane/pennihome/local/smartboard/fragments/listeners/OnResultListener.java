package shane.pennihome.local.smartboard.fragments.listeners;

import android.content.Intent;

/**
 * Created by shane on 27/01/18.
 */

public interface OnResultListener {
    void OnResult(int requestCode, int resultCode, Intent data);
}
