package com.app.RemoteDataSource.Model;

public class SigninRequest {

    private String email;
    private String password;

    private SigninRequest() {

    }

    /**
     * @param email
     * @param password
     */
    public SigninRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
