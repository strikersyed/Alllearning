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
    private String firstname;
    private String fullname;
    private String platform;
    private String profilepic;
    private Integer subscriptionType;
    private Integer isLoggedin;


    public Users(@NonNull String userID, String email, String firstname,
                 String fullname, String platform, String profilepic,
                 Integer subscriptionType, Integer isLoggedin) {
        this.userID = userID;
        this.email = email;
        this.firstname = firstname;
        this.fullname = fullname;
        this.platform = platform;
        this.profilepic = profilepic;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
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
