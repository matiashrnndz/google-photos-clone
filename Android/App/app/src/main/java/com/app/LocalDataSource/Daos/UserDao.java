package com.app.LocalDataSource.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.LocalDataSource.Model.User;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {

    @Insert(onConflict = REPLACE)
    void addUser(User user);

    @Query("SELECT * FROM users WHERE users.email = :email")
    LiveData<User> getUserByEmail(String email);
}
