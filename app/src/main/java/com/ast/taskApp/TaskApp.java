package com.ast.taskApp;

import android.app.Application;

import com.ast.taskApp.Database.TaskRepo;
import com.ast.taskApp.Database.TaskappDatabase;
import com.ast.taskApp.Database.TasksDataSource;
import com.ast.taskApp.Database.UserDataSource;
import com.ast.taskApp.Database.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TaskApp extends Application {

    private TaskappDatabase tdb;
    private static UserRepo userRepo;
    private static TaskRepo taskRepo;

    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static FirebaseUser user;
    private static StorageReference storage;

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

}
