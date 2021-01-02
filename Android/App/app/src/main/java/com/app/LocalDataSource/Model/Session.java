package com.app.LocalDataSource.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "sessions")
public class Session {

    @PrimaryKey
    @SerializedName("uid")
    @NonNull
    private String uid;

    @SerializedName("token")
    private String token;

    @Ignore
    public Session() {
    }

    /**
     * @param uid
     * @param token
     */
    public Session(String uid, String token) {
        super();
        this.uid = uid;
        this.token = token;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
