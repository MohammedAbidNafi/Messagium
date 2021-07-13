package com.margsapp.messenger.Notifications;

import com.margsapp.messenger.Notifications.MyResponse;
import com.margsapp.messenger.Notifications.Sender;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {

                    "Content-Type: application/json",
                    "Authorization:key=AAAAGBnO2nk:APA91bGP5Xd8vpAc9NhycTRtnJu93xBQkXF8ZkDXk7zxuFnSNUGqfmj7WSB666jIwNobI-XBDskX2XzobUMiL-T2iRcM3KjZtzGFCOqPFAJgdxGugY8Kg096KAJ9WKUKFnRcsx9b3Z-M"

            }




    )



    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

    @POST("fcm/send")
    Call<MyResponse> sendRemoteMessage(
            @Body Inviter body
    );
}
