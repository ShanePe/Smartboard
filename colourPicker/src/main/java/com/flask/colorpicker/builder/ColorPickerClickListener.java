package com.flask.colorpicker.builder;

import android.content.DialogInterface;

/**
 * Created by Charles Anderson on 4/17/15.
 */
@SuppressWarnings("ALL")
public interface ColorPickerClickListener {
    void onClick(DialogInterface d, int lastSelectedColor, Integer[] allColors);
}
