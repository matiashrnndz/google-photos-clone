package com.app.RemoteDataSource.Services;

import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Model.Album;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.Urls;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AlbumService {

    @Headers("Content-Type: application/json")
    @POST(Urls.ALBUM_ADD)
    LiveData<ApiResponse<Album>> add(
            @Query("name") String name,
            @Query("tag") List<String> tags
    );

    @Headers("Content-Type: application/json")
    @GET(Urls.ALBUM_GETALL)
    LiveData<ApiResponse<List<Album>>> getAll(
    );

    @Headers("Content-Type: application/json")
    @DELETE("albums/{id}")
    Call<Void> delete(
            @Path("id") String id
    );


}
