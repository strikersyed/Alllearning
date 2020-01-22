package com.ast.taskApp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ast.taskApp.Models.Tasks;

import java.util.List;

@Dao
public interface TasksDao {

    @Insert
    void insertTask(Tasks... tasks);
    @Query("SELECT * FROM Tasks WHERE taskID = :id")
    Tasks getTasksById (String id);

    @Insert
    void addAllTasks(List<Tasks> tasks);

    @Query("SELECT * FROM Tasks WHERE userID = :id ORDER BY startTime ASC")
    List<Tasks> getAllTasks(String id);

    @Query("DELETE FROM Tasks WHERE taskID == :id")
    void deleteTasksbyId (String id);
    @Update
    void updateTask(Tasks...tasks);

    @Query("DELETE FROM Tasks WHERE userID == :id")
    void deleteAllTasks(String id);

}
