package shane.pennihome.local.smartboard.ui.listeners;

import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by SPennicott on 06/02/2018.
 */

public interface OnIconActionListener {
    void OnIconSelected(String iconPath);

    @SuppressWarnings("EmptyMethod")
    void OnIconSizeChanged(UIHelper.IconSizes iconSize);
}
