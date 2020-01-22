package com.ast.taskApp.Database;

import androidx.lifecycle.LiveData;

import com.ast.taskApp.Models.Users;

public interface IUserSource {
    Users getUserById(String id);
    LiveData<Users> getLiveUserUpdate(String id);
    void insertUser(Users... user);
    void updateUser(Users... user);
}
