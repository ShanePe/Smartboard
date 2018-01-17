package shane.pennihome.local.smartboard.UI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import shane.pennihome.local.smartboard.Adapters.SpinnerThingAdapter;
import shane.pennihome.local.smartboard.Comms.Interface.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.Data.Interface.IBlock;
import shane.pennihome.local.smartboard.Data.Routine;
import shane.pennihome.local.smartboard.Data.Switch;
import shane.pennihome.local.smartboard.Data.SwitchBlock;
import shane.pennihome.local.smartboard.Data.Group;
import shane.pennihome.local.smartboard.Data.Interface.IThing;
import shane.pennihome.local.smartboard.Data.Things;
import shane.pennihome.local.smartboard.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.Listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.R;

/**
 * Created by shane on 16/01/18.
 */

public class UIHelper {
    public static void showBlockPropertyWindow(Activity activity, Things things, IBlock switchBlock, OnBlockSetListener onBlockSetListener) {
        showBlockPropertyWindow(activity, things, switchBlock, null, onBlockSetListener);
    }

    public static void showBlockPropertyWindow(final Activity activity, final Things things, final IBlock block, final Group group, final OnBlockSetListener onBlockSetListener) {
        showPropertyWindow(activity, "Add Block", R.layout.prop_block, new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                block.getUIHandler().buildBlockPropertyView(activity, view, things, group);
            }

            @Override
            public void onOkSelected(View view) {
                block.getUIHandler().populateBlockFromView(view, onBlockSetListener);
            }
        });
    }

    @ColorInt
    public static int getThingColour(IThing thing, int Off, int On) {
        if (thing instanceof Routine)
            return Off;
        if (thing instanceof Switch)
            switch (((Switch) thing).getState()) {
                case On:
                    return On;
                default:
                    return Off;
            }
        else
            return 0;
    }

    public static void showColourPicker(Context context, @ColorInt int colour, final OnProcessCompleteListener onProcessCompleteListener) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose colour")
                .initialColor(colour)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (onProcessCompleteListener != null)
                            onProcessCompleteListener.complete(true, selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }

    public static void showPropertyWindow(Context context, String title, int resource, final OnPropertyWindowListener onPropertyWindowListener) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View) inflater.inflate(resource, null);

        if (onPropertyWindowListener != null)
            onPropertyWindowListener.onWindowShown(view);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onPropertyWindowListener != null)
                    onPropertyWindowListener.onOkSelected(view);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public static void ShowInput(final Context context, String title, final OnProcessCompleteListener onProcessCompleteListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onProcessCompleteListener != null) {
                    try {
                        String val = input.getText().toString();
                        if (TextUtils.isEmpty(val))
                            throw new Exception("Value not supplied");
                        onProcessCompleteListener.complete(true, val);
                    } catch (Exception ex) {
                        Toast.makeText(context, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
