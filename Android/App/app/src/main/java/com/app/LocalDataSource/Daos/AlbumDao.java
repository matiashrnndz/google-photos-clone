package com.app.LocalDataSource.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.LocalDataSource.Model.Album;
import com.app.LocalDataSource.Model.Photo;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AlbumDao {

    @Insert(onConflict = REPLACE)
    void add(Album item);

    @Query("SELECT * FROM albums WHERE name = :name")
    LiveData<Album> getByName(String name);

    @Query("DELETE FROM albums WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT * FROM albums")
    LiveData<List<Album>> getAll();

    @Query("DELETE FROM albums")
    void deleteAll();
}
