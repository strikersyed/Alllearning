package com.ast.taskApp.Database;


import androidx.lifecycle.LiveData;

import com.ast.taskApp.Models.Users;


public class UserRepo implements IUserSource {
    private IUserSource localStorage;
    public static UserRepo mInstance;

    public UserRepo(IUserSource localStorage) {
        this.localStorage = localStorage;
    }
    public static UserRepo getmInstance(IUserSource localStorage){
        if (mInstance == null){
            mInstance = new UserRepo(localStorage);
        }
        return mInstance;
    }
    @Override
    public Users getUserById(String userId) {
        return localStorage.getUserById(userId);
    }

    @Override
    public LiveData<Users> getLiveUserUpdate(String id) {
        return localStorage.getLiveUserUpdate(id);
    }

    @Override
    public void insertUser(Users... user) {
        localStorage.insertUser(user);
    }

    @Override
    public void updateUser(Users... user) {
        localStorage.updateUser(user);
    }


}
