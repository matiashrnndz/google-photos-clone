package com.app.LocalDataSource;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.LocalDataSource.Daos.AlbumDao;
import com.app.LocalDataSource.Daos.PhotoDao;
import com.app.LocalDataSource.Daos.SessionDao;
import com.app.LocalDataSource.Daos.TagDao;
import com.app.LocalDataSource.Daos.UserDao;
import com.app.LocalDataSource.Model.Album;
import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Session;
import com.app.LocalDataSource.Model.Tag;
import com.app.LocalDataSource.Model.User;


@Database(entities = {User.class, Session.class, Photo.class, Tag.class, Album.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SessionDao sessionDao();

    public abstract UserDao userDao();

    public abstract PhotoDao photoDao();

    public abstract TagDao tagDao();

    public abstract AlbumDao albumDao();
}
