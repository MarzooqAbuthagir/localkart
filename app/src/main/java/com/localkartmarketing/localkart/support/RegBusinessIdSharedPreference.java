package com.localkartmarketing.localkart.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RegBusinessIdSharedPreference {
    public static final String REG_BUSINESS_ID__PREF = "id";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setBusinessId(Context context, String id) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(REG_BUSINESS_ID__PREF, id);
        editor.apply();
    }

    public static String getBusinessId(Context context) {
        return getPreferences(context).getString(REG_BUSINESS_ID__PREF, "");
    }
}
