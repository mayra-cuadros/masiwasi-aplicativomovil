package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManger {
    private static final String PREF_NAME = "travel_gallery_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";

    private static SharedPreferencesManger instance;
    private final SharedPreferences prefs;

    private SharedPreferencesManger(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManger getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManger(context.getApplicationContext());
        }
        return instance;
    }

    public void setLoginStatus(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
