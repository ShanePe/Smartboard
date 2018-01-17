package shane.pennihome.local.smartboard.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

/**
 * Created by shane on 27/12/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Globals {
    public final static String ACTIVITY = "SmartBoard";
    public final static String ST_CLIENT_ID = "953f9b86-9a48-461a-91e9-c9544dd980c4";
    public final static String ST_CLIENT_SECRET = "8da94852-2c55-47b0-89ee-79ce8ac3bcd5";
    public final static String ST_REDIRECT_URI = "http://localhost:4567/oauth/callback";
    public final static String ST_GRANT_TYPE = "authorization_code";
    public final static String ST_TOKEN_URL = "https://graph.api.smartthings.com/oauth/token";
    public final static String ST_OAUTH_URL = "https://graph.api.smartthings.com/oauth/authorize";
    public final static String ST_ENDPOINT_URL = "https://graph.api.smartthings.com/api/smartapps/endpoints";
    public final static String ST_OAUTH_SCOPE = "app";
    public final static String PH_DISCOVER_URL = "https://www.meethue.com/api/nupnp";
    public final static String ST_SERVER_URI = "https://www.googleapis.com/auth/urlshortener";
    private static SharedPreferences mPrefs;

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

  /*  public static Activity getContext() {
        return _context;
    }

    public static void setContext(Activity _context) {
        Globals._context = _context;
    }*/
}
