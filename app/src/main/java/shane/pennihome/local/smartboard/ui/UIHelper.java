package shane.pennihome.local.smartboard.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.listeners.OnImageImport;
import shane.pennihome.local.smartboard.listeners.OnPropertyWindowListener;
import shane.pennihome.local.smartboard.listeners.OnThingSelectListener;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.Things;
import shane.pennihome.local.smartboard.thingsframework.adapters.ThingSelectionAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnThingSetListener;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public class UIHelper {
    public static int IMAGE_RESULT = 8841;

    public static void showImageImport(final Fragment fragment)
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(fragment.getContext());
        builder.setTitle("Import image from");

        LayoutInflater inflater = (LayoutInflater) fragment.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View view = inflater.inflate(R.layout.dialog_image_import, null);

        LinearLayoutCompat btnGal = view.findViewById(R.id.btn_import_gallery);
        LinearLayoutCompat btnCam = view.findViewById(R.id.btn_import_camera);

        builder.setView(view);
        final android.app.AlertDialog dialog = builder.create();

        btnGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                fragment.startActivityForResult(intPhoto, IMAGE_RESULT);
                dialog.cancel();
            }
        });

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outFile = new File(fragment.getActivity().getFilesDir(), System.currentTimeMillis() + "_smartboard.png");
                Intent intPic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intPic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
                //intPic.putExtra("camera_file", outFile.getAbsolutePath());
                intPic.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                fragment.startActivityForResult(intPic, IMAGE_RESULT);
                dialog.cancel();
            }
        });

        //noinspection ConstantConditions
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog.show();

    }

    public static void showThingPropertyWindow(Activity activity, Things things, IThing thing, OnThingSetListener onThingSetListener) {
        showThingPropertyWindow(activity, things, thing, null, onThingSetListener);
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

        showPropertyWindow(activity, "Select Things", R.layout.dialog_thing_selection_list,
                false, adapter, 2, null, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        dial[0] = dialog;
                    }
                });
    }

    public static void showThingPropertyWindow(final Activity activity, final Things things, final IThing thing,
                                               final Group group, final OnThingSetListener onThingSetListener) {
        if (thing == null)
            return;

        showPropertyWindow(activity, "Add Block", thing.getUIHandler().getEditorViewResourceID(), new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                thing.getUIHandler().buildBlockPropertyView(activity, view, things, group);
            }

            @Override
            public void onOkSelected(View view) {
                thing.getUIHandler().populateBlockFromView(view, onThingSetListener);
            }
        });
    }

    public static String saveImage(Context context, Uri filePath)
    {
        String fileName = getFileNameFromUri(context, filePath);
        File fileToWrite = new File(context.getFilesDir(), fileName);
        if(fileToWrite.exists())
            fileToWrite.delete();

        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(filePath);
            bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if(fileToWrite.createNewFile())
            {
                fileOutputStream = new FileOutputStream(fileToWrite);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
            }

            return fileToWrite.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @ColorInt
    public static int getThingColour(IThing thing, int Off, int On) {
        if (thing == null)
            return Off;

        if (thing instanceof Routine)
            return Off;
        else if (thing instanceof Switch) {
            Switch sw = (Switch) thing;
            if (sw.getState() == null)
                return Off;

            switch (sw.getState()) {
                case On:
                    return On;
                default:
                    return Off;
            }
        } else
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

    private static void showPropertyWindow(Context context, String title, int resource, boolean showButtons, RecyclerView.Adapter adapter,
                                           int columnCount, final OnPropertyWindowListener onPropertyWindowListener,
                                           final DialogInterface.OnShowListener onShowListener) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View view = inflater.inflate(resource, null);

        if (adapter != null)
            if (view instanceof RecyclerView) {
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
        //noinspection ConstantConditions
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (onShowListener != null)
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    onShowListener.onShow(dialog);
                }
            });

        dialog.show();

    }

    public static int getColorWithAlpha(int clr, float ratio) {
        int alpha = Math.round(Color.alpha(clr) * ratio);

        int r = Color.red(clr);
        int g = Color.green(clr);
        int b = Color.blue(clr);

        return Color.argb(alpha, r, g, b);
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

    public static void ShowConfirm(final Context context, String title, String text, final OnProcessCompleteListener onProcessCompleteListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final TextView confirm = new TextView(context);
        confirm.setPadding(16,16,16,16);
        confirm.setText(text);
        builder.setView(confirm);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onProcessCompleteListener != null) {
                    onProcessCompleteListener.complete(true, null);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(false,null);
            }
        });

        //builder.setCancelable(false);
        builder.setIcon(R.drawable.icon_cog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(false,null);
            }
        });

        builder.show();
    }
}
