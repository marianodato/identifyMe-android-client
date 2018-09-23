package com.example.marianodato.identifyme_android_client.remote;

import com.example.marianodato.identifyme_android_client.model.User;
import com.example.marianodato.identifyme_android_client.model.UserLogin;
import com.example.marianodato.identifyme_android_client.model.UserResults;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("sessions/")
    Call<UserLogin> doLogin(@Body UserLogin userLogin);

    @DELETE("sessions/")
    Call<UserLogin> doLogout(@Query("accessToken") String accessToken);

    @GET("users/")
    Call<UserResults> getUsers(@Query("accessToken") String accessToken, @Query("offset") int offset, @Query("limit") int limit, @Query("fingerprintStatus") String filterByOption, @Query("order") String orderOption, @Query("sortBy") String sortByOption);

    @POST("users/")
    Call<User> addUser(@Query("accessToken") String accessToken, @Body User user);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") long id, @Query("accessToken") String accessToken, @Body User user);

    @DELETE("users/{id}")
    Call<User> deleteUser(@Path("id") long id, @Query("accessToken") String accessToken);
}
