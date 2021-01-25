package com.margsapp.messenger.Fragments;

import com.margsapp.messenger.Notifications.MyResponse;
import com.margsapp.messenger.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {

                    "Content-Type: application/json",
                    "Authorization: "

            }




    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);



}
