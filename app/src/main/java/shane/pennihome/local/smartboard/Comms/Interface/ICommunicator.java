package shane.pennihome.local.smartboard.Comms.Interface;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import shane.pennihome.local.smartboard.Comms.RESTCommunicatorResult;

/**
 * Created by shane on 01/01/18.
 */

@SuppressWarnings("ALL")
public abstract class ICommunicator<T> extends AsyncTask<String, String, RESTCommunicatorResult> {
    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final OnProcessCompleteListener<T> mProcessCompleteListener;
    private ProgressDialog mDialog;
    private boolean mSuccess;

    protected ICommunicator(Context mContext, OnProcessCompleteListener<T> mProcessCompleteListener) {
        this.mContext = mContext;
        this.mProcessCompleteListener = mProcessCompleteListener;
    }

    @SuppressWarnings("SameParameterValue")
    protected void UpdateDialog(final String msg) {
        if (mContext == null)
            return;

        Handler mainHandler = new Handler(mContext.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                if (mDialog == null)
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                else
                    mDialog.setMessage(msg);
            }
        };
        mainHandler.post(myRunnable);
    }

    protected abstract String getFailedMessage();

    protected abstract String getDialogMessage();

    protected abstract JSONObject Process() throws Exception;

    protected abstract void Complete(RESTCommunicatorResult result) throws Exception;

    public void PreProcess() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PreProcess();

        mSuccess = false;

        if (mContext != null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage(getDialogMessage());
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(true);
            mDialog.show();
        }
    }

    @Override
    protected RESTCommunicatorResult doInBackground(String... strings) {
        RESTCommunicatorResult result = new RESTCommunicatorResult();

        try {
            result.setResult(retrier());
        } catch (Exception ex) {
            result.setException(ex);
        }

        return result;
    }

    private JSONObject retrier() throws Exception {
        int max = 5;
        int current = 0;

        while (current <= max) {
            try {
                return Process();
            } catch (IOException ioe) {
                Thread.sleep(2000);
                current++;
            }
        }

        throw new Exception("Could not communicate with the gateway.");
    }

    @Override
    protected void onPostExecute(RESTCommunicatorResult restCommunicatorResult) {
        super.onPostExecute(restCommunicatorResult);
        try {
            if (!restCommunicatorResult.isSuccess())
                throw restCommunicatorResult.getException();

            Complete(restCommunicatorResult);

            mSuccess = true;

        } catch (Exception e) {
            if (mContext != null)
                Toast.makeText(mContext, getFailedMessage(), Toast.LENGTH_SHORT).show();
            mSuccess = false;
        } finally {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
            if (mProcessCompleteListener != null)
                mProcessCompleteListener.complete(mSuccess, (T) this);
        }
    }
}
