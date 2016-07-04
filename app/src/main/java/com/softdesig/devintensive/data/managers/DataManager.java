package com.softdesig.devintensive.data.managers;

/**
 * @author IsakovVlad
 * @created on 28.06.16
 */

public class DataManager {

    private static DataManager INSTANCE = null;

    public PreferencesManager mPreferencesManager;

    public DataManager() {
        mPreferencesManager = new PreferencesManager();
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

}
