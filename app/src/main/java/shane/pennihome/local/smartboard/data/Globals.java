package shane.pennihome.local.smartboard.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Globals extends Application{
    public final static String ACTIVITY = "SmartBoard";
    public final static int BLOCK_COLUMNS = 8;
    public final static int BLOCK_PADDING = 2;
    private static SharedPreferences mPrefs;
    private static Globals mInstance;

    public static SharedPreferences getSharedPreferences() {
        return mPrefs;
    }

    public static void setSharedPreferences(Context c) {
        mPrefs = c.getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        if (mPrefs.getString("uid", "").equals("")) {

            char[] chars = "abcdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
            Random r = new Random(System.currentTimeMillis());
            char[] id = new char[8];
            for (int i = 0; i < 8; i++) {
                id[i] = chars[r.nextInt(chars.length)];
            }

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("uid", new String(id));
            editor.apply();
        }
    }

    public static long GetNextLongId()
    {
        long nextId = mPrefs.getLong("uniId", 0 ) + 1;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putLong("uniId", nextId);
        editor.apply();

        return nextId;
    }

    private static Globals getInstance()
    {
        return mInstance;
    }

    public static Context getContext()
    {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
    }
}
