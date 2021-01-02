package com.app.RemoteDataSource.Services;

import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.Model.UserUpdateRequest;
import com.app.RemoteDataSource.Urls;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PUT;

public interface UserService {

    @Headers("Content-Type: application/json")
    @PUT(Urls.USER_UPDATE)
    LiveData<ApiResponse<User>> userUpdate(
            @Body UserUpdateRequest body
    );
}
