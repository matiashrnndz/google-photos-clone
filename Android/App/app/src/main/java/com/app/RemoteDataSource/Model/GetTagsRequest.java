package com.app.RemoteDataSource.Model;

public class GetTagsRequest {

    private String Id;

    private GetTagsRequest() {

    }

    /**
     * @param id
     */
    public GetTagsRequest(String id) {
        this.Id = id;
    }

    public String getId() {
        return Id;
    }

}
