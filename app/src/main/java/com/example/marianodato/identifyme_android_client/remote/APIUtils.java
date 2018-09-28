package com.example.marianodato.identifyme_android_client.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.marianodato.identifyme_android_client.LoginActivity;
import com.example.marianodato.identifyme_android_client.R;
import com.example.marianodato.identifyme_android_client.utils.CommonKeys;

import org.json.JSONObject;

import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class APIUtils implements CommonKeys {

    private APIUtils(){
    }

    private static final String API_URL = "https://identifyme-backend-api.herokuapp.com/";
    private static final String API_MESSAGE = "message";

    public static UserService getUserService(){
        return RetrofitClient.getClient(API_URL).create(UserService.class);
    }

    public static void onFailureGenericLogic(Context context, Throwable t) {
        Log.e(LOG_ERROR, t.getMessage());
        Toast.makeText(context, context.getString(R.string.ERROR_GENERICO), Toast.LENGTH_LONG).show();
    }

    public static void onResponseErrorGenericLogic(Context context, Response response, boolean handleUnauthorizedResponse) {
        try {
            JSONObject jObjError = new JSONObject(response.errorBody().string());
            Log.e(LOG_ERROR, jObjError.getString(API_MESSAGE));
            Toast.makeText(context, jObjError.getString(API_MESSAGE), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(LOG_ERROR, e.getMessage());
            Toast.makeText(context, context.getString(R.string.ERROR_GENERICO), Toast.LENGTH_LONG).show();
        }

        if (handleUnauthorizedResponse && response.code() == 401) {
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
            editor.remove(ACCESS_TOKEN_KEY);
            editor.apply();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }
}
