package shane.pennihome.local.smartboard.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import shane.pennihome.local.smartboard.data.Globals;
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

        showPropertyWindow(activity, "Add Block", block.getUIHandler().getEditorWindowLayoutID(), new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                block.getUIHandler().buildEditorWindowView(activity, view, Monitor.getMonitor().getThings().getForBlock(block), group);
            }

            @Override
            public void onOkSelected(View view) {
                block.getUIHandler().buildBlockFromEditorWindowView(view, onBlockSetListener);
            }
        });
    }

    public static void showBlockTemplateWindow(final AppCompatActivity activity, final IBlock block,  final OnBlockSetListener onBlockSetListener) {
        if (block == null)
            return;

        showPropertyWindow(activity, "Create Block template", block.getUIHandler().getEditorWindowLayoutID(), new OnPropertyWindowListener() {
            @Override
            public void onWindowShown(View view) {
                block.getUIHandler().buildEditorWindowView(activity, view, null, null);
                ViewGroup group = (ViewGroup)view;
                for(int i = 0;i<group.getChildCount();i++)
                    if(group.getChildAt(i) instanceof ViewSwiper)
                    {
                        ((ViewSwiper)group.getChildAt(i)).removeView("Template");
                        break;
                    }
            }

            @Override
            public void onOkSelected(View view) {
                block.getUIHandler().buildBlockFromEditorWindowView(view, onBlockSetListener);
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

    public static void showPropertyWindow(Context context, String title, int resource, boolean showButtons, RecyclerView.Adapter adapter,
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

            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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

    private static Bitmap scaleDownBitmap(Bitmap image, int maxWidth, int maxHeight, boolean keepAspectRation) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            if (keepAspectRation) {
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float) maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float) maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            } else
                image = Bitmap.createScaledBitmap(image, maxWidth, maxHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Drawable createColourBlocks(Context context, @ColorInt int colours[], int width, int height)
    {
        if (width == 0 || height == 0)
            return null;

        Bitmap bitmap = Bitmap.createBitmap(width * colours.length, height, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        for(int i = 0; i< colours.length;i++)
        {
            paint.setColor(colours[i]);
            Rect rect = new Rect(i * width, 0,(i * width) + width, height );
            canvas.drawRect(rect, paint);
        }
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable combineDrawables(Context context, Drawable draw1, Drawable draw2, int width, int height )
    {
        if (width == 0 || height == 0) {
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap bitmap1 = ((BitmapDrawable)draw1).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable)draw2).getBitmap();

        Rect src1 = new Rect(0, 0, bitmap1.getWidth(), bitmap1.getHeight());
        Rect src2 = new Rect(0, 0, bitmap2.getWidth(), bitmap2.getHeight());

        Rect rect1 = new Rect(0,0, width, height/2);
        Rect rect2 = new Rect(0, height/2, width, height);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap1, src1, rect1, paint);
        canvas.drawBitmap(bitmap2, src2, rect2, paint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable generateImage(Context context, @ColorInt int backClr, int backClrAlphaPerc, Bitmap image, int imageAlphaPerc, int width, int height, boolean roundCrns, ImageRenderTypes imageRenderType) {
        if (width == 0 || height == 0) {
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        if (backClr != 0 && image == null) {
            Paint paint = new Paint();
            paint.setColor(getColorWithAlpha(backClr, backClrAlphaPerc / 100f));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);
        } else if (image != null) {
            if (imageRenderType == null)
                imageRenderType = ImageRenderTypes.Center;

            Bitmap scaled = scaleDownBitmap(image, width, height, imageRenderType == ImageRenderTypes.Center);
            if (roundCrns)
                scaled = getRoundedCornerBitmap(scaled, 4);

            Rect src = new Rect(0, 0, scaled.getWidth(), scaled.getHeight());
            Rect dest = null;

            if (imageRenderType == ImageRenderTypes.Center) {
                int destLeft = (width - scaled.getWidth()) / 2;
                int destTop = (height - scaled.getHeight()) / 2;

                dest = new Rect(destLeft, destTop, scaled.getWidth() + destLeft, scaled.getHeight() + destTop);
            } else if (imageRenderType == ImageRenderTypes.Stretch)
                dest = new Rect(0, 0, width, height);

            Paint paint = new Paint();
            //canvas.clipRect(dest);
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

//    public static Bitmap scaleDownBitmap(Bitmap realImage, float maxImageSize,
//                                         boolean filter) {
//        float ratio = Math.min(
//                (float) maxImageSize / realImage.getWidth(),
//                (float) maxImageSize / realImage.getHeight());
////        float ratio = (float) maxImageSize / realImage.getHeight();
//        int width = Math.round((float) ratio * realImage.getWidth());
//        int height = Math.round((float) ratio * realImage.getHeight());
//
//        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
//                height, filter);
//        return newBitmap;
//    }

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
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(false, null);
            }
        });

        //builder.setCancelable(false);
        builder.setIcon(R.mipmap.icon_add_mm_fg);
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

    public static @ColorInt int getDefaultForegroundColour()
    {
        int[] attrs = {R.attr.colorButtonNormal};
        TypedArray ta = Globals.getContext().obtainStyledAttributes(attrs);
        @ColorInt int color = ta.getResourceId(0, android.R.color.black);
        ta.recycle();
        return color;
    }


    public enum ImageRenderTypes {Center, Stretch}

    public enum IconSizes {Small, Medium, Large}
}
