package com.ast.taskApp.Workers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;




import android.net.Uri;
import android.os.Build;

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

public class TaskStartWorker extends Worker {

    String TaskID;
    Context context;
    Timestamp timestamp;
    Boolean uploaded;
    public TaskStartWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        workerParams.getInputData();
    }

    @NonNull
    @Override
    public Result doWork() {

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

                            uploaded = true;

                        }
                        else {
                            uploaded = false;
                        }

                    }
                });
        if (uploaded){
            return  Result.success();
        }
        else {
            return Result.retry();
        }


    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)*//*
    public void setstartalarmtime(Timestamp timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,timestamp.toDate().getMinutes());
        calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
        calendar.set(Calendar.SECOND,timestamp.toDate().getSeconds());
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra("TaskID",TaskID);
        if (vibration==true){
            myIntent.putExtra("vibration",true);
        } else {
            myIntent.putExtra("vibration",false);
        }
        if (Ringname.equals("") || Ringname.isEmpty()){
            myIntent.putExtra("ringcheck",false);
        } else {
            myIntent.putExtra("ringcheck",true);
            myIntent.putExtra("ringtone",tuneuri.toString());
        }
        if (tskname.equals("") || tskname.isEmpty()){

        } else {
            myIntent.putExtra("name",tskname);
        }
        myIntent.putExtra("imageurl",imageurl);

        myIntent.setAction("starttask");
        pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_HOUR,pendingIntent);
    }*/
}
