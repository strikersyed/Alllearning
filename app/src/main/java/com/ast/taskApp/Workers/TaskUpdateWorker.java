package com.ast.taskApp.Workers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.ast.taskApp.AlarmReceiver;
import com.ast.taskApp.TaskApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.concurrent.CountDownLatch;

import static android.content.Context.ALARM_SERVICE;

public class TaskUpdateWorker extends Worker {

    private String TaskID;
    private Context context;
    private CountDownLatch countDownLatch;
    private Data resultData;
    private Timestamp timestamp;
    public TaskUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        countDownLatch = new CountDownLatch(1);
        TaskID = getInputData().getString("TaskID");
        if (getInputData().getInt("status",0)>0){
            int status = getInputData().getInt("status",0);
            TaskApp.getFirestore()
                    .collection("Tasks")
                    .document(TaskID)
                    .update("taskStatus",status)
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
        }else if (getInputData().getLong("endtime",0)>0){
            long endtime = getInputData().getLong("endtime",0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endtime);
            timestamp = new Timestamp(calendar.getTime());
            TaskApp.getFirestore()
                    .collection("Tasks")
                    .document(TaskID)
                    .update("endTime",timestamp)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                setInputData("success");
                                countDownLatch.countDown();
                            }
                            else {
                                Toast.makeText(context,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                setInputData(task.getException().getLocalizedMessage());
                                countDownLatch.countDown();
                            }
                        }
                    });
        }


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
