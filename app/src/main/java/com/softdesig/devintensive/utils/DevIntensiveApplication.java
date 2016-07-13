package com.softdesig.devintensive.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author IsakovVlad
 * @created on 28.06.16
 */

public class DevIntensiveApplication extends Application {

    public static SharedPreferences sSharedPreferences;
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    public static Context getContext() {
        return sContext;
    }
}
