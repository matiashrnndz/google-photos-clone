package com.app.LocalDataSource.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Photo.class,
        parentColumns = "id",
        childColumns = "photoId",
        onDelete = CASCADE))
public class Tag {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("photoId")
    @NonNull
    private String photoId;

    @SerializedName("name")
    private String name;

    @Ignore
    public Tag() {
    }

    /**
     * @param id
     * @param photoId
     * @param name
     */
    public Tag(String id, String photoId, String name) {
        super();
        this.id = id;
        this.photoId = photoId;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
