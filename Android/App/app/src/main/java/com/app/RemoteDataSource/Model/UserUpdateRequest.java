package com.app.RemoteDataSource.Model;

public class UserUpdateRequest {

    private String email;
    private String password;
    private String passwordConfirmation;
    private String name;
    private String bornDate;
    private String phone;

    private UserUpdateRequest() {

    }

    /**
     * @param passwordConfirmation
     * @param phone
     * @param bornDate
     * @param name
     * @param password
     */
    public UserUpdateRequest(String name, String password, String passwordConfirmation, String bornDate, String phone) {
        super();
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.name = name;
        this.bornDate = bornDate;
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public String getName() {
        return name;
    }

    public String getBornDate() {
        return bornDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
