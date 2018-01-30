package shane.pennihome.local.smartboard.comms;

/**
 * Created by shane on 29/01/18.
 */

public class ExecutorResult {
    private Exception mError;
    private String mResult;

    public ExecutorResult(Exception mError) {
        this.mError = mError;
    }

    public ExecutorResult(String mResult) {
        this.mResult = mResult;
    }

    public boolean isSuccess() {
        return mError == null;
    }

    public Exception getError() {
        return mError;
    }

    public String getResult() {
        return mResult;
    }
}
