package com.app.RemoteDataSource.Services;

import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Model.Photo;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.Model.PhotoAddRequest;
import com.app.RemoteDataSource.Urls;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PhotoService {

    @Headers("Content-Type: application/json")
    @POST(Urls.PHOTO_ADD)
    LiveData<ApiResponse<Photo>> photoAdd(
            @Body PhotoAddRequest body
    );

    @Headers("Accept: application/json")
    @GET(Urls.PHOTO_GETALL)
    LiveData<ApiResponse<List<Photo>>> getAll();

    @Headers("Content-Type: application/json")
    @GET(Urls.PHOTO_SEARCH_BY_TAGS)
    LiveData<ApiResponse<List<Photo>>> searchByTags(
            @Query("tag") List<String> tags
    );

    @Headers("Content-Type: application/json")
    @DELETE(Urls.PHOTO_DELETE)
    Call<Void> delete(
            @Query("id") String id
    );

    @Headers("Content-Type: application/json")
    @DELETE(Urls.PHOTO_GET)
    LiveData<ApiResponse<Photo>> get(
            @Query("id") String id
    );

    @Headers("Content-Type: application/json")
    @DELETE(Urls.TAG_DELETE)
    Call<Void> deleteTag(
            @Query("id") String id,
            @Query("tagId") String tagId
    );

    @Headers("Content-Type: application/json")
    @POST("photos/{id}/tags")
    LiveData<ApiResponse<Photo>> addTag(
            @Path("id") String id,
            @Query("tag") String tag
    );
}
