package com.vume.allinonegames.Util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class JoshSharedPreferences {

    private static SharedPreferences mJoshSharedPreferences;
    private static volatile JoshSharedPreferences mJoshSharedPreferenceInstance;

    public static final String SELECTED_LANGUAGE = "josh_selected_language_callbreak";
    public static final String USER_PROFILEURL = "josh_userprofileurl_callbreak";
    public static final String USER_ISBANKACCOUTNVERIFIED = "josh_isbankaccount_verified_callbreak";
    public static final String USER_ISKYCVERIFIED = "josh_iskyc_verified_callbreak";
    public static final String USER_PHONENO = "josh_user_phoneno_callbreak";
    public static final String USER_NAME = "josh_username_callbreak";
    public static final String EMAIL = "josh_email_callbreak";
    public static final String PASSWORD = "josh_password_callbreak";
    public static final String REMEMBER_ME = "josh_rememberme_callbreak";
    public static final String FIREBASE_USER_UUID = "josh_user_uuid_callbreak";
    public static final String FIREBASE_USER_ID_TOKEN = "josh_user_idtoken_callbreak";
    public static final String FIRST_INSTALL = "josh_first_install_callbreak";
    public static final String INSTALL_KEY = "josh_install_key_callbreak";
    public static final String AUTH_RESULT_USER_ID_TOKEN_ = "josh_auth_result_user_id_token_callbreak";
    public static final String AUTH_RESULT_CREDENTIAL = "josh_auth_result_credential_callbreak";
    public static final String INSTALL_ID = "josh_install_id_callbreak";
    public static final String OAUTH_USER_ID = "josh_oauth_user_id_callbreak";
    public static final String DEVICE_ID = "josh_device_id_callbreak";
    public static final String USER_ID = "josh_user_id_callbreak";
    public static final String LOGIN_AUTHTOKEN = "josh_login_authtoken_callbreak";
    public static final String ISGONETOSETTINGSFORPERMISSION = "josh_is_gone_to_settings_for_permission_callbreak";
    public static final String SHOULD_REFRESH_BALANCE_ON_LOBBYRESUME = "josh_should_refresh_balance_callbreak";
    public static final String CURRENTBALANCE = "joshcurrentbalance_callbreak";
    public static final String CURRENTBONUSMONEY = "joshcurrentbonusmoney_callbreak";
    public static final String CURRENTFCMTOKEN = "joshfcmtoken_callbreak";

    public JoshSharedPreferences(Context context){
        if (mJoshSharedPreferenceInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }
        mJoshSharedPreferences = context.getApplicationContext().getSharedPreferences("JoshRummyAppPref_callbreak", Context.MODE_PRIVATE);
    }

    public static JoshSharedPreferences getInstance(Context context) {
        if (mJoshSharedPreferenceInstance == null) {
            synchronized (JoshSharedPreferences.class) {
                if (mJoshSharedPreferenceInstance == null) mJoshSharedPreferenceInstance = new JoshSharedPreferences(context);
            }
        }
        return mJoshSharedPreferenceInstance;
    }


    public boolean saveToSharedPreferences(String Key, boolean Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putBoolean(Key, Value);
        return editor.commit();
    }


    public boolean saveToSharedPreferences(String Key, String Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putString(Key, Value);
        return editor.commit();
    }


    public boolean saveToSharedPreferences(String Key, int Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putInt(Key, Value);
        return editor.commit();
    }


    public boolean saveToSharedPreferences(String Key, Float Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putFloat(Key, Value);
        return editor.commit();
    }


    public boolean saveToSharedPreferences(String Key, Long Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putLong(Key, Value);
        return editor.commit();
    }


    public boolean saveToSharedPreferences(String Key, Set<String> Value) {
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.putStringSet(Key, Value);
        return editor.commit();
    }


    public String getSharedPrefStringValue(String Key, String defaultVal) {
        return mJoshSharedPreferences.getString(Key, defaultVal);
    }

    public Set<String> getSharedPrefStringSetValue(String Key, Set<String> defaultVal) {
        return mJoshSharedPreferences.getStringSet(Key, defaultVal);
    }

    public boolean getSharedPrefBooleanValue(String Key, boolean defaultVal) {
        return mJoshSharedPreferences.getBoolean(Key, defaultVal);
    }

    public long getSharedPrefLongValue(String Key, long defaultVal) {
        return mJoshSharedPreferences.getLong(Key, defaultVal);
    }

    public Float getSharedPrefFloatValue(String Key, Float defaultVal) {
        return mJoshSharedPreferences.getFloat(Key, defaultVal);
    }

    public int getSharedPrefIntValue(String Key, int defaultVal) {
        return mJoshSharedPreferences.getInt(Key, defaultVal);
    }

    public boolean removeSharedPreferences(String key){
        final SharedPreferences.Editor editor = mJoshSharedPreferences.edit();
        editor.remove(key);
        return editor.commit();
    }
}
