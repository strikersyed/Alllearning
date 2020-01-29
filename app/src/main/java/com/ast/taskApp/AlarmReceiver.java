package com.ast.taskApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;

import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;

import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Utils.NotificationUtils;

public class AlarmReceiver extends BroadcastReceiver {

    Uri alarmtone;
    public static String TaskID, taskName, ringtone ;
    public static boolean vibration, ringcheck;
    Data.Builder data;
    @Override
    public void onReceive(Context context, Intent intent) {

        vibration = false;

        TaskID = intent.getExtras().getString("taskID");
        taskName = intent.getExtras().getString("taskName");
        ringtone = intent.getExtras().getString("ringtone");
        vibration = intent.getExtras().getBoolean("vibration");
        ringcheck = intent.getExtras().getBoolean("ringcheck");
        data = new Data.Builder();

        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
        //imageurl = intent.getExtras().getString("imageurl");
        //name = tasks.getName();

        if (intent.getAction().equals("startTask") || intent.getAction().equals("endTask")) {
            if (intent.getExtras().get("ringcheck").equals(true)) {
                if (tasks.getTuneUrl() == null) {
                    alarmtone = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
                    if (alarmtone == null) {
                        alarmtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    }
                } else {
                    alarmtone = Uri.parse(tasks.getTuneUrl());
                }
                Ringtone ringtone = RingtoneManager.getRingtone(context, alarmtone);
                ringtone.play();
                CountDownTimer countDownTimer = new CountDownTimer(20000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ringtone.stop();
                        notificationManagerCompat.cancel(2);
                    }
                }.start();
            } else {

            }
            if (intent.getAction().equals("startTask")){
                if (tasks.isVibrateeonStart()){
                    vibration = true;
                    NotificationUtils.makeStatusNotification(false, "Task is Starting", "Your Task is about to start", context);

                }
                else {
                    vibration = false;
                    NotificationUtils.makeStatusNotification(false, "Task is Starting", "Your Task is about to start", context);

                }
                data.putString("TaskID",TaskID);
                data.putInt("status",1);
                TaskApp.getWorkManager().enqueue(TaskApp.getTaskUpdateRequest(data.build()));
                //Tasks tasks1 = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                tasks.setTaskStatus(1);
                TaskApp.getTaskRepo().updateTask(tasks);

            }
            else if (intent.getAction().equals("endTask")){
                if (tasks.isVibrateonEnd()){
                    vibration = true;
                    NotificationUtils.makeStatusNotification(false, "Task is Ending", "Your Task is about to End", context);
                }
                else {
                    vibration = false;
                    NotificationUtils.makeStatusNotification(false, "Task is Ending", "Your Task is about to End", context);
                }
                //Tasks tasks2 = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                data.putString("TaskID",TaskID);
                data.putInt("status",3);
                TaskApp.getWorkManager().enqueue(TaskApp.getTaskUpdateRequest(data.build()));
                tasks.setTaskStatus(3);
                TaskApp.getTaskRepo().updateTask(tasks);
            }


        }

        if (intent.getAction().toString().equals("start_task")){
            Intent intent1 = new Intent(context,Login.class);
            notificationManagerCompat.cancel(2);
            context.startActivity(intent1);
            data.putString("TaskID",TaskID);
            data.putInt("status",1);
            TaskApp.getWorkManager().enqueue(TaskApp.getTaskUpdateRequest(data.build()));
            Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(TaskID);
            taskss.setTaskStatus(1);
            TaskApp.getTaskRepo().updateTask(taskss);
        }
        if (intent.getAction().toString().equals("complete_task")){
            Intent intent1 = new Intent(context,Login.class);
            notificationManagerCompat.cancel(2);
            context.startActivity(intent1);
            data.putString("TaskID",TaskID);
            data.putInt("status",4);
            TaskApp.getWorkManager().enqueue(TaskApp.getTaskUpdateRequest(data.build()));
            Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(TaskID);
            taskss.setTaskStatus(4);
            TaskApp.getTaskRepo().updateTask(taskss);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent start = new Intent(context, AlarmReceiver.class);
            start.setAction("endTask");
            PendingIntent pistart = PendingIntent.getBroadcast(context,
                    TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), start, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pistart);
            pistart.cancel();

            AlarmManager alrmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent end = new Intent(context, AlarmReceiver.class);
            end.setAction("startTask");
            PendingIntent piend = PendingIntent.getBroadcast(context,
                    TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), end, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(piend);

        }
    }
}
