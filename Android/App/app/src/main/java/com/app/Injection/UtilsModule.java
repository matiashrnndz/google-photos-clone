package com.app.Injection;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;

import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.app.BuildConfig;
import com.app.LocalDataSource.AppDatabase;
import com.app.LocalDataSource.Daos.AlbumDao;
import com.app.LocalDataSource.Daos.PhotoDao;
import com.app.LocalDataSource.Daos.SessionDao;
import com.app.LocalDataSource.Daos.TagDao;
import com.app.LocalDataSource.Daos.UserDao;
import com.app.LocalDataSource.Model.User;
import com.app.RemoteDataSource.AuthInterceptor;
import com.app.RemoteDataSource.Deserializer.UserDeserializer;
import com.app.RemoteDataSource.LiveDataCallAdapterFactory;
import com.app.RemoteDataSource.Services.AlbumService;
import com.app.RemoteDataSource.Services.PhotoService;
import com.app.RemoteDataSource.Services.SessionService;
import com.app.RemoteDataSource.Services.UserService;
import com.app.RemoteDataSource.Urls;
import com.app.Repository.AlbumRepository;
import com.app.Repository.PhotoRepository;
import com.app.Repository.SessionRepository;
import com.app.Repository.UserRepository;
import com.app.Utilities.AppExecutors;
import com.app.ViewModel.ViewModelFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Module
public class UtilsModule {

    @Provides
    @Singleton
    ViewModelProvider.Factory getViewModelFactory(SessionRepository sessionRepository, UserRepository userRepository, PhotoRepository photoRepository, AlbumRepository albumRepository) {
        return new ViewModelFactory(sessionRepository, userRepository, photoRepository, albumRepository);
    }

    @Provides
    @Singleton
    AppExecutors getAppExecutors() {
        return new AppExecutors();
    }

    /* Web Services */

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder().build();
            return chain.proceed(request);
        })
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);

        httpClient.addNetworkInterceptor(authInterceptor);

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(new OkHttpProfilerInterceptor());
        }

        return httpClient.build();
    }

    @Provides
    @Singleton
    AuthInterceptor provideAuthInterceptor() {
        return new AuthInterceptor();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .registerTypeAdapter(User.class, new UserDeserializer());
        //.setDateFormat("dd-m-yyyy");
        return builder.setLenient().create();
    }

    /* Services */

    @Provides
    @Singleton
    SessionService getSessionService(Retrofit retrofit) {
        return retrofit.create(SessionService.class);
    }

    @Provides
    @Singleton
    UserService getUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Provides
    @Singleton
    PhotoService getPhotoService(Retrofit retrofit) {
        return retrofit.create(PhotoService.class);
    }

    @Provides
    @Singleton
    AlbumService getAlbumService(Retrofit retrofit) {
        return retrofit.create(AlbumService.class);
    }

    /* Daos */

    @Provides
    @Singleton
    AppDatabase provideDatabase(Application app) {
        return Room.databaseBuilder(app, AppDatabase.class, "mydatabase2.db").build();
    }

    @Provides
    @Singleton
    SessionDao provideSessionDao(AppDatabase database) {
        return database.sessionDao();
    }

    @Provides
    @Singleton
    UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

    @Provides
    @Singleton
    PhotoDao providePhotoDao(AppDatabase database) {
        return database.photoDao();
    }

    @Provides
    @Singleton
    TagDao provideTagDao(AppDatabase database) {
        return database.tagDao();
    }

    @Provides
    @Singleton
    AlbumDao provideAlbumDao(AppDatabase database) {
        return database.albumDao();
    }

    /* Locks the device window in portrait mode */

    public static void lockOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
