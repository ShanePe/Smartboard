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
import shane.pennihome.local.smartboard.comms.interfaces.OnProcessCompleteListener;
import shane.pennihome.local.smartboard.services.adapters.LoaderAdapter;

/**
 * Created by shane on 30/01/18.
 */

public class LoaderDialog extends Dialog {
    public static LoaderDialog mDialog;
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

    private ArrayList<Pair<String, String>> getMessages() {
        return mAdapter.getMessages();
    }

    public synchronized void setMessages(ArrayList<Pair<String, String>> messages) {
        mAdapter.setMessages(messages);
        mAdapter.notifyDataSetChanged();
    }

    public synchronized void addMessage(Pair<String, String> message) {
        mDialog.mAdapter.addMessage(message);
        mDialog.mAdapter.notifyDataSetChanged();
        Log.i("Loader", String.format("Added message %s with key: %s", message.first, message.second));
    }

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

    private LoaderAdapter.ViewHolder findViewHolder(Object key) {
        if (mRecycleView == null)
            return null;

        for (int i = 0; i < mRecycleView.getChildCount(); i++) {
            LoaderAdapter.ViewHolder vh = (LoaderAdapter.ViewHolder) mRecycleView.findViewHolderForAdapterPosition(i);
            if (vh != null)
                if (vh.getLoadMessage().first.equals(key))
                    return vh;
        }

        return null;
    }

    public static class AsyncLoaderDialog extends AsyncTask<Void, Pair<String, Pair<String, String>>, Void> {
        static AsyncLoaderDialog mLoader;
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
            if(mLoader==null)
                return;
            mLoader.publishProgress(new Pair<>("add", new Pair<>(messageKey, message)));
        }

        public static void RemoveMessage(String messageKey) {
            if(mLoader==null)
                return;
            mLoader.publishProgress(new Pair<>("remove", new Pair<>(messageKey, "")));
        }

        @Override
        protected void onPreExecute() {
            mDialog = new LoaderDialog(mContext);
            mDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(final Pair<String, Pair<String, String>>... values) {
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
        protected void onPostExecute(Void unused) {
            if (mDialog.isShowing())
                mDialog.dismiss();
            mDialog = null;
            mLoader = null;
            super.onPostExecute(unused);
        }
    }
}
