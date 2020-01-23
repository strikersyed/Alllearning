package com.ast.taskApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.request.RequestOptions;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.indicators.PagerIndicator;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.MerlinsBeard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Imageslider extends AppCompatActivity {


    SliderLayout sliderLayout;
    ImageButton cancelbtn,addtimebtn;
    TextView heading,timerdescript,countdowntv;
    PagerIndicator indicator;
    Window window;
    FirebaseFirestore db;
    TaskDB taskDB;
    Tasks tasks;
    ArrayList<Tasks> taskss = new ArrayList<>();
    ArrayList<Tasks> selectedtasks = new ArrayList<>();
    HashMap<Integer, String> ads = new HashMap<Integer, String>();
    Calendar start,end;
    ProgressBar progressBar;
    CountDownTimer countDownTimer;
    String TaskID,date;
    Timestamp starttime;
    SimpleDateFormat format;
    MerlinsBeard merlinsBeard;
    FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        window = this.getWindow();
            //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(Color.BLACK);


        setContentView(R.layout.activity_imageslider);


        auth = FirebaseAuth.getInstance();
        merlinsBeard = new MerlinsBeard.Builder().build(this);
        progressBar = findViewById(R.id.progressbar);
        countdowntv = findViewById(R.id.countdonwntxtv);
        heading = findViewById(R.id.tv_heading);
        timerdescript = findViewById(R.id.tv_description);
        db = FirebaseFirestore.getInstance();
        taskDB = new TaskDB(this);
        sliderLayout = findViewById(R.id.imageslider);
        indicator = findViewById(R.id.indicator);
        cancelbtn = findViewById(R.id.cancel);
        addtimebtn = findViewById(R.id.addtime);
        TaskID = "";


      /*  if (hasConnection()) {
            HashMap<String, String> url_maps = new HashMap<String, String>();
            url_maps.put("1", "https://www.skk.se/globalassets/bilder/annonser-duktig-hund/annons-appen_1.jpg");
            url_maps.put("2", "https://www.skk.se/globalassets/bilder/annonser-duktig-hund/annons-appen_2.jpg");
            url_maps.put("3", "https://www.skk.se/globalassets/bilder/annonser-duktig-hund/annons-appen_3.jpg");


            for (String name : url_maps.keySet()) {
                // initialize a SliderLayout


                DefaultSliderView defaultSliderView = new DefaultSliderView(this);
                defaultSliderView
                        //.description(name)
                        .image(url_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit);
                sliderLayout.addSlider(defaultSliderView);
            }

            n


        } else {*/

        /*if (merlinsBeard.isConnected()){
            getFirestoreData();
        } else {
            if (TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid())!=null){
                Users user = TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid());
                if (TaskApp.getTaskRepo().getAllTasks(user.getUserID()).size()!=0){
                    taskss = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(user.getUserID());
                    taskorganizer();
                }
            }
        }*/
        taskss = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(auth.getCurrentUser().getUid());
        taskorganizer();



        
      addtimebtn.setOnClickListener(new View.OnClickListener() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onClick(View v) {

              //Toast.makeText(Imageslider.this, "Time has been added", Toast.LENGTH_SHORT).show();
              if (selectedtasks.get(sliderLayout.getCurrentPosition()).getTaskStatus()!=3) {
                  starttime = selectedtasks.get(sliderLayout.getCurrentPosition()).getEndTime();
                  Calendar calendar = Calendar.getInstance();
                  calendar.setTimeInMillis(starttime.toDate().getTime());
                  calendar.add(Calendar.MINUTE, 20);
                  starttime = new Timestamp(calendar.getTime());
                  //starttime.toDate().setMinutes((taskss.get(sliderLayout.getCurrentPosition()).getEndTime().toDate().getMinutes() + 20));
                  db.collection("Tasks").document(TaskID).update("endTime", starttime).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                          Toast.makeText(Imageslider.this, "Time has been added", Toast.LENGTH_SHORT).show();
                      }
                  });
                  Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                  tasks.setEndTime(starttime);
                  TaskApp.getTaskRepo().updateTask(tasks);

                  AlarmManager alrmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                  Intent cancel = new Intent(Imageslider.this, AlarmReceiver.class);
                  cancel.setAction("endtask");
                  PendingIntent picancel = PendingIntent.getBroadcast(Imageslider.this,
                          TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), cancel, PendingIntent.FLAG_CANCEL_CURRENT);
                  alrmManager.cancel(picancel);
                  picancel.cancel();

                  AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                  Intent i = new Intent(Imageslider.this, AlarmReceiver.class);
                  i.setAction("endtask");


                  if (tasks.isVibrateeonStart()){
                      i.putExtra("vibration",true);
                      i.putExtra("ringcheck",false);
                  } else {
                      i.putExtra("vibration",false);
                      if (tasks.getTuneName()=="" || tasks.getTuneName().isEmpty()){
                          i.putExtra("ringcheck",false);
                      } else {
                          i.putExtra("ringcheck",true);
                          i.putExtra("ringtone",tasks.getTuneUrl());
                      }
                  }
                  i.putExtra("TaskID",TaskID);
                  PendingIntent pi = PendingIntent.getBroadcast(Imageslider.this,
                          TaskApp.getTaskRepo().getTaskbyId(TaskID).getAlaramID(), i, PendingIntent.FLAG_UPDATE_CURRENT);

                  if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus()==0){
                      alarmManager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pi);

                  } else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus()==1){
                      alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);

                  }else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus()==2){
                      alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(), DateUtils.WEEK_IN_MILLIS,pi);

                  }else if (TaskApp.getTaskRepo().getTaskbyId(TaskID).getTaskStatus()==3) {
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

          }
      });

      cancelbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(Imageslider.this,Home.class);
              startActivity(intent);
              finish();
          }
      });







        sliderLayout.setCustomIndicator(indicator);
        sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Toast.makeText(Imageslider.this,String.valueOf(position),Toast.LENGTH_SHORT);
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onPageSelected(int position) {
                heading.setText(selectedtasks.get(position).getName());
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                //format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

                date = format.format(selectedtasks.get(position).getStartDate().toDate());
                timerdescript.setText(date);
                TaskID = selectedtasks.get(position).getTaskID();
                start = Calendar.getInstance();
                end = Calendar.getInstance();
                start.setTimeInMillis(selectedtasks.get(position).getStartTime().toDate().getTime());
                end.setTimeInMillis(selectedtasks.get(position).getEndTime().toDate().getTime());

                long start_milli = start.getTimeInMillis();
                long end_milli = end.getTimeInMillis();
                //double total_milli = (end_milli - start_milli);
                long total_milli = (end_milli - start_milli);
                //int factor = (int) (100 / total_milli);
                Calendar forCounter = Calendar.getInstance();
                forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                starttime = selectedtasks.get(position).getStartTime();
                if (selectedtasks.get(position).getTaskStatus()!=3) {
                    if (forCounter.getTimeInMillis() > 0) {
                        if (selectedtasks.get(position).getStartTime().toDate().before(Timestamp.now().toDate()) || selectedtasks.get(position).getStartTime().toDate() == Timestamp.now().toDate()) {
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                counterRun(forCounter.getTimeInMillis(), total_milli, position);

                            } else {
                                counterRun(forCounter.getTimeInMillis(), total_milli, position);
                            }
                        } else {
                            progressBar.setProgress(50);
                            format = new SimpleDateFormat("hh:mm");
                            String count = format.format(selectedtasks.get(position).getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
                            countdowntv.setText(count);
                        }
                    }
                    else {
                        progressBar.setProgress(50);
                        format = new SimpleDateFormat("hh:mm");
                        String count = format.format(selectedtasks.get(position).getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
                        countdowntv.setText(count);
                        db.collection("Tasks").document(TaskID).update("taskStatus",3);
                        Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(auth.getCurrentUser().getUid());
                        if (tasks!=null){
                            tasks.setTaskStatus(3);
                            TaskApp.getTaskRepo().updateTask(tasks);
                        }
                    }
                    } else {
                        progressBar.setProgress(50);
                        format = new SimpleDateFormat("hh:mm");
                        String count = format.format(selectedtasks.get(position).getStartTime().toDate());
                    /*String hms = String.format("%02d : %02d",
                            taskss.get(position).getStartTime().toDate().getHours(),
                            taskss.get(position).getStartTime().toDate().getMinutes());*/
                        countdowntv.setText(count);
                    }


            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



            /*HashMap<Integer, String> ads = new HashMap<Integer, String>();
            ads.put(1, R.drawable.demopic);
            ads.put(2, R.drawable.demo8);
            ads.put(3, R.drawable.demo9);
            for (Integer name : ads.keySet()) {
                // initialize a SliderLayout
                DefaultSliderView defaultSliderView = new DefaultSliderView(this);
                defaultSliderView
                        //.description(name)
                        .image(ads.get(name))
                        .setRequestOption(RequestOptions.centerCropTransform());
                sliderLayout.addSlider(defaultSliderView);

            }

            sliderLayout.setCustomIndicator(indicator);
            sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });*/

        }

        public void getFirestoreData(){
            db.collection("Tasks").whereEqualTo("userID",auth.getCurrentUser().getUid())
                    .orderBy("startTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    taskss = (ArrayList<Tasks>) queryDocumentSnapshots.toObjects(Tasks.class);
                    taskorganizer();
                }
            });
        }
        
        

    public void taskorganizer(){
        selectedtasks.clear();
        for (int i = 0; i < taskss.size(); i++) {
            if (taskss.get(i).getTaskStatus()!=4) {
                if (getIntent().getExtras().get("check").equals("Upcoming")) {
                    if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime() - taskss.get(i).getStartTime().toDate().getTime())) <= 12) {
                        selectedtasks.add(taskss.get(i));
                        //ads.put(i, selectedtasks.get(i).getTaskImageUrl());

                    }
                } else if (getIntent().getExtras().get("check").equals("Today")) {
                    if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime() - taskss.get(i).getStartTime().toDate().getTime())) > 12
                            && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime() - taskss.get(i).getStartTime().toDate().getTime())) < 24) {
                        selectedtasks.add(taskss.get(i));
                        // ads.put(i, selectedtasks.get(i).getTaskImageUrl());
                    }
                } else if (getIntent().getExtras().get("check").equals("Tomorrow")) {
                    if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime() - taskss.get(i).getStartTime().toDate().getTime())) >= 24) {
                        selectedtasks.add(taskss.get(i));
                        //ads.put(i, selectedtasks.get(i).getTaskImageUrl());
                    }
                }
            }

        }
        for (int j = 0 ;j <selectedtasks.size();j++){
            ads.put(j, selectedtasks.get(j).getTaskImageUrl());
        }
        for (Integer name : ads.keySet()) {

            // initialize a SliderLayout
            DefaultSliderView defaultSliderView = new DefaultSliderView(Imageslider.this);
            defaultSliderView
                    //.description(name)
                    .image(ads.get(name))
                    .setRequestOption(RequestOptions.centerCropTransform());
            sliderLayout.addSlider(defaultSliderView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Imageslider.this,Home.class);
        startActivity(intent);
        finish();
    }
    void counterRun(long tofinish ,long totalTime,int position){
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
                ,TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                countdowntv.setText(hms);
                //holder.timestatus.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + ":"
                //        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            @Override
            public void onFinish() {
                db.collection("tasks").document(selectedtasks.get(position).getTaskID()).update("taskStatus", 3);
                Tasks tasks = TaskApp.getTaskRepo().getTaskbyId(TaskID);
                if (tasks!=null){
                    tasks.setTaskStatus(3);
                    TaskApp.getTaskRepo().updateTask(tasks);
                }
            }
        }.start();
    }
}
