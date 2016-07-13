package com.softdesig.devintensive.data.managers;

import android.content.Context;

import com.softdesig.devintensive.data.network.RestService;
import com.softdesig.devintensive.data.network.ServiceGenerator;
import com.softdesig.devintensive.data.network.req.UserLoginReq;
import com.softdesig.devintensive.data.network.res.UserModelRes;
import com.softdesig.devintensive.utils.DevIntensiveApplication;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * @author IsakovVlad
 * @created on 28.06.16
 */

public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        this.mPreferencesManager = new PreferencesManager();
        this.mContext = DevIntensiveApplication.getContext();
        // создание REST-сервиса
        this.mRestService = ServiceGenerator.createService(RestService.class);
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

    public Context getContext() {
        return mContext;
    }

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> getImage(String url) {
        return mRestService.getImage(url);
    }

    public Call<ResponseBody> uploadPhoto(File photoFile) {
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);
        MultipartBody.Part bodyPart =
                MultipartBody.Part.createFormData("photo", photoFile.getName(), requestBody);
        return mRestService.uploadImage(bodyPart);
    }
}
