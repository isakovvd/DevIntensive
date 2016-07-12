package com.softdesig.devintensive.data.network;

import com.softdesig.devintensive.data.network.req.UserLoginReq;
import com.softdesig.devintensive.data.network.res.UserModelRes;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * @author IsakovVlad
 * @created on 12.07.16
 */

public class RestService {

    @POST("login")
    public Call<UserModelRes> loginUser(@Body UserLoginReq req) {
        return null;
    }

    @Multipart
    @POST("profile/edit")
    public Call<ResponseBody> uploadImage(@Part MultipartBody.Part file) {
        return null;
    }

    @GET
    public Call<ResponseBody> getImage(@Url String url) {
        return null;
    }
}
