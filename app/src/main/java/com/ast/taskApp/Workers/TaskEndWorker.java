package com.ast.taskApp.Workers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ast.taskApp.AlarmReceiver;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.Context.ALARM_SERVICE;

public class TaskEndWorker extends Worker {

    String Ringname,TaskID,tskname,imageurl;
    Boolean vibration;
    Uri tuneuri;
    Context context;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Timestamp timestamp;
    FirebaseFirestore db;

    public TaskEndWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Data data = getInputData();
        tuneuri = Uri.parse(data.getString("ringtone"));
        vibration = data.getBoolean("vibrationend",true);
        Ringname = data.getString("ringname");
        TaskID = data.getString("TaskID");
        tskname = data.getString("name");
        imageurl = data.getString("imageurl");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data.getLong("endtime",0));
        timestamp = new Timestamp(calendar.getTime());
        setendalarmtime(timestamp);
        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setendalarmtime(Timestamp timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,timestamp.toDate().getMinutes());
        calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
        calendar.set(Calendar.YEAR,timestamp.toDate().getYear());
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra("TaskID",TaskID);
        if (vibration){
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
        myIntent.setAction("endtask");
        pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}