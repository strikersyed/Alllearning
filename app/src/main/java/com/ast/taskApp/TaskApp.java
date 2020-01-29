package com.ast.taskApp;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.ast.taskApp.Database.TaskRepo;
import com.ast.taskApp.Database.TaskappDatabase;
import com.ast.taskApp.Database.TasksDataSource;
import com.ast.taskApp.Database.UserDataSource;
import com.ast.taskApp.Database.UserRepo;
import com.ast.taskApp.Workers.TaskUpdateWorker;
import com.ast.taskApp.Workers.TaskUploadWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class TaskApp extends Application {

    private TaskappDatabase tdb;
    private static UserRepo userRepo;
    private static TaskRepo taskRepo;

    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static FirebaseUser user;
    private static StorageReference storage;
    private static WorkManager workManager;
    private static Constraints constraints;
    private static OneTimeWorkRequest taskUploadRequest;
    private static OneTimeWorkRequest taskUpdateRequest;
    private static String uploadReqID, updateReqID;

    @Override
    public void onCreate() {
        super.onCreate();

        tdb = TaskappDatabase.getTaskappDatabase(this);
        userRepo = UserRepo.getmInstance(UserDataSource.userDataSource(tdb.userDao()));
        taskRepo = TaskRepo.getTskinstance(TasksDataSource.tasksDataSource(tdb.tasksDao()));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance().getReference();
        workManager = WorkManager.getInstance();
        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();




    }

    public static UserRepo getUserRepo() {
        return userRepo;
    }

    public static TaskRepo getTaskRepo(){
        return taskRepo;
    }

    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public static StorageReference getStorage() {
        return storage;
    }

    public static WorkManager getWorkManager(){
        return workManager;
    }

    public static Constraints setConstraints(){
        return constraints;
    }

    public static WorkRequest getTaskUploadRequest(Data data){
        taskUploadRequest = new OneTimeWorkRequest.Builder(TaskUploadWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        uploadReqID = taskUploadRequest.getId().toString();
        return taskUploadRequest;
    }

    public static WorkRequest getTaskUpdateRequest(Data data){
        taskUpdateRequest = new  OneTimeWorkRequest.Builder(TaskUpdateWorker.class)
                .setConstraints(constraints)
                .setInputData(data)
                .build();
        updateReqID = taskUpdateRequest.getId().toString();
        return taskUpdateRequest;
    }

    public static UUID getUploadRequestID (){

        return UUID.fromString(uploadReqID);
    }

    public static UUID getUpdateReqID(){

        return UUID.fromString(updateReqID);
    }



}
