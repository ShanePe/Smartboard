package shane.pennihome.local.smartboard.ui.listeners;

import android.support.annotation.ColorInt;

/**
 * Created by shane on 26/01/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnBackgroundActionListener {
    void OnColourSelected(@ColorInt int colour);

    void OnColourTransparencyChanged(int transparent);

    void OnImageTransparencyChanged(int transparent);

    void OnImageSelected(String imageFile);
}
