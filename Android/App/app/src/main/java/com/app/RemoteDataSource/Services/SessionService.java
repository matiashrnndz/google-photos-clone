package com.app.RemoteDataSource.Services;

import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Model.Session;
import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Model.SigninRequest;
import com.app.RemoteDataSource.Model.SignupRequest;
import com.app.RemoteDataSource.Urls;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SessionService {

    @Headers({"Content-Type: application/json",
            AuthInterceptor.NO_AUTHORIZATION_VALUE})
    @POST(Urls.SIGNIN)
    LiveData<ApiResponse<Session>> signin(
            @Body SigninRequest body
    );

    @Headers({"Content-Type: application/json",
            AuthInterceptor.NO_AUTHORIZATION_VALUE})
    @POST(Urls.SIGNUP)
    LiveData<ApiResponse<User>> signup(
            @Body SignupRequest body
    );
}
