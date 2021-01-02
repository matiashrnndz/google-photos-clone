package com.app.Repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Daos.AlbumDao;
import com.app.LocalDataSource.Daos.PhotoDao;
import com.app.LocalDataSource.Daos.TagDao;
import com.app.LocalDataSource.Model.Album;
import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Model.AlbumAddRequest;
import com.app.RemoteDataSource.NetworkBoundResource;
import com.app.RemoteDataSource.Resource;
import com.app.RemoteDataSource.Services.AlbumService;
import com.app.Utilities.AbsentLiveData;
import com.app.Utilities.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AlbumRepository {

    private final AppExecutors appExecutors;
    private final AlbumService albumService;
    private final AlbumDao albumDao;
    private final PhotoDao photoDao;
    private final TagDao tagDao;
    private static List<Album> lastAlbums = new ArrayList<>();
    private final AuthInterceptor authInterceptor;

    @Inject
    public AlbumRepository(AppExecutors appExecutors, AlbumService albumService, AlbumDao albumDao, PhotoDao photoDao, TagDao tagDao, AuthInterceptor authInterceptor) {
        this.appExecutors = appExecutors;
        this.albumService = albumService;
        this.albumDao = albumDao;
        this.photoDao = photoDao;
        this.tagDao = tagDao;
        this.authInterceptor = authInterceptor;
    }



    public LiveData<Resource<Album>> add(AlbumAddRequest request) {
        return new NetworkBoundResource<Album, Album>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<Album> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return albumDao.getByName(request.getName());
            }

            @Override
            protected boolean shouldFetch(@Nullable Album data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Album>> createCall() {
                Timber.d("Creating call to REST Api");
                return albumService.add(request.getName(), request.getTags());
            }

            @Override
            protected void saveCallResult(@NonNull Album data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    albumDao.add(data);
                    if (data.getPhotos() != null) {
                        for (int i = 0; i < data.getPhotos().size(); i++) {
                            Photo photo = data.getPhotos().get(i);
                            photo.setAlbumId(data.getId());
                            photoDao.add(photo);
                            for (int j = 0; j < photo.getTags().size(); j++) {
                                Tag tag = photo.getTags().get(j);
                                tag.setPhotoId(photo.getId());
                                tagDao.insert(tag);
                            }
                        }
                    }
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public void delete(String id) {
        Call<Void> call = albumService.delete(id);

        appExecutors.diskIO().execute(() -> albumDao.deleteById(id));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public LiveData<Resource<List<Album>>> getAll() {
        return new NetworkBoundResource<List<Album>, List<Album>>(appExecutors) {

            @Override
            protected LiveData<List<Album>> loadFromDb() {
                return AbsentLiveData.create(lastAlbums);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Album> data) {
                Timber.d("Always fetch.");
                return true;
            }

            @Override
            protected LiveData<ApiResponse<List<Album>>> createCall() {
                Timber.d("Creating call to REST Api");
                return albumService.getAll();
            }

            @Override
            protected void saveCallResult(@NonNull List<Album> data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    lastAlbums.clear();
                    for (int i = 0; i < data.size(); i ++) {
                        Album album = data.get(i);
                        albumDao.add(album);
                        if (album.getPhotos() != null) {
                            for (int j = 0; j < album.getPhotos().size(); j++) {
                                Photo photo = album.getPhotos().get(j);
                                photoDao.add(photo);
                                for (int g = 0; g < photo.getTags().size(); g++) {
                                    Tag tag = photo.getTags().get(g);
                                    tag.setPhotoId(photo.getId());
                                    tagDao.insert(tag);
                                }
                            }
                            lastAlbums.add(album);
                        }
                    }
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<Photo>>> getAllByAlbum(String albumId) {
        return new NetworkBoundResource<List<Photo>, List<Photo>>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<List<Photo>> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                LiveData<List<Photo>> toReturn = AbsentLiveData.create();
                for (int i = 0; i < lastAlbums.size(); i++) {
                    if (lastAlbums.get(i).getId().equals(albumId)) {
                        toReturn = AbsentLiveData.create(lastAlbums.get(i).getPhotos());
                    }
                }
                return toReturn;
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Photo> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Photo>>> createCall() {
                Timber.d("Creating call to REST Api");
                return null;
            }

            @Override
            protected void saveCallResult(@io.reactivex.annotations.NonNull List<Photo> data) {
                Timber.d("Save user info to cache.");
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public void clear() {
        appExecutors.diskIO().execute(() -> albumDao.deleteAll());
    }

}
