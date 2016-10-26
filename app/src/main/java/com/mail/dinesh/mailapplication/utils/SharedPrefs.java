package com.mail.dinesh.mailapplication.utils;

/**
 * Created by dinesh on 26.10.16.
 */

import android.content.Context;

import com.mail.dinesh.mailapplication.conf.Configuration;

public class SharedPrefs {

    public static void storePref(String key, String value, Context context) {
        context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(key, value)
                .commit();
    }

    public static void storePref(String key, boolean value, Context context) {
        context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).edit().putBoolean(key, value)
                .commit();
    }

    public static void storePref(String key, int value, Context context) {
        context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).edit().putInt(key, value)
                .commit();
    }

    public static void storePref(String key, long value, Context context) {
        context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).edit().putLong(key, value)
                .commit();
    }

    public static void storePref(String key, float value, Context context) {
        context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).edit().putFloat(key, value)
                .commit();
    }

    public static String getPrefString(String key, String defValue, Context context) {
        if (context != null) {
            return context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).getString(key, defValue);
        } else {
            return defValue;
        }
    }

    public static boolean getPrefBooolean(String key, boolean defValue, Context context) {
        if (context != null) {
            return context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(key, defValue);
        } else {
            return defValue;
        }
    }

    public static int getPrefInt(String key, int defValue, Context context) {
        if (context != null) {
            return context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).getInt(key, defValue);
        } else {
            return defValue;
        }
    }

    public static long getPrefLong(String key, long defValue, Context context) {
        if (context != null) {
            return context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).getLong(key, defValue);
        } else {
            return defValue;
        }
    }

    public static float getPrefFloat(String key, float defValue, Context context) {
        if (context != null) {
            return context.getSharedPreferences(Configuration.SHARED_PREFS, Context.MODE_PRIVATE).getFloat(key, defValue);
        } else {
            return defValue;
        }
    }
}
