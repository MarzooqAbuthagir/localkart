package com.kart.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RegBusinessSharedPrefrence {

    public static final String REG_BUSINESS__PREF = "flag";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setMenuFlag(Context context, String flag) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(REG_BUSINESS__PREF, flag);
        editor.apply();
    }

    public static String getMenuFlag(Context context) {
        return getPreferences(context).getString(REG_BUSINESS__PREF, "0");
    }
}
