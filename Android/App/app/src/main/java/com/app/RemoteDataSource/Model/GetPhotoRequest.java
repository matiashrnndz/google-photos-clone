package com.app.RemoteDataSource.Model;

public class GetPhotoRequest {

    private String Id;

    private GetPhotoRequest() {

    }

    /**
     * @param id
     */
    public GetPhotoRequest(String id) {
        this.Id = id;
    }

    public String getId() {
        return Id;
    }

}
