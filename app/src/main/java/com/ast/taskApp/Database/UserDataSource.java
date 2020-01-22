package com.ast.taskApp.Database;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.ast.taskApp.Models.Users;

public class UserDataSource implements IUserSource {
    private UserDao userDao;
    private static UserDataSource source;
    public UserDataSource(UserDao userDao) {
        this.userDao = userDao;
    }
    public static UserDataSource userDataSource(UserDao userDao){
        if (source== null){
            source= new UserDataSource(userDao);
        }
        return  source;
    }
    @Override
    public Users getUserById(String userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public LiveData<Users> getLiveUserUpdate(String id) {
        return userDao.getLiveUserUpdate(id);
    }
    @Override
    public void insertUser(Users... user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    @Override
    public void updateUser(Users... user) {
        new UpDateUserAsyncTask(userDao).execute(user);
    }


    private static class InsertUserAsyncTask extends AsyncTask<Users, Void, Void> {
        private UserDao userDao;
        private InsertUserAsyncTask(UserDao userDao){
            this.userDao = userDao;
        }
        @Override
        protected Void doInBackground(Users... userModels) {
            userDao.insertUser(userModels[0]);
            return null;
        }
    }
    private static class UpDateUserAsyncTask extends AsyncTask<Users, Void, Void> {
            private UserDao userDao;
            private UpDateUserAsyncTask(UserDao userDao){
                this.userDao = userDao;
            }
            @Override
            protected Void doInBackground(Users... user) {
                 userDao.updateUser(user[0]);
                 return null;
            }
          }

}
