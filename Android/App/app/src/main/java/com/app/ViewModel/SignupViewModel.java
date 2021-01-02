package com.app.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.Model.SignupRequest;
import com.app.RemoteDataSource.Resource;
import com.app.Repository.SessionRepository;

import javax.inject.Inject;

public class SignupViewModel extends ViewModel {

    public SessionRepository sessionRepository;

    public MutableLiveData<Resource<SignupRequest>> signupRequest = new MutableLiveData<>();

    public LiveData<Resource<User>> signupResponse = Transformations.switchMap(signupRequest,
            (input) -> signupResponse = sessionRepository.signup(input.data)
    );

    @Inject
    public SignupViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void signup(String email, String password, String passwordConfirmation, String name, String bornDate, String mobileNumber) {
        SignupRequest request = new SignupRequest(email, password, passwordConfirmation, name, bornDate, mobileNumber);
        Resource<SignupRequest> resource = new Resource<>(null, request, null);
        signupRequest.setValue(resource);
        sessionRepository.signup(request);
    }
}
