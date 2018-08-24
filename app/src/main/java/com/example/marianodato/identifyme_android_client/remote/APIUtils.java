package com.example.marianodato.identifyme_android_client.remote;

public class APIUtils {

    private APIUtils(){
    }

    private static final String API_URL = "https://identifyme-backend-api.herokuapp.com/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(API_URL).create(UserService.class);
    }

}
