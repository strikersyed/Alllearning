package com.ast.taskApp.Database;

import com.ast.taskApp.Models.Tasks;

import java.util.List;

public class TaskRepo implements ITasksSource {
    private ITasksSource localstorage;
    public static TaskRepo tskinstance;



    public TaskRepo(ITasksSource localstorage) {
        this.localstorage = localstorage;
    }


    public static TaskRepo getTskinstance(ITasksSource localstorage) {
        if (tskinstance==null){
            tskinstance = new TaskRepo(localstorage);
        }
        return tskinstance;
    }
    @Override
    public List<Tasks> getAllTasks(String userID) {
        return localstorage.getAllTasks(userID);
    }

    @Override
    public void addAllTasks(List<Tasks> tasks) {
        localstorage.addAllTasks(tasks);
    }

    @Override
    public Tasks getTaskbyId(String id) {
        return localstorage.getTaskbyId(id);
    }

    @Override
    public void insertTasks(Tasks... tasks) {
        localstorage.insertTasks(tasks);
    }

    @Override
    public void updateTask(Tasks... tasks) {
        localstorage.updateTask(tasks);
    }

    @Override
    public void deleteTasks(String id) {
        localstorage.deleteTasks(id);
    }

    @Override
    public void deleteallTasks(String userID) {
        localstorage.deleteallTasks(userID);
    }
}
