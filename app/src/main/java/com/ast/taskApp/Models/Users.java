package com.ast.taskApp.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class Users implements Serializable {


    @PrimaryKey @NonNull
    private String userID;
    private String email;
    private String firstName;
    private String fullName;
    private String platform;
    private String profilePicture;
    private Integer subscriptionType;
    private Integer isLoggedin;


    public Users(@NonNull String userID, String email,
                 String firstName, String fullName,
                 String platform, String profilePicture,
                 Integer subscriptionType, Integer isLoggedin) {
        this.userID = userID;
        this.email = email;
        this.firstName = firstName;
        this.fullName = fullName;
        this.platform = platform;
        this.profilePicture = profilePicture;
        this.subscriptionType = subscriptionType;
        this.isLoggedin = isLoggedin;
    }

    @Ignore
    public Users() {}

    @NonNull
    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Integer getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(Integer subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Integer getIsLoggedin() {
        return isLoggedin;
    }

    public void setIsLoggedin(Integer isLoggedin) {
        this.isLoggedin = isLoggedin;
    }
}
