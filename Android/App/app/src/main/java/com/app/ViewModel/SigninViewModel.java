package com.app.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.app.LocalDataSource.Model.Session;
import com.app.RemoteDataSource.Model.SigninRequest;
import com.app.RemoteDataSource.Resource;
import com.app.Repository.SessionRepository;

import javax.inject.Inject;

public class SigninViewModel extends ViewModel {

    public SessionRepository sessionRepository;

    public MutableLiveData<Resource<SigninRequest>> signinRequest = new MutableLiveData<>();

    public LiveData<Resource<Session>> signinResponse = Transformations.switchMap(signinRequest,
            (input) -> signinResponse = sessionRepository.signin(input.data)
    );

    @Inject
    public SigninViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void signin(String email, String password) {
        SigninRequest request = new SigninRequest(email, password);
        Resource<SigninRequest> resource = new Resource<>(null, request, null);
        signinRequest.setValue(resource);
        sessionRepository.signin(request);
    }
}
