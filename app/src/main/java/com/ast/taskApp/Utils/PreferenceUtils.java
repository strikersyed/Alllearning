package com.ast.taskApp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static boolean setRingtoneUri(String ringtoneUri, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("ringtoneUri", ringtoneUri);
        prefsEditor.apply();
        return true;
    }

    public static String getRingtoneUri(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("ringtoneUri", null);
    }
}
