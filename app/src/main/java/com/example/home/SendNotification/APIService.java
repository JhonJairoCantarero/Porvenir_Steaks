package com.example.home.SendNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=BMZ6WlYI1zFCh18gpugKJHbQDsmvnB0hnmsa-zd5O87Sj1TsBQQUBc75LME3fZ55mkhYnSMmgb6VFtND4mFY2kg"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}
