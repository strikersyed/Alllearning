package com.ast.taskApp.Workers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;


import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ast.taskApp.AlarmReceiver;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.TaskApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import static android.content.Context.ALARM_SERVICE;

public class TaskUploadWorker extends Worker {

    private String TaskID;
    private Context context;
    private Timestamp timestamp;
    private CountDownLatch countDownLatch;
    private Data resultData;
    public TaskUploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

        workerParams.getInputData();
    }

    @NonNull
    @Override
    public Result doWork() {
        countDownLatch = new CountDownLatch(1);
        Data data = getInputData();
        TaskID = data.getString("TaskID");
        Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
        TaskApp.getFirestore()
                .collection("Tasks")
                .document(TaskID)
                .set(tasks)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            setInputData("success");
                            countDownLatch.countDown();
                        }
                        else {
                            setInputData(task.getException().getLocalizedMessage());
                            countDownLatch.countDown();

                        }

                    }
                });
        try {
            countDownLatch.await();
        } catch (Exception e){
            Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            setInputData(e.getLocalizedMessage());
        }
        return Result.success(resultData);

    }


    public void setInputData(String result){
        resultData = new Data.Builder()
                .putString("result",result)
                .build();
    }


}
