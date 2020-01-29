package com.ast.taskApp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    public static boolean setUser(String email, String firstName, String fullName, int isLoggedin, String platform, String profilePicture, int subscriptionType, String userID, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("email", email);
        prefsEditor.putString("firstName", firstName);
        prefsEditor.putString("fullName", fullName);
        prefsEditor.putInt("isLoggedin", isLoggedin);
        prefsEditor.putString("platform", platform);
        prefsEditor.putString("profilePicture", profilePicture);
        prefsEditor.putInt("subscriptionType", subscriptionType);
        prefsEditor.putString("userID", userID);
        prefsEditor.apply();
        return true;
    }

    public static String getId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("userID", null);
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("email", null);
    }

    public static String getName(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("fullName", null);
    }

    public static String getImage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("profilePicture", null);
    }

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

    public static boolean clearMemory(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        return true;
    }
}
