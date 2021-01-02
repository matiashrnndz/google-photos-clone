package com.app.Repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Daos.PhotoDao;
import com.app.LocalDataSource.Daos.TagDao;
import com.app.LocalDataSource.Model.Photo;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Model.AddTagRequest;
import com.app.RemoteDataSource.Model.GetPhotoRequest;
import com.app.RemoteDataSource.Model.GetTagsRequest;
import com.app.RemoteDataSource.Model.PhotoAddRequest;
import com.app.RemoteDataSource.Model.SearchByTagsRequest;
import com.app.RemoteDataSource.NetworkBoundResource;
import com.app.RemoteDataSource.Resource;
import com.app.RemoteDataSource.Services.PhotoService;
import com.app.Utilities.AppExecutors;

import java.util.List;
import com.app.LocalDataSource.Model.Tag;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

@Singleton
public class PhotoRepository {

    private final AppExecutors appExecutors;
    private final PhotoService photoService;
    private final PhotoDao photoDao;
    private final TagDao tagDao;
    private final AuthInterceptor authInterceptor;
    private String photoId = "prueba";

    @Inject
    public PhotoRepository(AppExecutors appExecutors, PhotoService photoService, PhotoDao photoDao, TagDao tagDao, AuthInterceptor authInterceptor) {
        this.appExecutors = appExecutors;
        this.photoService = photoService;
        this.photoDao = photoDao;
        this.tagDao = tagDao;
        this.authInterceptor = authInterceptor;
    }

    public LiveData<Resource<Photo>> photoAdd(PhotoAddRequest request) {
        return new NetworkBoundResource<Photo, Photo>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<Photo> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return photoDao.getById(photoId);
            }

            @Override
            protected boolean shouldFetch(@Nullable Photo data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Photo>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.photoAdd(request);
            }

            @Override
            protected void saveCallResult(@NonNull Photo data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    photoId = data.getId();
                    photoDao.add(data);
                    if (data.getTags() != null) {
                        for (int j = 0; j < data.getTags().size(); j++) {
                            Tag tag = data.getTags().get(j);
                            tag.setPhotoId(data.getId());
                            tagDao.insert(tag);
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

    public LiveData<Resource<List<Photo>>> getAll() {
        return new NetworkBoundResource<List<Photo>, List<Photo>>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<List<Photo>> loadFromDb() {
                return photoDao.getAllByName(authInterceptor.getUid() + "%");
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Photo> data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Photo>>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.getAll();
            }

            @Override
            protected void saveCallResult(@NonNull List<Photo> data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        photoDao.add(data.get(i));
                        if (data.get(i).getTags() != null) {
                            for (int j = 0; j < data.get(i).getTags().size(); j++) {
                                Tag tag = data.get(i).getTags().get(j);
                                tag.setPhotoId(data.get(i).getId());
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

    public LiveData<Resource<List<Photo>>> searchByTags(SearchByTagsRequest request) {
        return new NetworkBoundResource<List<Photo>, List<Photo>>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<List<Photo>> loadFromDb() {
                return photoDao.getAll();
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Photo> data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Photo>>> createCall() {
                Timber.d("Creating call to REST Api");
                LiveData<ApiResponse<List<Photo>>> photos = photoService.searchByTags(request.getTags());
                return photos;
            }

            @Override
            protected void saveCallResult(@NonNull List<Photo> data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        photoDao.add(data.get(i));
                        if (data.get(i).getTags() != null) {
                            for (int j = 0; j < data.get(i).getTags().size(); j++) {
                                Tag tag = data.get(i).getTags().get(j);
                                tag.setPhotoId(data.get(i).getId());
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

    public void clear() {
        appExecutors.diskIO().execute(() -> photoDao.deleteAll());
    }

    public void delete(String id) {
        Call<Void> call = photoService.delete(id);

        appExecutors.diskIO().execute(() -> photoDao.deleteById(id));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public LiveData<Resource<Photo>> getPhoto(GetPhotoRequest request) {
        return new NetworkBoundResource<Photo, Photo>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<Photo> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return photoDao.getById(request.getId());
            }

            @Override
            protected boolean shouldFetch(@Nullable Photo data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Photo>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.get(request.getId());
            }

            @Override
            protected void saveCallResult(@NonNull Photo data) {
                Timber.d("Save photo info to cache.");
                if (data != null) {
                    photoDao.add(data);
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<Tag>>> getTags(GetTagsRequest request) {
        return new NetworkBoundResource<List<Tag>, Photo>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<List<Tag>> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return tagDao.getTagsByPhotoId(request.getId());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Tag> data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Photo>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.get(request.getId());
            }

            @Override
            protected void saveCallResult(@NonNull Photo data) {
                Timber.d("Save photo info to cache.");
                if (data != null) {
                    photoDao.add(data);
                    if (data.getTags() != null) {
                        for (int i = 0; i < data.getTags().size(); i ++) {
                            tagDao.insert(data.getTags().get(i));
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

    public void deleteTag(String id, String tagId) {
        Call<Void> call = photoService.deleteTag(id, tagId);

        appExecutors.diskIO().execute(() -> tagDao.delete(tagId));

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public LiveData<Resource<Photo>> tagAdd(AddTagRequest request) {
        return new NetworkBoundResource<Photo, Photo>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<Photo> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return photoDao.getById(request.getPhotoId());
            }

            @Override
            protected boolean shouldFetch(@Nullable Photo data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Photo>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.addTag(request.getPhotoId(), request.getTagName());
            }

            @Override
            protected void saveCallResult(@NonNull Photo data) {
                Timber.d("Save user info to cache.");
                if (data != null) {
                    photoId = data.getId();
                    photoDao.add(data);
                    if (data.getTags() != null) {
                        for (int j = 0; j < data.getTags().size(); j++) {
                            Tag tag = data.getTags().get(j);
                            tag.setPhotoId(data.getId());
                            tagDao.insert(tag);
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
                return photoDao.getAllByAlbum(albumId);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Photo> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Photo>>> createCall() {
                Timber.d("Creating call to REST Api");
                return photoService.getAll();
            }

            @Override
            protected void saveCallResult(@NonNull List<Photo> data) {
                Timber.d("Save user info to cache.");
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }
}
