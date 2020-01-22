package com.ast.taskApp.Database;

import android.os.AsyncTask;

import com.ast.taskApp.Models.Tasks;

import java.util.List;

public class TasksDataSource implements ITasksSource {
    private TasksDao tasksDao;
    public static TasksDataSource source;

    public TasksDataSource(TasksDao tasksDao) {
        this.tasksDao = tasksDao;
    }
    public static TasksDataSource tasksDataSource (TasksDao tasksDao){
        if (source == null){
            source = new TasksDataSource(tasksDao);
        }
        return source;
    }


    @Override
    public Tasks getTaskbyId(String id) {
        return tasksDao.getTasksById(id);
    }

    @Override
    public List<Tasks> getAllTasks(String userID) {
        return tasksDao.getAllTasks(userID);
    }

    @Override
    public void addAllTasks(List<Tasks> tasks) {
        new InsertAllTaskAsyncTask(tasksDao).execute(tasks);
    }

    @Override
    public void insertTasks(Tasks... tasks) {
        new InsertTaskAsyncTask(tasksDao).execute(tasks);

    }

    @Override
    public void updateTask(Tasks... tasks) {
        new UpDateTaskAsyncTask(tasksDao).execute(tasks);
    }

    @Override
    public void deleteTasks(String id) {
        new DeleteTaskAsyncTask(tasksDao).execute(id);

    }

    @Override
    public void deleteallTasks(String userID) {
        new DeleteAllTaskAsyncTask(tasksDao).execute(userID);
    }

    private static class InsertTaskAsyncTask extends AsyncTask<Tasks, Void, Void> {
        private TasksDao tasksDao;
        private InsertTaskAsyncTask(TasksDao tasksDao){
            this.tasksDao = tasksDao;
        }

        @Override
        protected Void doInBackground(Tasks... tasks) {
            tasksDao.insertTask(tasks[0]);
            return null;
        }

    }

    private static class InsertAllTaskAsyncTask extends AsyncTask<List<Tasks>, Void, Void> {
        private TasksDao tasksDao;
        private InsertAllTaskAsyncTask(TasksDao tasksDao){
            this.tasksDao = tasksDao;
        }

        @Override
        protected Void doInBackground(List<Tasks>... tasks) {
            tasksDao.addAllTasks(tasks[0]);
            return null;
        }

    }


    private static class UpDateTaskAsyncTask extends AsyncTask<Tasks, Void, Void> {
        private TasksDao tasksDao;
        private UpDateTaskAsyncTask(TasksDao tasksDao){
            this.tasksDao = tasksDao;
        }
        @Override
        protected Void doInBackground(Tasks... tasks) {
            tasksDao.updateTask(tasks[0]);
            return null;
        }
    }

    private static class DeleteTaskAsyncTask extends AsyncTask<String,Void,Void>{
        private TasksDao tasksDao;
        private DeleteTaskAsyncTask(TasksDao tasksDao){
            this.tasksDao = tasksDao;
        }
        @Override
        protected Void doInBackground(String... tasks) {
            tasksDao.deleteTasksbyId(tasks[0]);
            return null;
        }
    }

    private static class DeleteAllTaskAsyncTask extends AsyncTask<String,Void,Void>{
        private TasksDao tasksDao;
        private DeleteAllTaskAsyncTask(TasksDao tasksDao){
            this.tasksDao = tasksDao;
        }
        @Override
        protected Void doInBackground(String... tasks) {
            tasksDao.deleteTasksbyId(tasks[0]);
            return null;
        }
    }
}
