package com.margsapp.messenger.Model;

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
                    "Authorization:key=AAAAOXhQUFI:APA91bFJhn5J8FBEdqRBKqLUOkiXgDrrquruCCfVUktgyoOtOdgQHv6jjAbkDFV6O6k_LHAYF25S7TMFxV50gH9OSXUDsfduulOczNmcNyFBIBwuR0q_8D9_QmQsQMkQx1tJDVVZItW4"

            }




    )


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);



}
