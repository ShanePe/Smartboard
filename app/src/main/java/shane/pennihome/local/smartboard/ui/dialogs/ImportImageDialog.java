package shane.pennihome.local.smartboard.ui.dialogs;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.ui.UIHelper;

/**
 * Created by shane on 27/01/18.
 */

public class ImportImageDialog extends DialogFragment {
    private static final int IMAGE_RESULT = 8841;
    private static final int CAMERA_RESULT = 8842;
    private OnProcessCompleteListener<String> mOnProcessCompleteListener;

    public static ImportImageDialog newInstance(OnProcessCompleteListener<String> onProcessCompleteListener) {
        ImportImageDialog frag = new ImportImageDialog();
        frag.setOnProcessCompleteListener(onProcessCompleteListener);
        Bundle args = new Bundle();
        args.putString("title", "Select Image");
        frag.setArguments(args);
        return frag;
    }

    @SuppressLint("StaticFieldLeak")
    private static void getFileFromIntent(final Context context, final Intent data, final int code, final OnProcessCompleteListener<String> onProcessCompleteListener) {
        new AsyncTask<Void, Void, String>() {
            ProgressDialog dialog;

            @Override
            protected String doInBackground(Void... voids) {
                String result = "";
                if (code == IMAGE_RESULT) {
                    result = UIHelper.saveImage(context, data.getData());
                } else if (code == CAMERA_RESULT) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    result = UIHelper.saveBitmap(context, imageBitmap);
                }
                return result;
            }

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog();
                dialog.setMessage("Loading image ...");
                dialog.show(context);
            }

            @Override
            protected void onPostExecute(String s) {
                if (onProcessCompleteListener != null)
                    onProcessCompleteListener.complete(true, s);
                if (dialog != null)
                    dialog.dismiss();
            }
        }.execute();
    }

    private void setOnProcessCompleteListener(OnProcessCompleteListener<String> onProcessCompleteListener) {
        this.mOnProcessCompleteListener = onProcessCompleteListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_import, container, false);
        LinearLayoutCompat btnGal = view.findViewById(R.id.btn_import_gallery);
        LinearLayoutCompat btnCam = view.findViewById(R.id.btn_import_camera);

        btnGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intPhoto, IMAGE_RESULT);
            }
        });

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_RESULT);
            }
        });

        Bundle args = getArguments();
        assert args != null;
        getDialog().setTitle(args.getString("title"));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mOnProcessCompleteListener != null && resultCode == RESULT_OK) {
            getFileFromIntent(getActivity(), data, requestCode, new OnProcessCompleteListener<String>() {
                @Override
                public void complete(boolean success, String source) {
                    if (success)
                        mOnProcessCompleteListener.complete(true, source);
                    Handler dismisser = new Handler();
                    dismisser.post(new Runnable() {
                        @Override
                        public void run() {
                            getDialog().dismiss();
                        }
                    });
                }
            });
        }
    }
}
