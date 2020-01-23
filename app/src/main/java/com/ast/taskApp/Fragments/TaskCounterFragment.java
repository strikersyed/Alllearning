package com.ast.taskApp.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ast.taskApp.AlarmReceiver;
import com.ast.taskApp.Home;
import com.ast.taskApp.Imageslider;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.R;
import com.ast.taskApp.TaskApp;
import com.ast.taskApp.TaskDB;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.novoda.merlin.MerlinsBeard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class TaskCounterFragment extends Fragment implements View.OnClickListener {

    ImageButton cancelbtn, addtimebtn;
    private TextView heading, timerdescript, countdowntv;
    private FirebaseFirestore db;
    TaskDB taskDB;
    Tasks[] tasks;
    private Calendar start, end;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private String TaskID, date;
    private Timestamp endtime;
    SimpleDateFormat format;
    MerlinsBeard merlinsBeard;
    private FirebaseAuth auth;
    StorageReference mStorageRef;
    ArrayList<Tasks> selectedtasks = new ArrayList<>();
    private ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_tskcounterfrgment,container,false);
        auth = FirebaseAuth.getInstance();
        merlinsBeard = new MerlinsBeard.Builder().build(getContext());
        progressBar = view.findViewById(R.id.progressbar);
        countdowntv = view.findViewById(R.id.countdonwntxtv);
        heading = view.findViewById(R.id.tv_heading);
        timerdescript = view.findViewById(R.id.tv_description);
        db = FirebaseFirestore.getInstance();
        imageView = view.findViewById(R.id.tskbackimage);
        cancelbtn = view.findViewById(R.id.cancel);
        addtimebtn = view.findViewById(R.id.addtime);
        TaskID = "";

        addtimebtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tasks = (Tasks[]) getArguments().getSerializable("Tasks");


        heading.setText(tasks[0].getName());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        //format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        if (tasks[0].getPlatform().equals("Android")) {
            if (!(tasks[0].getTaskImageUrl() == null)) {
                Glide.with(getContext()).load(tasks[0].getTaskImageUrl()).into(imageView);
            } else {
                Glide.with(getContext()).load(R.drawable.demo11).sizeMultiplier(0.5f).into(imageView);
            }
        } else {
            final StorageReference Ref = mStorageRef.child("Tasks").child(tasks[0].getTaskID()).child("Attachment").child("mountains.jpg");
            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    Glide.with(getContext()).load(uri).into(imageView);
                }
            });
        }

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);


        date = format.format(tasks[0].getStartDate().toDate());
        timerdescript.setText(date);
        TaskID = tasks[0].getTaskID();
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.setTimeInMillis(tasks[0].getStartTime().toDate().getTime());
        end.setTimeInMillis(tasks[0].getEndTime().toDate().getTime());

        long start_milli = start.getTimeInMillis();
        long end_milli = end.getTimeInMillis();
        //double total_milli = (end_milli - start_milli);
        long total_milli = (end_milli - start_milli);
        //int factor = (int) (100 / total_milli);
        Calendar forCounter = Calendar.getInstance();
        forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
        endtime = tasks[0].getStartTime();
        if (tasks[0].getTaskStatus() != 3) {
            if (forCounter.getTimeInMillis() > 0) {
                if (tasks[0].getStartTime().toDate().before(Timestamp.now().toDate()) || tasks[0].getStartTime().toDate() == Timestamp.now().toDate()) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        counterRun(forCounter.getTimeInMillis(), total_milli);

                    } else {
                        counterRun(forCounter.getTimeInMillis(), total_milli);
                    }
                } else {
                    progressBar.setProgress(50);
                    format = new SimpleDateFormat("hh:mm");
                    String count = format.format(tasks[0].getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
                    countdowntv.setText(count);
                }
            } else {
                progressBar.setProgress(50);
                format = new SimpleDateFormat("hh:mm");
                String count = format.format(tasks[0].getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
                countdowntv.setText(count);
                db.collection("Tasks").document(TaskID).update("taskStatus", 3);
                Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(auth.getCurrentUser().getUid());
                if (tasks != null) {
                    tasks.setTaskStatus(3);
                    TaskApp.getTaskRepo().updateTask(tasks);
                }
            }
        } else {
            progressBar.setProgress(50);
            format = new SimpleDateFormat("hh:mm");
            String count = format.format(tasks[0].getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
            countdowntv.setText(count);
        }

    }

    public static TaskCounterFragment initializetskcounterfrgment(Tasks... tasks){
        TaskCounterFragment taskCounterFragment = new TaskCounterFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Tasks",tasks);
        taskCounterFragment.setArguments(bundle);
        return taskCounterFragment;
    }

    void counterRun(long tofinish, long totalTime) {
        countDownTimer = new CountDownTimer(tofinish, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                    0.001
                //int count = (int) ((int) (forCounter.getTimeInMillis() - millisUntilFinished) * 0.001);
                //double progress = (100.0 * count) / forCounter.getTimeInMillis();

                //holder.progressBar.setProgress((int) millisUntilFinished / 1000);
                progressBar.setProgress((int) ((Double.parseDouble("" + millisUntilFinished) / totalTime) * 100));
                String hms = String.format("%02d : %02d : %02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))
                        , TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                countdowntv.setText(hms);
                //holder.timestatus.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + ":"
                //        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                db.collection("tasks").document(tasks[0].getTaskID()).update("taskStatus", 3);
                Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                if (tasks != null) {
                    tasks.setTaskStatus(3);
                    TaskApp.getTaskRepo().updateTask(tasks);
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addtime:
            {
                endtime = tasks[0].getEndTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(endtime.toDate().getTime());
                calendar.add(Calendar.MINUTE, 20);
                endtime = new Timestamp(calendar.getTime());
                //endtime.toDate().setMinutes((taskss.get(sliderLayout.getCurrentPosition()).getEndTime().toDate().getMinutes() + 20));
                db.collection("Tasks").document(TaskID).update("endTime", endtime).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Time has been added", Toast.LENGTH_SHORT).show();
                    }
                });
                Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                tasks.setEndTime(endtime);
                TaskApp.getTaskRepo().updateTask(tasks);
                //tasks[0].setEndTime(endtime);

                AlarmManager alrmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                Intent cancel = new Intent(getContext(), AlarmReceiver.class);
                cancel.setAction("endtask");
                PendingIntent picancel = PendingIntent.getBroadcast(getContext(),
                        TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), cancel, PendingIntent.FLAG_CANCEL_CURRENT);
                alrmManager.cancel(picancel);
                picancel.cancel();

                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                Intent i = new Intent(getContext(), AlarmReceiver.class);
                i.setAction("endtask");


                if (tasks.isVibrateeonStart()) {
                    i.putExtra("vibration", true);
                    i.putExtra("ringcheck", false);
                } else {
                    i.putExtra("vibration", false);
                    if (tasks.getTuneName() == "" || tasks.getTuneName().isEmpty()) {
                        i.putExtra("ringcheck", false);
                    } else {
                        i.putExtra("ringcheck", true);
                        i.putExtra("ringtone", tasks.getTuneUrl());
                    }
                }
                i.putExtra("TaskID", TaskID);
                PendingIntent pi = PendingIntent.getBroadcast(getContext(),
                        TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), i, PendingIntent.FLAG_UPDATE_CURRENT);

                if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus() == 0) {
                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pi);

                } else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus() == 1) {
                    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

                } else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus() == 2) {
                    alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), DateUtils.WEEK_IN_MILLIS, pi);

                } else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus() == 3) {
                    if (calendar.get(Calendar.MONTH) == Calendar.JANUARY || calendar.get(Calendar.MONTH) == Calendar.MARCH || calendar.get(Calendar.MONTH) == Calendar.MAY || calendar.get(Calendar.MONTH) == Calendar.JULY
                            || calendar.get(Calendar.MONTH) == Calendar.AUGUST || calendar.get(Calendar.MONTH) == Calendar.OCTOBER || calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 31, pi);
                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.APRIL || calendar.get(Calendar.MONTH) == Calendar.JUNE || calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER
                            || calendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pi);
                    }
                    if (calendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, pi);
                    }
                }


            }

            case R.id.cancel:
            {
                Intent intent = new Intent(getContext(), Home.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }
}
