package shane.pennihome.local.smartboard.Comms.Interface;

/**
 * Created by shane on 28/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public interface OnProcessCompleteListener<T> {
    void complete(boolean success, T source);
}
