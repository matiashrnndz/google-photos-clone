package com.app.LocalDataSource.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Album.class,
        parentColumns = "id",
        childColumns = "albumId",
        onDelete = CASCADE))
public class Photo {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("url")
    private String url;

    @SerializedName("location")
    private String location;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @Ignore
    private List<Tag> tags = new ArrayList<>();

    @SerializedName("albumId")
    private String albumId;

    @Ignore
    public Photo() {
    }

    public Photo(
            String id,
            String name,
            String type,
            String url,
            String location,
            String latitude,
            String longitude) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Ignore
    public Photo(
            String id,
            String name,
            String type,
            String url,
            String location,
            String latitude,
            String longitude,
            String albumId) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.albumId = albumId;
    }

    @Ignore
    public Photo(
            String id,
            String name,
            String type,
            String url,
            String location,
            String latitude,
            String longitude,
            List<Tag> tags) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = tags;
    }

    @Ignore
    public Photo(
            String id,
            String name,
            String type,
            String url,
            String location,
            String latitude,
            String longitude,
            List<Tag> tags,
            String albumId) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tags = tags;
        this.albumId = albumId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(@NonNull String albumId) {
        this.albumId = albumId;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }
}
