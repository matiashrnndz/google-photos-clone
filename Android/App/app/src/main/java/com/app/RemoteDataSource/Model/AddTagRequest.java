package com.app.RemoteDataSource.Model;

public class AddTagRequest {

    private String photoId;
    private String tagName;

    public AddTagRequest(String photoId, String tagName) {
        this.photoId = photoId;
        this.tagName = tagName;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getTagName() {
        return tagName;
    }
}
