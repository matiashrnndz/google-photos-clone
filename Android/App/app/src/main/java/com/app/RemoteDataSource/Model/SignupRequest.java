package com.app.RemoteDataSource.Model;

public class SignupRequest {

    private String Email;
    private String Password;
    private String PasswordConfirmation;
    private String Name;
    private String BornDate;
    private String Phone;

    private SignupRequest() {

    }

    /**
     * @param email
     * @param password
     * @param passwordConfirmation
     * @param name
     * @param bornDate
     * @param phone
     */
    public SignupRequest(String email, String password, String passwordConfirmation, String name, String bornDate, String phone) {
        this.Email = email;
        this.Password = password;
        this.PasswordConfirmation = passwordConfirmation;
        this.Name = name;
        this.BornDate = bornDate;
        this.Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getPasswordConfirmation() {
        return PasswordConfirmation;
    }

    public String getName() {
        return Name;
    }

    public String getBornDate() {
        return BornDate;
    }

    public String getPhone() {
        return Phone;
    }
}
