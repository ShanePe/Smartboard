package shane.pennihome.local.smartboard.data;

import android.content.Context;

import shane.pennihome.local.smartboard.data.interfaces.IDatabaseObject;
import shane.pennihome.local.smartboard.data.sql.DBEngine;

/**
 * Created by shane on 02/03/18.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Options extends IDatabaseObject {
    private boolean mKeepScreenOn = false;
    private boolean mFadeOut = false;
    private int mFadeOutInMinutes = 5;
    private Thread mScreenFaderThread;

    public static Options getFromDataStore(Context context) {
        DBEngine db = new DBEngine(context);
        Options opts = (Options) db.readFromDatabase("options");
        if (opts == null)
            opts = new Options();
        return opts;
    }

    public boolean isKeepScreenOn() {
        return mKeepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        mKeepScreenOn = keepScreenOn;
        if (!mKeepScreenOn)
            setFadeOut(false);
    }

    public boolean isFadeOut() {
        return mFadeOut;
    }

    public void setFadeOut(boolean fadeOut) {
        mFadeOut = fadeOut;
    }

    public int getFadeOutInMinutes() {
        return mFadeOutInMinutes;
    }

    public void setFadeOutInMinutes(int fadeOutInMinutes) {
        mFadeOutInMinutes = fadeOutInMinutes;
    }

    @Override
    public Types getDatabaseType() {
        return Types.Options;
    }

    @Override
    public String getDataID() {
        return "options";
    }

    public void stopMonitorForScreenFadeOut() {
        if (mScreenFaderThread != null) {
            mScreenFaderThread.interrupt();
            try {
                if (mScreenFaderThread != null)
                    mScreenFaderThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startMonitorForScreenFadeOut(final OnFadeTimeElapsedListener onFadeTimeElapsedListener) {
        stopMonitorForScreenFadeOut();
        mScreenFaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(getFadeOutInMinutes() * 60 * 1000);
                    if (onFadeTimeElapsedListener != null)
                        onFadeTimeElapsedListener.onElapsed();

                } catch (InterruptedException ignored) {
                } finally {
                    mScreenFaderThread = null;
                }
            }
        });
        mScreenFaderThread.setName("ScreenFaderThread");
        mScreenFaderThread.start();
    }

    @Override
    protected void finalize() throws Throwable {
        stopMonitorForScreenFadeOut();
        super.finalize();
    }

    public interface OnFadeTimeElapsedListener {
        void onElapsed();
    }
}
