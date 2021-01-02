package com.app.RemoteDataSource.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchByTagsRequest {

    private ArrayList<String> tags;

    private SearchByTagsRequest() {

    }

    /**
     * @param tags
     */
    public SearchByTagsRequest(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

}
