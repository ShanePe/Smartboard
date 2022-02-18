package shane.pennihome.local.smartboard.ui.listeners;

import android.view.View;

public abstract class OnDialogWindowListener<T> implements OnPropertyWindowListener {
    public abstract T Populate(View view);

    public abstract void OnComplete(T data);

    @Override
    public void onOkSelected(View view) {
        OnComplete(Populate(view));
    }
}
