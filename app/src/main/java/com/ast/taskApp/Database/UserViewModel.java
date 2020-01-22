package com.ast.taskApp.Database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ast.taskApp.Models.Users;

public class UserViewModel extends AndroidViewModel {
    private UserDataSource userDataSource;
    private TaskappDatabase db;
    public UserViewModel(@NonNull Application application) {
        super(application);
        db = TaskappDatabase.getTaskappDatabase(application);
        this.userDataSource = UserDataSource.userDataSource(db.userDao());

    }
    public void insert(Users user){
        userDataSource.insertUser(user);
    }
    public void update(Users user){
        userDataSource.updateUser(user);
    }

    public Users getUserByID(String ID){
        return userDataSource.getUserById(ID);
    }
    public LiveData<Users> getLiveUserUpdate(String id){
        return userDataSource.getLiveUserUpdate(id);
    }
}
