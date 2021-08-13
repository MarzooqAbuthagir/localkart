package com.kart.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RegBusinessTypeSharedPreference {
    public static final String REG_BUSINESS_TYPE__PREF = "type";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setBusinessType(Context context, String type) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(REG_BUSINESS_TYPE__PREF, type);
        editor.apply();
    }

    public static String getBusinessType(Context context) {
        return getPreferences(context).getString(REG_BUSINESS_TYPE__PREF, "");
    }
}
