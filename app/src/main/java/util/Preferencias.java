package util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

public class Preferencias {
    private static final String APP_FILE = "APP_PREFERENCES";
    private static final String USER_FILE = "USER_PREFERENCES";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    static final String PREF_KEY_TWITTER_PHOTO = "user_profile_photo";
    static final String PREF_KEY_TWITTER_URLPHOTO = "user_profile_photo_url";
    static final String PREF_KEY_TWITTER_BANNER = "user_profile_banner";
    static final String PREF_KEY_TWITTER_URLBANNER = "user_profile_banner_url";
    static final String PREF_KEY_TWITTER_COLOR = "user_profile_color";

    //OAUTHTOKEN
    public static void setOauthToken(Context context, String token)
    {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_OAUTH_TOKEN, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static String getOauthToken(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_OAUTH_TOKEN, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }
    //OAUTHSECRET
    public static void setOauthSecret(Context context, String token) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_OAUTH_SECRET, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }
    public static String getOauthSecret(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_OAUTH_SECRET, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }
    //LOGIN
    public static void setLogged(Context context, boolean bool) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putBoolean(PREF_KEY_TWITTER_LOGIN, bool);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static boolean getLogged(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getBoolean(PREF_KEY_TWITTER_LOGIN, false);
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return false;
    }
    //PHOTO
    public static void setPhoto(Context context, String token) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_TWITTER_PHOTO, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static String getPhoto(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_TWITTER_PHOTO, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }
    //BANNER
    public static void setBanner(Context context, String token) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_TWITTER_BANNER, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static String getBanner(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_TWITTER_BANNER, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }
    //PHOTO
    public static void setPhotoURL(Context context, String token) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_TWITTER_URLPHOTO, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static String getPhotoURL(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_TWITTER_URLPHOTO, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }
    //BANNER
    public static void setBannerURL(Context context, String token) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
            editor.putString(PREF_KEY_TWITTER_URLBANNER, token);
            editor.commit();
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
    }

    public static String getBannerURL(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getString(PREF_KEY_TWITTER_URLBANNER, "");
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return null;
    }

    public static void setProfileColor(Context context, int i) {
        try {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).edit();
        editor.putInt(PREF_KEY_TWITTER_COLOR, i);
        editor.commit();
    } catch (Exception e) {
        Log.d(Preferencias.class.getSimpleName(), "Exception", e);
    }
    }
    public static int getProfileColor(Context context) {
        try {
            return context.getSharedPreferences(USER_FILE, Activity.MODE_PRIVATE).getInt(PREF_KEY_TWITTER_COLOR, -1);
        } catch (Exception e) {
            Log.d(Preferencias.class.getSimpleName(), "Exception", e);
        }
        return -1;
    }
}



