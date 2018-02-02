package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.Monitor;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.data.Group;
import shane.pennihome.local.smartboard.things.routines.Routine;
import shane.pennihome.local.smartboard.things.switches.Switch;
import shane.pennihome.local.smartboard.thingsframework.adapters.BlockSelectionAdapter;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IBlock;
import shane.pennihome.local.smartboard.thingsframework.interfaces.IThing;
import shane.pennihome.local.smartboard.thingsframework.listeners.OnBlockSetListener;
import shane.pennihome.local.smartboard.ui.dialogs.ImportImageDialog;
import shane.pennihome.local.smartboard.ui.listeners.OnBlockSelectListener;
import shane.pennihome.local.smartboard.ui.listeners.OnPropertyWindowListener;

/**
 * Created by shane on 16/01/18.
 */

@SuppressWarnings("ALL")
public class UIHelper {
    public static void showImageImport(FragmentManager fragmentManager, final OnProcessCompleteListener<String> onProcessCompleteListener) {

        final ImportImageDialog importImageDialog = ImportImageDialog.newInstance(onProcessCompleteListener);
        importImageDialog.show(fragmentManager, "image_import");
    }

    public static void showBlockPropertyWindow(AppCompatActivity activity, IBlock block, OnBlockSetListener onBlockSetListener) {
        showBlockPropertyWindow(activity, block, null, onBlockSetListener);
    }

    public static void showBlockSelectionWindow(AppCompatActivity activity, final OnBlockSelectListener onBlockSelectListener) {
        final DialogInterface[] dial = new DialogInterface[1];
        BlockSelectionAdapter adapter = new BlockSelectionAdapter(new OnBlockSelectListener() {
            @Override
            public void BlockSelected(IBlock block) {
                onBlockSelectListener.BlockSelected(block);
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

    public static void showBlockPropertyWindow(final AppCompatActivity activity, final IBlock block,
                                               final Group group, final OnBlockSetListener onBlockSetListener) {
        if (block == null)
            return;

        showPropertyWindow(activity, "Add Block", block.getUIHandler().getEditorViewResourceID(), new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                block.getUIHandler().buildBlockPropertyView(activity, view, Monitor.getMonitor().getThings().getFilterViewForBlock(block), group);
            }

            @Override
            public void onOkSelected(View view) {
                block.getUIHandler().populateBlockFromView(view, onBlockSetListener);
            }
        });
    }

    public static String saveBitmap(Context context, Bitmap imageSave) {
        String fileName = "Smartboard_" + UUID.randomUUID().toString() + ".png";
        File fileToWrite = new File(context.getFilesDir(), fileName);

        if (fileToWrite.exists())
            fileToWrite.delete();

        FileOutputStream fileOutputStream = null;

        try {
            if (fileToWrite.createNewFile()) {
                fileOutputStream = new FileOutputStream(fileToWrite);
                imageSave.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
            }

            return fileToWrite.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String saveImage(Context context, Uri filePath) {
        String fileName = "Smartboard_" + UUID.randomUUID().toString() + ".png";
        File fileToWrite = new File(context.getFilesDir(), fileName);
        if (fileToWrite.exists())
            fileToWrite.delete();

        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(filePath);
            bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (fileToWrite.createNewFile()) {
                fileOutputStream = new FileOutputStream(fileToWrite);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
            }

            return fileToWrite.getPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
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
            return sw.isOn() ? On : Off;
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

    public static Bitmap scaleDownBitmap(Bitmap realImage, float maxImageSize,
                                         boolean filter) {
        float ratio = Math.max(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        //float ratio = (float) maxImageSize / realImage.getHeight();
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static Drawable generateImage(Context context, @ColorInt int backClr, int backClrAlphaPerc, Bitmap image, int imageAlphaPerc, int width, int height, boolean roundCrns) {
        if (width == 0) width = 100;
        if (height == 0) height = 50;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (backClr != 0 && image == null) {
            Paint paint = new Paint();
            paint.setColor(getColorWithAlpha(backClr, backClrAlphaPerc / 100f));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);
        } else if (image != null) {
            Bitmap scaled = scaleDownBitmap(image, Math.min(width, height), true);
            if (roundCrns)
                scaled = getRoundedCornerBitmap(scaled, 4);

            int destLeft = (width - scaled.getWidth()) / 2;
            Rect src = new Rect(0, 0, scaled.getWidth(), scaled.getHeight());
            Rect dest = new Rect(destLeft, 0, scaled.getWidth() + destLeft, scaled.getHeight());

            Paint paint = new Paint();
            canvas.clipRect(dest);
            if (backClr != 0) {
                paint.setColor(getColorWithAlpha(backClr, backClrAlphaPerc / 100f));
                paint.setStyle(Paint.Style.FILL);
                if (roundCrns) {
                    RectF rectF = new RectF(dest);
                    canvas.drawRoundRect(rectF, 4, 4, paint);
                } else
                    canvas.drawPaint(paint);
            }
            if (imageAlphaPerc > 0)
                paint.setAlpha(255 / 100 * imageAlphaPerc);

            canvas.drawBitmap(scaled, src, dest, paint);
        }

        if (roundCrns && image == null)
            return new BitmapDrawable(context.getResources(), getRoundedCornerBitmap(bitmap, 4));
        else
            return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static GradientDrawable getButtonShape(@ColorInt int colour) {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(4);
        shape.setColor(colour);
        return shape;
    }

    public static void showInput(final Context context, String title, final OnProcessCompleteListener onProcessCompleteListener) {
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

    public static void showConfirm(final Context context, String title, String text, final OnProcessCompleteListener onProcessCompleteListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final TextView confirm = new TextView(context);
        confirm.setPadding(16, 16, 16, 16);
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
                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(false, null);
            }
        });

        //builder.setCancelable(false);
        builder.setIcon(R.drawable.icon_cog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(false, null);
            }
        });

        builder.show();
    }

    public static Drawable getImageFromFile(Context context, String sFile, int alpha) {
        Drawable drawable = getImageFromFile(context, sFile);
        drawable.setAlpha((255 / 100 * alpha));
        return drawable;
    }

    public static Drawable getImageFromFile(Context context, String sFile) {
        Bitmap bitmap = BitmapFactory.decodeFile(sFile);
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;
    }
}
