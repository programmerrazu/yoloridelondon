package com.razu.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "session";
    private static final String IS_FIRST_TIME_LAUNCH = "is_first_time_launch";
    private static final String IS_SIGN_IN = "is_sign_in";
    private static final String EMAIL = "email";

    public PreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setSignIn(boolean isSignIn) {
        editor.putBoolean(IS_SIGN_IN, isSignIn);
        editor.commit();
    }

    public boolean isSignIn() {
        return pref.getBoolean(IS_SIGN_IN, true);
    }

    public void setEmail(String email) {
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(EMAIL, "");
    }
}