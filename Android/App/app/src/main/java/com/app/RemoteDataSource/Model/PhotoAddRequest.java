package com.app.RemoteDataSource.Model;

public class PhotoAddRequest {

    private String FileType;
    private String ImageBase64;

    private PhotoAddRequest() {

    }

    /**
     * @param fileType
     * @param imageBase64
     */
    public PhotoAddRequest(String fileType, String imageBase64) {
        this.FileType = fileType;
        this.ImageBase64 = imageBase64;
    }

    public String getImageBase64() {
        return ImageBase64;
    }

    public String getFileType() {
        return FileType;
    }
}
