package com.app.LocalDataSource.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "albums")
public class Album {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("name")
    @NonNull
    private String name;

    @Ignore
    private List<Photo> photos = new ArrayList<>();

    @Ignore
    public Album() {

    }

    /**
     * @param id
     * @param name
     */
    public Album(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * @param id
     * @param name
     * @param photos
     */
    public Album(String id, String name, List<Photo> photos) {
        super();
        this.id = id;
        this.name = name;
        this.photos = photos;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }
}
