package shane.pennihome.local.smartboard.UI;

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
import shane.pennihome.local.smartboard.Data.Block;
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
    public static void showBlockPropertyWindow(Context context, Things things, Block block, OnBlockSetListener onBlockSetListener) {
        showBlockPropertyWindow(context, things, block, null, onBlockSetListener);
    }

    public static void showBlockPropertyWindow(final Context context, final Things things, final Block block, final Group group, final OnBlockSetListener onBlockSetListener) {
        showPropertyWindow(context, "Add Block", R.layout.prop_block, new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                Spinner spThings = view.findViewById(R.id.sp_thing);
                final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
                NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
                NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

                final Button btnBGOff = view.findViewById(R.id.btn_clr_bg_Off);
                final Button btnBGOn = view.findViewById(R.id.btn_clr_bg_On);
                final Button btnFGOff = view.findViewById(R.id.btn_clr_fg_Off);
                final Button btnFGOn = view.findViewById(R.id.btn_clr_fg_On);

                SpinnerThingAdapter aptr = new SpinnerThingAdapter(context);
                aptr.setThings(things);
                spThings.setAdapter(aptr);

                final int spAtInx = block.getThing() == null ? -1 : things.GetIndex(block.getThing());
                if (spAtInx != -1) {
                    spThings.setSelection(spAtInx);
                    txtBlkName.setText(block.getName());
                }

                spThings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (block.getThing() == null) {
                            IThing thing = (IThing) parent.getItemAtPosition(position);
                            txtBlkName.setText(thing.getName());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                txtWidth.setMaxValue(4);
                txtWidth.setMinValue(1);
                txtHeight.setMaxValue(4);
                txtHeight.setMinValue(1);
                txtWidth.setWrapSelectorWheel(true);
                txtHeight.setWrapSelectorWheel(true);

                txtWidth.setValue(block.getWidth());
                txtHeight.setValue(block.getHeight());

                btnBGOff.setBackgroundColor(block.getBackgroundColourOff());
                btnBGOn.setBackgroundColor(block.getBackgroundColourOn());
                btnFGOff.setBackgroundColor(block.getForeColourOff());
                btnFGOn.setBackgroundColor(block.getForeColourOn());

                btnBGOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColourPicker(context, block.getBackgroundColourOff(), new OnProcessCompleteListener() {
                            @Override
                            public void complete(boolean success, Object source) {
                                @ColorInt int clr = (int) source;
                                block.setBackgroundColourOff(clr);
                                if (group != null)
                                    group.setDefaultBlockBackgroundColourOff(clr);
                                btnBGOff.setBackgroundColor(block.getBackgroundColourOff());
                            }
                        });
                    }
                });

                btnFGOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColourPicker(context, block.getForeColourOn(), new OnProcessCompleteListener() {
                            @Override
                            public void complete(boolean success, Object source) {
                                @ColorInt int clr = (int) source;
                                block.setForeColourOff(clr);
                                if (group != null)
                                    group.setDefaultBlockForeColourOff(clr);
                                btnFGOff.setBackgroundColor(block.getForeColourOff());

                            }
                        });
                    }
                });

                btnBGOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColourPicker(context, block.getBackgroundColourOn(), new OnProcessCompleteListener() {
                            @Override
                            public void complete(boolean success, Object source) {
                                @ColorInt int clr = (int) source;
                                block.setBackgroundColourOn(clr);
                                if (group != null)
                                    group.setDefaultBlockBackgroundColourOn(clr);
                                btnBGOn.setBackgroundColor(block.getBackgroundColourOn());
                            }
                        });
                    }
                });

                btnFGOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showColourPicker(context, block.getForeColourOn(), new OnProcessCompleteListener() {
                            @Override
                            public void complete(boolean success, Object source) {
                                @ColorInt int clr = (int) source;
                                block.setForeColourOn(clr);
                                if (group != null)
                                    group.setDefaultBlockForeColourOn(clr);
                                btnFGOn.setBackgroundColor(block.getForeColourOn());
                            }
                        });
                    }
                });
            }

            @Override
            public void onOkSelected(View view) {
                Spinner spThings = view.findViewById(R.id.sp_thing);
                final EditText txtBlkName = view.findViewById(R.id.txt_blk_name);
                NumberPicker txtWidth = view.findViewById(R.id.txt_blk_width);
                NumberPicker txtHeight = view.findViewById(R.id.txt_blk_height);

                block.setThing((IThing) spThings.getSelectedItem());
                block.setName(txtBlkName.getText().toString());
                block.setWidth(txtWidth.getValue());
                block.setHeight(txtHeight.getValue());

                if (onBlockSetListener != null) {
                    onBlockSetListener.OnSet(block);
                }
            }
        });
    }

    private static void showColourPicker(Context context, @ColorInt int colour, final OnProcessCompleteListener onProcessCompleteListener) {
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
