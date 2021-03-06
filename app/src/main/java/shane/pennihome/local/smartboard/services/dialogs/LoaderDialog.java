package shane.pennihome.local.smartboard.services.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.core.util.Function;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;

import shane.pennihome.local.smartboard.R;
import shane.pennihome.local.smartboard.services.adapters.LoaderAdapter;

/**
 * Created by shane on 30/01/18.
 */

public class LoaderDialog extends Dialog {
    private RecyclerView mRecycleView;
    private LoaderAdapter mAdapter;

    private LoaderDialog(@NonNull Context context) {
        super(context, R.style.transparentDialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        mAdapter = new LoaderAdapter();

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.service_startup_list, null);
        mRecycleView = (RecyclerView) view;
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        setContentView(mRecycleView);
    }

    @SuppressLint("NotifyDataSetChanged")
    public synchronized void setMessages(ArrayList<Pair<String, String>> messages) {
        mAdapter.setMessages(messages);
        mAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public synchronized void addMessage(Pair<String, String> message) {
        mAdapter.addMessage(message);
        mAdapter.notifyDataSetChanged();
        Log.i("Loader", String.format("Added message %s with key: %s", message.first, message.second));
    }

    @SuppressLint("NotifyDataSetChanged")
    public synchronized void removeMessage(final String key) {

        Log.i("Loader", String.format("Removing message with key: %s", key));
        Pair<String, String> item = null;
        for (int i = 0; i < mAdapter.getMessages().size(); i++) {
            if (mAdapter.getMessages().get(i).first.equals(key))
                item = mAdapter.getMessages().get(i);
        }

        if (item != null) {
            mAdapter.getMessages().remove(item);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void post(Runnable action) {
        mRecycleView.post(action);
    }

    public static class AsyncLoaderDialog extends AsyncTask<Void, Pair<String, Pair<String, String>>, Void> {
        @SuppressLint("StaticFieldLeak")
        static AsyncLoaderDialog mLoader;
        @SuppressLint("StaticFieldLeak")
        static LoaderDialog mDialog;
        @SuppressLint("StaticFieldLeak")
        Context mContext;
        Function<Void, Void> mFunction;

        public AsyncLoaderDialog(Context context) {
            this.mContext = context;
        }

        public static void run(Context context, Function<Void, Void> function) {
            mLoader = new AsyncLoaderDialog(context);
            mLoader.mFunction = function;
            mLoader.execute();
        }

        public static void AddMessage(String messageKey, String message) {
            if (mLoader == null)
                return;
            //noinspection unchecked
            mLoader.publishProgress(new Pair<>("add", new Pair<>(messageKey, message)));
        }

        public static void RemoveMessage(String messageKey) {
            if (mLoader == null)
                return;
            //noinspection unchecked
            mLoader.publishProgress(new Pair<>("remove", new Pair<>(messageKey, "")));
        }

        @Override
        protected void onPreExecute() {
            mDialog = new LoaderDialog(mContext);
            mDialog.show();
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(final Pair<String, Pair<String, String>>... values) {
            if (values[0].first.equals("add"))
                mDialog.addMessage(values[0].second);
            if (values[0].first.equals("remove"))
                mDialog.removeMessage(values[0].second.first);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mFunction.apply(null);
            return null;
        }

        @Override
        protected void finalize() {
            mDialog = null;
            mLoader = null;
            mContext = null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            mDialog = null;
            mLoader = null;
            super.onPostExecute(unused);
        }
    }
}
