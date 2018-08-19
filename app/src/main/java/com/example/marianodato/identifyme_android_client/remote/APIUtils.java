package com.example.marianodato.identifyme_android_client.remote;

public class APIUtils {

    private APIUtils(){
    }

    public static final String API_URL = "http://localhost:8080/demo/";

    public static UserService getUserService(){
        return RetrofitClient.getClient(API_URL).create(UserService.class);
    }

}
