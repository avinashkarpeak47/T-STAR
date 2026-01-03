package com.example.t_star.STUDENTACTIVITIES;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface AIService {

    @Headers({
            "Content-Type: application/json",
            "HTTP-Referer: https://tstar.app",
            "X-Title: T-STAR"
    })
    @POST("chat/completions")
    Call<JsonObject> askAI(
            @Header("Authorization") String auth,
            @Body JsonObject body
    );
}







