package com.ast.taskApp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ast.taskApp.Models.Tasks;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.guojunustb.library.WeekDayView;
import com.guojunustb.library.WeekHeaderView;
import com.guojunustb.library.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalenderView extends AppCompatActivity implements WeekDayView.MonthChangeListener, WeekDayView.EventLongPressListener, WeekDayView.EventClickListener, WeekDayView.ScrollListener {

    WeekDayView mWeekView;
    WeekHeaderView mWeekHeaderView;
    TextView year,month;
    List<WeekViewEvent> mNewEvent;
    ImageButton backbtn;
    Window window;
    FirebaseFirestore db;
    ArrayList<Tasks> tasks = new ArrayList<>();
    TaskDB taskDB;
    Calendar startTime,endTime;
    List<WeekViewEvent> events = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        window = this.getWindow();
        //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.WHITE);
        setContentView(R.layout.activity_task_calender);

        taskDB = new TaskDB(this);
        db = FirebaseFirestore.getInstance();
        backbtn = findViewById(R.id.back);
        mNewEvent = new ArrayList<WeekViewEvent>();
        mWeekView = (WeekDayView) findViewById(R.id.weekdayview);
        mWeekHeaderView= (WeekHeaderView) findViewById(R.id.weekheaderview);
        year = findViewById(R.id.year);
        month = findViewById(R.id.month);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setScrollListener(this);

        db.collection("Tasks").whereEqualTo("userID", taskDB.getString("userID")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Tasks taskss = queryDocumentSnapshot.toObject(Tasks.class);
                    tasks.add(taskss);
                }
                if (tasks.size()>0) {
                    startTime = Calendar.getInstance();
                    endTime = Calendar.getInstance();
                    for (int i = 0; i < tasks.size(); i++) {
                        startTime.setTimeInMillis(tasks.get(i).getStartTime().toDate().getTime());
                        endTime.setTimeInMillis(tasks.get(i).getEndTime().toDate().getTime());
                        WeekViewEvent event = new WeekViewEvent(1, tasks.get(i).getName(), startTime, endTime);
                        event.setColor(getResources().getColor(R.color.blue));
                        events.add(event);
                    }
                }

            }
        });

        if(Timestamp.now().toDate().getMonth()==0){
            month.setText("Jan");
        }
        else if (Timestamp.now().toDate().getMonth()==1){
            month.setText("Feb");
        }
        else if (Timestamp.now().toDate().getMonth()==2){
            month.setText("Mar");
        }
        else if (Timestamp.now().toDate().getMonth()==3){
            month.setText("Apr");
        }
        else if (Timestamp.now().toDate().getMonth()==4){
            month.setText("May");
        }
        else if (Timestamp.now().toDate().getMonth()==5){
            month.setText("Jun");
        }
        else if (Timestamp.now().toDate().getMonth()==6){
            month.setText("Jul");
        }
        else if (Timestamp.now().toDate().getMonth()==7){
            month.setText("Aug");
        }
        else if (Timestamp.now().toDate().getMonth()==8){
            month.setText("Sep");
        }
        else if (Timestamp.now().toDate().getMonth()==9){
            month.setText("Oct");
        }
        else if (Timestamp.now().toDate().getMonth()==10){
            month.setText("Nov");
        }
        else if (Timestamp.now().toDate().getMonth()==11){
            month.setText("Dec");
        }

        int monthnum = Timestamp.now().toDate().getMonth() + 1;
        year.setText("" +monthnum);

        mWeekHeaderView.setDateSelectedChangeListener(new WeekHeaderView.DateSelectedChangeListener() {
            @Override
            public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay) {
                mWeekView.goToDate(newSelectedDay);
            }
        });
        mWeekHeaderView.setScrollListener(new WeekHeaderView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                mWeekView.goToDate(mWeekHeaderView.getSelectedDay());

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalenderView.this,Home.class);
                startActivity(intent);
            }
        });



    }




    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();


        if (tasks.size()>0){
            for (int i = 0 ; i < tasks.size() ; i++){
                startTime.setTime(tasks.get(i).getStartTime().toDate());
                endTime.setTime(tasks.get(i).getEndTime().toDate());
                WeekViewEvent event = new WeekViewEvent(1, tasks.get(i).getName(), startTime, endTime);
                event.setColor(getResources().getColor(R.color.blue));
                events.add(event);
            }
        } else {
            startTime.set(Calendar.HOUR_OF_DAY, 3);
            startTime.set(Calendar.MINUTE, 0);
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);
            ;
            endTime.add(Calendar.HOUR, 1);
            endTime.set(Calendar.MONTH, newMonth - 1);
            WeekViewEvent event = new WeekViewEvent(1, "This is a Event!!", startTime, endTime);
            event.setColor(getResources().getColor(R.color.blue));
            events.add(event);
        }



        //events.addAll(mNewEvent);
        //events.add(event);


        return events;



    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {


    }

    @Override
    public void onSelectedDaeChange(Calendar selectedDate) {
        mWeekHeaderView.setSelectedDay(selectedDate);
        int monthnum = selectedDate.get(Calendar.MONTH) + 1;
        year.setText(""+monthnum  );

        if(selectedDate.get(Calendar.MONTH)==0){
            month.setText("Jan");
        }
        else if (selectedDate.get(Calendar.MONTH)==1){
            month.setText("Feb");
        }
        else if (selectedDate.get(Calendar.MONTH)==2){
            month.setText("Mar");
        }
        else if (selectedDate.get(Calendar.MONTH)==3){
            month.setText("Apr");
        }
        else if (selectedDate.get(Calendar.MONTH)==4){
            month.setText("May");
        }
        else if (selectedDate.get(Calendar.MONTH)==5){
            month.setText("Jun");
        }
        else if (selectedDate.get(Calendar.MONTH)==6){
            month.setText("Jul");
        }
        else if (selectedDate.get(Calendar.MONTH)==7){
            month.setText("Aug");
        }
        else if (selectedDate.get(Calendar.MONTH)==8){
            month.setText("Sep");
        }
        else if (selectedDate.get(Calendar.MONTH)==9){
            month.setText("Oct");
        }
        else if (selectedDate.get(Calendar.MONTH)==10){
            month.setText("Nov");
        }
        else if (selectedDate.get(Calendar.MONTH)==11){
            month.setText("Dec");
        }



    }
}
