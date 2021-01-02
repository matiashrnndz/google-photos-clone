package com.app.LocalDataSource.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.LocalDataSource.Model.Tag;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TagDao {

    @Insert(onConflict = REPLACE)
    void insert(Tag tag);

    @Query("SELECT * FROM tag WHERE photoId=:photoId")
    LiveData<List<Tag>> getTagsByPhotoId(String photoId);

    @Query("DELETE FROM tag WHERE id = :id")
    void delete(String id);
}
