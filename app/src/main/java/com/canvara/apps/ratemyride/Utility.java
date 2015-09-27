package com.canvara.apps.ratemyride;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Copyright (C) 2015, Canvara Technologies
 *
 * Author: Hari Narasimhan
 */

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }


    public static String convertToK(long value) {
        double val = (value / 1000);
        return String.format("%.0f", val);
    }

    public static String convertToPercentage(double value) {
        return String.format("%.00f", value * 100);
    }
}
