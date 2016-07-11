package com.softdesig.devintensive.utils;

/**
 * @author IsakovVlad
 * @created on 24.06.16
 */

public interface ConstantManager {

    String TAG = "DEV";
    String COLOR_MODE = "";

    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_PHONE_KEY";
    String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    String USER_VK_KEY = "USER_VK_KEY";
    String USER_GIT_KEY = "USER_GIT_KEY";
    String USER_BIO_KEY = "USER_BIO_KEY";
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";

    int USER_PHONE_ET = 0;
    int USER_EMAIL_ET = 1;
    int USER_VK_ET = 2;
    int USER_GIT_ET = 3;

    int USER_PHONE_IV = 0;
    int USER_EMAIL_IV = 1;
    int USER_VK_IV = 2;
    int USER_GIT_IV = 3;

    int LOAD_PROF_PHOTO = 1;
    int CAMERA_REQUEST = 50;
    int PIC_REQUEST = 60;
    int PERMISSION_REQUEST_SETTINGS_CODE = 70;
    int CAMERA_REQUEST_PERMISSION_CODE = 100;
    int CALL_PHONE_REQUEST_PERMISSION_CODE = 110;
}
