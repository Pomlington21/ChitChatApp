package com.bluej.chitchat.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @POST("send")
    Call<String> sendMessage(

            @HeaderMap HashMap<String, String> headers,
            @Body String messageBody
    );
}
