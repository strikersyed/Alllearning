package com.ast.taskApp.Database;

import com.ast.taskApp.Models.Tasks;

import java.util.List;

public interface ITasksSource  {
    Tasks getTaskbyId (String taskID );
    List<Tasks> getAllTasks(String userID);
    void addAllTasks(List<Tasks> tasks);
    void insertTasks(Tasks... tasks);
    void updateTask(Tasks... tasks);
    void deleteTasks(String taskID);
    void deleteallTasks(String userID);

}
