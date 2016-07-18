package com.softdesig.devintensive.utils;

/**
 * @author IsakovVlad
 * @created on 24.06.16
 */

public interface ConstantManager {

    String TAG = "DEV";
    String COLOR_MODE = "";

    int EDIT_MODE_ON = 1;
    int EDIT_MODE_OFF = 0;

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
    int CALL_PHONE_REQUEST_PERMISSION_CODE = 110;

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;

    int PERMISSION_REQUEST_SETTINGS_CODE = 101;

    int CAMERA_REQUEST_PERMISSION_CODE = 102;

    String USER_AVATAR_KEY = "USER_AVATAR_KEY";

    String USER_ID_KEY = "USER_ID_KEY";
    String AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY";

    String USER_RATING_VALUE = "USER_RATING_VALUE";
    String USER_CODE_LINES_VALUE = "USER_CODE_LINES_VALUE";
    String USER_PROJECTS_VALUE = "USER_PROJECTS_VALUE";

    String USER_FIRST_NAME = "USER_FIRST_NAME";
    String USER_SECOND_NAME = "USER_SECOND_NAME";

    String USER_PHOTO_URL_KEY = "USER_PHOTO_URL_KEY";
    String USER_AVATAR_URL_KEY = "USER_AVATAR_URL_KEY";

    String PARCELABLE_KEY = "PARCELABLE_KEY";
}
