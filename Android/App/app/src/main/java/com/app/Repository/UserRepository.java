package com.app.Repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.app.LocalDataSource.Daos.SessionDao;
import com.app.LocalDataSource.Daos.UserDao;
import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.ApiResponse;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Model.UserUpdateRequest;
import com.app.RemoteDataSource.NetworkBoundResource;
import com.app.RemoteDataSource.Resource;
import com.app.RemoteDataSource.Services.UserService;
import com.app.Utilities.AppExecutors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.annotations.NonNull;
import timber.log.Timber;

@Singleton
public class UserRepository {

    private final AppExecutors appExecutors;
    private final UserService userService;
    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final AuthInterceptor authInterceptor;

    @Inject
    public UserRepository(AppExecutors appExecutors, UserService userService, UserDao userDao, SessionDao sessionDao, AuthInterceptor authInterceptor) {
        this.appExecutors = appExecutors;
        this.userService = userService;
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.authInterceptor = authInterceptor;
    }

    public LiveData<Resource<User>> userUpdate(final UserUpdateRequest request) {
        return new NetworkBoundResource<User, User>(appExecutors) {
            @NonNull
            @Override
            protected LiveData<User> loadFromDb() {
                Timber.d("Cannot be loaded from database.");
                return userDao.getUserByEmail(authInterceptor.getUid());
            }

            @Override
            protected boolean shouldFetch(@Nullable User user) {
                Timber.d("Always fetch.");
                return true;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                Timber.d("Creating call to REST Api");
                return userService.userUpdate(request);
            }

            @Override
            protected void saveCallResult(@NonNull User user) {
                Timber.d("Save user info to cache.");
                if (user != null && !user.getEmail().equals("")) {
                    userDao.addUser(user);
                }
            }

            @Override
            protected void onFetchFailed() {
                Timber.d("Must retry fetching.");
            }

        }.asLiveData();
    }
}
