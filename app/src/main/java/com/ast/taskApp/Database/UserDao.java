package com.ast.taskApp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ast.taskApp.Models.Users;

@Dao
public interface UserDao {
    @Insert
    void insertUser(Users... user);
    @Query("SELECT * FROM Users  WHERE userID = :id")
    Users getUserById(String id);
    @Query("SELECT * FROM Users WHERE userID = :id")
    LiveData<Users> getLiveUserUpdate(String id);
    @Update
    void updateUser(Users... user);

}
