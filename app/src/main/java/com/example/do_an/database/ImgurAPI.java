package com.example.do_an.database;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ImgurAPI {
    @FormUrlEncoded
    @POST("image")
    Call<ImgurResponse> uploadImage(
            @Header("Authorization") String auth,
            @Field("image") RequestBody image
    );
}


