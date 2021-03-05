package com.vume.allinonegames.Util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class JoshLocaleUtils {

    private static Context mContext ;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, HINDI, TELUGU, TAMIL, MALAYALAM, KANNADA})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {ENGLISH, HINDI, TELUGU, TAMIL, MALAYALAM, KANNADA};
    }

    private static final String DEFAULT_LANGAUGE = "en";

    public static final String ENGLISH = "en";
    public static final String HINDI = "hi";
    public static final String TELUGU = "te";
    public static final String TAMIL = "ta";
    public static final String MALAYALAM = "ma";
    public static final String KANNADA = "ka";

    private static volatile JoshLocaleUtils mJoshLocalUtilsInstance;

    public JoshLocaleUtils(Context context){
        if (mJoshLocalUtilsInstance != null) {
            throw new RuntimeException(
                    "Use getInstance() method to get the single instance of this class.");
        }

    }

    public static JoshLocaleUtils getInstance(Context context) {
        mContext = context;
        if (mJoshLocalUtilsInstance == null) {
            synchronized (JoshSharedPreferences.class) {
                if (mJoshLocalUtilsInstance == null) mJoshLocalUtilsInstance = new JoshLocaleUtils(context);
            }
        }
        return mJoshLocalUtilsInstance;
    }

    public void initialize() {
        setCurrentLanguage(JoshApplication.sharedPreferences(mContext).getSharedPrefStringValue(JoshSharedPreferences.SELECTED_LANGUAGE, DEFAULT_LANGAUGE));
    }

    public String getSelectedLanguage() {
        return getSharedPreferenceSelectedLanguage();
    }

    public boolean setCurrentLanguage( @LocaleDef String language) {
        saveToPreference(language);
        return updateResources(mContext, language);
    }

    private String getSharedPreferenceSelectedLanguage() {
        return JoshApplication.sharedPreferences(mContext).getSharedPrefStringValue(JoshSharedPreferences.SELECTED_LANGUAGE, DEFAULT_LANGAUGE);
    }

    private void saveToPreference(String language) {
        JoshApplication.sharedPreferences(mContext).saveToSharedPreferences(JoshSharedPreferences.SELECTED_LANGUAGE, language);
    }

    private boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }
}