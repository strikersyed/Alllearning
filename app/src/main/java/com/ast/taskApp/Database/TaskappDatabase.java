package com.ast.taskApp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;


@Database(entities ={Users.class, Tasks.class},version = 3,exportSchema = false)
@TypeConverters({ConverterRoom.class})
public abstract class TaskappDatabase extends RoomDatabase {
    //public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "TaskAppDB";
    public abstract UserDao userDao();
    public abstract TasksDao tasksDao();
    private static TaskappDatabase mInstance;
    public static TaskappDatabase getTaskappDatabase(Context context){
        if (mInstance == null){
            mInstance = Room.databaseBuilder(context, TaskappDatabase.class,DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return mInstance;
    }
}

