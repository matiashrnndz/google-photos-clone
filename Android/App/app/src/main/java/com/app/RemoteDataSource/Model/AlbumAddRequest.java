package com.app.RemoteDataSource.Model;

import java.util.List;

public class AlbumAddRequest {

    private String name;
    private List<String> tags;

    public AlbumAddRequest(String name, List<String> tags) {
        this.name = name;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

}
