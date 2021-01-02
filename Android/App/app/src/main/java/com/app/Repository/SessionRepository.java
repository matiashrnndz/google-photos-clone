package com.app.Repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Daos.SessionDao;
import com.app.LocalDataSource.Daos.UserDao;
import com.app.LocalDataSource.Model.Session;
import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Model.SigninRequest;
import com.app.RemoteDataSource.Model.SignupRequest;
import com.app.RemoteDataSource.NetworkBoundResource;
import com.app.RemoteDataSource.Resource;
import com.app.RemoteDataSource.Services.SessionService;
import com.app.Utilities.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.annotations.NonNull;
import timber.log.Timber;

@Singleton
public class SessionRepository {

    private final AppExecutors appExecutors;
    private final SessionService sessionService;
    private final SessionDao sessionDao;
    private final UserDao userDao;
    private final AuthInterceptor authInterceptor;

    @Inject
    public SessionRepository(AppExecutors appExecutors, SessionService sessionService, SessionDao sessionDao, UserDao userDao, AuthInterceptor authInterceptor) {
        this.appExecutors = appExecutors;
        this.sessionService = sessionService;
        this.sessionDao = sessionDao;
        this.userDao = userDao;
        this.authInterceptor = authInterceptor;
    }

    public LiveData<Resource<User>> signup(final SignupRequest request) {
        return new NetworkBoundResource<User, User>(appExecutors) {

            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                Timber.d("Load from database.");
                return userDao.getUserByEmail(request.getEmail());
            }

            @Override
            protected boolean shouldFetch(@Nullable User data) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Timber.d("Creating call to REST Api");
                return sessionService.signup(request);
            }

            @Override
            protected void saveCallResult(@NonNull User item) {
                Timber.d("Save user info to cache.");
                if (item != null) {
                    userDao.addUser(item);
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public LiveData<Resource<Session>> signin(final SigninRequest request) {
        return new NetworkBoundResource<Session, Session>(appExecutors) {

            @NonNull
            @Override
            protected LiveData<Session> loadFromDb() {
                Timber.d("Load from database.");
                return sessionDao.getSessionByUid(request.getEmail());
            }

            @Override
            protected boolean shouldFetch(@Nullable Session data) {
                Timber.d("Always fetch.");
                return true;
            }

            @Override
            protected void processData(Session session) {
                if (session != null) {
                    authInterceptor.setToken(session.getToken());
                    authInterceptor.setUid(session.getUid());
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Session>> createCall() {
                Timber.d("Creating call to REST Api");
                return sessionService.signin(request);
            }

            @Override
            protected void saveCallResult(@NonNull Session item) {
                Timber.d("Save info to cache.");
                if (item != null) {
                    sessionDao.addSession(item);
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }

    public void signout() {
        Timber.d("Logging out");
        appExecutors.diskIO().execute(() -> sessionDao.deleteSession());
        authInterceptor.clearToken();
    }
}
