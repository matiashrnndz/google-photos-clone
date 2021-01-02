package com.app.ViewModel;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;
import com.app.RemoteDataSource.Model.AddTagRequest;
import com.app.RemoteDataSource.Model.GetPhotoRequest;
import com.app.RemoteDataSource.Model.GetTagsRequest;
import com.app.RemoteDataSource.Model.PhotoAddRequest;
import com.app.RemoteDataSource.Model.SearchByTagsRequest;
import com.app.RemoteDataSource.Resource;
import com.app.Repository.PhotoRepository;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PhotoViewModel extends ViewModel {

    public PhotoRepository photoRepository;

    @Inject
    public PhotoViewModel(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    /* Add Photo */

    public MutableLiveData<Resource<PhotoAddRequest>> photoAddRequest = new MutableLiveData<>();
    public LiveData<Resource<Photo>> photoAddResponse = Transformations.switchMap(photoAddRequest,
            (input) -> photoAddResponse = photoRepository.photoAdd(input.data)
    );

    public void photoAdd(Bitmap photoBitmap) {
        String encoded = encodeBitmapToBase64(photoBitmap);
        PhotoAddRequest request = new PhotoAddRequest("image/jpg", encoded);
        Resource<PhotoAddRequest> resource = new Resource<>(null, request, null);
        photoAddRequest.setValue(resource);
        photoRepository.photoAdd(request);
    }

    private String encodeBitmapToBase64(Bitmap photoBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

    /* Get All Photos */

    private MutableLiveData<Resource<List<Photo>>> getAllRequest = new MutableLiveData<>();
    public LiveData<Resource<List<Photo>>> getAllResponse = Transformations.switchMap(getAllRequest,
            (input) -> getAllResponse = photoRepository.getAll()
    );

    public void getAll() {
        List<Photo> request = new ArrayList<>();
        Resource<List<Photo>> resource = new Resource<>(null, request, null);
        getAllRequest.setValue(resource);
        photoRepository.getAll();
    }

    /* Search Photos by Tags */

    public MutableLiveData<Resource<SearchByTagsRequest>> searchByTagsRequest = new MutableLiveData<>();
    public LiveData<Resource<List<Photo>>> searchByTagsResponse = Transformations.switchMap(searchByTagsRequest,
            (input) -> searchByTagsResponse = photoRepository.searchByTags(input.data)
    );

    public void searchByTags(ArrayList<String> tags) {
        SearchByTagsRequest request = new SearchByTagsRequest(tags);
        Resource<SearchByTagsRequest> resource = new Resource<>(null, request, null);
        searchByTagsRequest.setValue(resource);
        photoRepository.searchByTags(request);
    }

    /* Get Photo's Tags */

    public MutableLiveData<Resource<GetTagsRequest>> getTagsRequest = new MutableLiveData<>();
    public LiveData<Resource<List<Tag>>> getTagsResponse = Transformations.switchMap(getTagsRequest,
            (input) -> getTagsResponse = photoRepository.getTags(input.data)
    );

    public void getTags(String photoId) {
        GetTagsRequest request = new GetTagsRequest(photoId);
        Resource<GetTagsRequest> resource = new Resource<>(null, request, null);
        getTagsRequest.setValue(resource);
        photoRepository.getTags(request);
    }

    /* Get Photo */

    public MutableLiveData<Resource<GetPhotoRequest>> getPhotoRequest = new MutableLiveData<>();
    public LiveData<Resource<Photo>> getPhotoResponse = Transformations.switchMap(getPhotoRequest,
            (input) -> getPhotoResponse = photoRepository.getPhoto(input.data)
    );

    public void getPhoto(String photoId) {
        GetPhotoRequest request = new GetPhotoRequest(photoId);
        Resource<GetPhotoRequest> resource = new Resource<>(null, request, null);
        getPhotoRequest.setValue(resource);
        photoRepository.getPhoto(request);
    }

    /* Delete Photo */

    public void delete(String id) {
        photoRepository.delete(id);
    }

    /* Clear Photos from Cache */

    public void clearPhotos() {
        photoRepository.clear();
    }

    /* Delete Tag */

    public void deleteTag(String id, String tagId) {
        photoRepository.deleteTag(id, tagId);
    }

    /* Add Tag */

    public MutableLiveData<Resource<AddTagRequest>> addTagRequest = new MutableLiveData<>();
    public LiveData<Resource<Photo>> addTagResponse = Transformations.switchMap(addTagRequest,
            (input) -> addTagResponse = photoRepository.tagAdd(input.data)
    );

    public void addTag(String photoId, String tag) {
        AddTagRequest request = new AddTagRequest(photoId, tag);
        Resource<AddTagRequest> resource = new Resource<>(null, request, null);
        addTagRequest.setValue(resource);
        photoRepository.tagAdd(request);
    }


}
