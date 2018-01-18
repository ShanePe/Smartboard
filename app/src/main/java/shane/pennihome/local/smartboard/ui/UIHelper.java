package shane.pennihome.local.smartboard.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.blocks.Listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.blocks.interfaces.IBlock;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.listeners.OnThingSelectListener;
import shane.pennihome.local.smartboard.things.Adapters.ThingSelectionAdapter;
import shane.pennihome.local.smartboard.things.Interface.IThing;
import shane.pennihome.local.smartboard.things.Routine.Routine;
import shane.pennihome.local.smartboard.things.Switch.Switch;
import shane.pennihome.local.smartboard.things.Things;

/**
 * Created by shane on 16/01/18.
 */

public class UIHelper {
    public static void showBlockPropertyWindow(Activity activity, Things things, IBlock block, OnBlockSetListener onBlockSetListener) {
        showBlockPropertyWindow(activity, things, block, null, onBlockSetListener);
    }

    public static void ShowThingsSelectionWindow(Activity activity, final OnThingSelectListener onThingSelectListener) {
        final DialogInterface[] dial = new DialogInterface[1];
        ThingSelectionAdapter adapter = new ThingSelectionAdapter(new OnThingSelectListener() {
            @Override
            public void ThingSelected(IThing thing) {
                onThingSelectListener.ThingSelected(thing);
                dial[0].dismiss();
            }
        });

        showPropertyWindow(activity, "Select Things", R.layout.thing_selection_list,
                false, adapter, 2, null, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dial[0] = dialog;
                    }
                });
    }

    public static void showBlockPropertyWindow(final Activity activity, final Things things, final IBlock block,
                                               final Group group, final OnBlockSetListener onBlockSetListener) {
        if(block == null)
            return;

        showPropertyWindow(activity, "Add Block", block.getEditorViewResourceID(), new OnPropertyWindowListener() {
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
        if (thing == null)
            return Off;

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

    public static void showPropertyWindow(Context context, String title, int resource,
                                          final OnPropertyWindowListener onPropertyWindowListener) {
        showPropertyWindow(context, title, resource, true, null, 0, onPropertyWindowListener, null);
    }

    public static Dialog showPropertyWindow(Context context, String title, int resource, boolean showButtons, RecyclerView.Adapter adapter,
                                            int columnCount, final OnPropertyWindowListener onPropertyWindowListener,
                                            final DialogInterface.OnShowListener onShowListener) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = (View) inflater.inflate(resource, null);

        if(adapter != null)
            if(view instanceof RecyclerView)
            {
                RecyclerView recyclerView = (RecyclerView) view;
                if (columnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
                }

            recyclerView.setAdapter(adapter);

            }

        if (onPropertyWindowListener != null)
            onPropertyWindowListener.onWindowShown(view);

        builder.setView(view);

        if (showButtons) {
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
        }

        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        if(onShowListener != null)
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    onShowListener.onShow(dialog);
                }
            });

        dialog.show();

        return dialog;
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
