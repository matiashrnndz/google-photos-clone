package com.app.ViewModel;

import androidx.lifecycle.ViewModel;

import com.app.Repository.SessionRepository;

import javax.inject.Inject;

public class SignoutViewModel extends ViewModel {

    public SessionRepository sessionRepository;

    @Inject
    public SignoutViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void signout() {
        sessionRepository.signout();
    }
}
