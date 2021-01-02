package com.app.LocalDataSource.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.LocalDataSource.Model.Session;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SessionDao {

    @Insert(onConflict = REPLACE)
    void addSession(Session item);

    @Query("SELECT * FROM sessions WHERE sessions.uid = :email")
    LiveData<Session> getSessionByUid(String email);

    @Query("DELETE FROM sessions")
    void deleteSession();
}

