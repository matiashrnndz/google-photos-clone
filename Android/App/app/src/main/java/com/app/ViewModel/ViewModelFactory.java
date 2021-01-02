package com.app.ViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.app.Repository.AlbumRepository;
import com.app.Repository.PhotoRepository;
import com.app.Repository.SessionRepository;
import com.app.Repository.UserRepository;

import javax.inject.Inject;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private PhotoRepository photoRepository;
    private AlbumRepository albumRepository;

    @Inject
    public ViewModelFactory(SessionRepository sessionRepository, UserRepository userRepository, PhotoRepository photoRepository, AlbumRepository albumRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.photoRepository = photoRepository;
        this.albumRepository = albumRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignupViewModel.class)) {
            return (T) new SignupViewModel(sessionRepository);
        } else if (modelClass.isAssignableFrom(SigninViewModel.class)) {
            return (T) new SigninViewModel(sessionRepository);
        } else if (modelClass.isAssignableFrom(SignoutViewModel.class)) {
            return (T) new SignoutViewModel(sessionRepository);
        } else if (modelClass.isAssignableFrom(EditAccountViewModel.class)) {
            return (T) new EditAccountViewModel(userRepository);
        } else if (modelClass.isAssignableFrom(PhotoViewModel.class)) {
            return (T) new PhotoViewModel(photoRepository);
        } else if (modelClass.isAssignableFrom(AlbumViewModel.class)) {
            return (T) new AlbumViewModel(albumRepository);
        }

        throw new IllegalArgumentException("Unknown class name");
    }

}
