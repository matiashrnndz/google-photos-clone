package com.app.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.Model.UserUpdateRequest;
import com.app.RemoteDataSource.Resource;
import com.app.Repository.UserRepository;

import javax.inject.Inject;

public class EditAccountViewModel extends ViewModel {

    public UserRepository userRepository;

    public MutableLiveData<Resource<UserUpdateRequest>> userUpdateRequest = new MutableLiveData<>();

    public LiveData<Resource<User>> userUpdateResponse = Transformations.switchMap(userUpdateRequest,
            (input) -> userUpdateResponse = userRepository.userUpdate(input.data)
    );

    @Inject
    public EditAccountViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void userUpdate(String name, String password, String passwordConfirmation, String bornDate, String phone) {
        UserUpdateRequest request = new UserUpdateRequest(name, password, passwordConfirmation, bornDate, phone);
        Resource<UserUpdateRequest> resource = new Resource<>(null, request, null);
        userUpdateRequest.setValue(resource);
        userRepository.userUpdate(request);
    }
}
