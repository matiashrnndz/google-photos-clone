package com.app.Injection;

import android.app.Application;

import com.app.View.AlbumPhotoListFragment;
import com.app.View.AlbumsFragment;
import com.app.View.EditAccountFragment;
import com.app.View.HomeActivity;
import com.app.View.NewAlbumFragment;
import com.app.View.PhotoDetailFragment;
import com.app.View.PhotoListFragment;
import com.app.View.SigninActivity;
import com.app.View.SignupActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;


@Component(modules = {AppModule.class, UtilsModule.class})
@Singleton
public interface AppComponent {

    @Component.Builder
    interface Builder {
        AppComponent build();

        @BindsInstance
        Builder application(Application application);
    }

    void doInjection(SignupActivity signupActivity);

    void doInjection(SigninActivity signinActivity);

    void doInjection(HomeActivity homeActivity);

    void doInjection(EditAccountFragment editAccountFragment);

    void doInjection(PhotoDetailFragment photoDetailFragment);

    void doInjection(NewAlbumFragment newAlbumFragment);

    void doInjection(AlbumPhotoListFragment albumPhotoListFragment);

    void doInjection(AlbumsFragment albumsFragment);

    void doInjection(PhotoListFragment photoListFragment);
}
