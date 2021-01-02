package com.app.LocalDataSource.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.LocalDataSource.Model.Photo;
import com.app.LocalDataSource.Model.Tag;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhotoDao {

    @Insert(onConflict = REPLACE)
    void add(Photo item);

    @Query("SELECT * FROM photo WHERE photo.id = :id LIMIT 1")
    LiveData<Photo> getById(String id);

    @Query("SELECT * FROM photo WHERE photo.name LIKE :uid")
    LiveData<List<Photo>> getAllByName(String uid);

    @Query("DELETE FROM photo")
    void deleteAll();

    @Query("SELECT * FROM photo")
    LiveData<List<Photo>> getAll();

    @Query("SELECT id FROM photo WHERE url = :url LIMIT 1")
    String getIdByUrl(String url);

    @Query("DELETE FROM photo WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM photo WHERE albumId = :albumId")
    LiveData<List<Photo>> getAllByAlbum(String albumId);

    /*
    @Query("SELECT * FROM photo WHERE albumId=:albumId")
    LiveData<List<Photo>> getTagsByAlbumId(String albumId);
*/

    /*
    @Query("SELECT * FROM photos LEFT JOIN tags ON photos.id = tags.photoId WHERE photos.id = :id")
    LiveData<Photo> getPhotoWithTag(String id);

    @Insert(onConflict = REPLACE)
    void addTag(Tag tag);

    @Query("SELECT * FROM photos LEFT JOIN tags ON photos.id = tags.photoId WHERE photos.name LIKE :name")
    LiveData<List<Photo>> getAllByNameWithTags(String name);

    @Query("SELECT * FROM photos LEFT JOIN tags ON photos.id = tags.photoId")
    LiveData<List<Photo>> getAllWithTags();
    */
}
