package shane.pennihome.local.smartboard.Comms.Interface;

/**
 * Created by shane on 28/12/17.
 */

public interface ProcessCompleteListener<T> {
    void Complete(boolean success, T source);
}
