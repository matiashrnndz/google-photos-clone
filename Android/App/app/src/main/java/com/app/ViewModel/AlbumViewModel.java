package com.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.app.LocalDataSource.Model.Album;
import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.Model.AlbumAddRequest;
import com.app.RemoteDataSource.NetworkBoundResource;
import com.app.RemoteDataSource.Resource;
import com.app.Repository.AlbumRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class AlbumViewModel extends ViewModel {

    public AlbumRepository albumRepository;

    @Inject
    public AlbumViewModel(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    /* Add Album */

    public MutableLiveData<Resource<AlbumAddRequest>> albumAddRequest = new MutableLiveData<>();
    public LiveData<Resource<Album>> albumAddResponse = Transformations.switchMap(albumAddRequest,
            (input) -> albumAddResponse = albumRepository.add(input.data)
    );

    public void add(String name, List<Tag> tags) {
        List<String> tagNames = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i).getName().trim().toLowerCase();
            String firstCharLowerCase = Character.toString(tag.charAt(0));
            String firstCharUpperCase = Character.toString(tag.charAt(0)).toUpperCase();
            tag = tag.replaceFirst(firstCharLowerCase, firstCharUpperCase);
            tagNames.add(tag);
        }
        AlbumAddRequest request = new AlbumAddRequest(name, tagNames);
        Resource<AlbumAddRequest> resource = new Resource<>(null, request, null);
        albumAddRequest.setValue(resource);
        albumRepository.add(request);
    }

    /* Delete Album */

    public void delete(String id) {
        albumRepository.delete(id);
    }

    /* Get All Albums */

    private MutableLiveData<Resource<List<Album>>> getAllRequest = new MutableLiveData<>();
    public LiveData<Resource<List<Album>>> getAllResponse = Transformations.switchMap(getAllRequest,
            (input) -> getAllResponse = albumRepository.getAll()
    );

    public void getAll() {
        List<Album> request = new ArrayList<>();
        Resource<List<Album>> resource = new Resource<>(null, request, null);
        getAllRequest.setValue(resource);
        albumRepository.getAll();
    }

    /* Get All By Album */

    private MutableLiveData<Resource<String>> getAllByAlbumRequest = new MutableLiveData<>();
    public LiveData<Resource<List<Photo>>> getAllByAlbumResponse = Transformations.switchMap(getAllByAlbumRequest,
            (input) -> getAllByAlbumResponse = albumRepository.getAllByAlbum(input.data)
    );

    public void getAllByAlbum(String albumId) {
        String request = albumId;
        Resource<String> resource = new Resource<>(null, request, null);
        getAllByAlbumRequest.setValue(resource);
        albumRepository.getAllByAlbum(request);
    }

    /* Clear Albums from Cache */

    public void clear() {
        albumRepository.clear();
    }
}
