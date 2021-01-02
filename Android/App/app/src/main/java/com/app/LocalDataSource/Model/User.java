package com.app.LocalDataSource.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("passwordConfirmation")
    private String passwordConfirmation;

    @SerializedName("name")
    private String name;

    @SerializedName("bornDate")
    private String bornDate;

    @SerializedName("phone")
    private String phone;

    @Ignore
    public User() {
    }

    /**
     * @param id
     * @param email
     * @param password
     * @param passwordConfirmation
     * @param name
     * @param bornDate
     * @param phone
     */
    public User(String id, String email, String password, String passwordConfirmation, String name, String bornDate, String phone) {
        super();
        this.id = id;
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.name = name;
        this.bornDate = bornDate;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
