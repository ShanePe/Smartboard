package shane.pennihome.local.smartboard.ui.listeners;

import android.support.annotation.ColorInt;

import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 26/01/18.
 */

public interface OnBackgroundActionListener {
    void OnColourSelected(@ColorInt int colour);

    void OnColourTransparencyChanged(int transparent);

    void OnImageTransparencyChanged(int transparent);

    void OnImageSelected(String imageFile);

    void OnImageRenderTypeChanged(UIHelper.ImageRenderTypes imageRenderType);

    void OnPaddingChanged(int padding);
}
