package com.ast.taskApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.ast.taskApp.Utils.PreferenceUtils;
import com.bumptech.glide.Glide;
import com.ast.taskApp.Activities.NewTaskActivity;
import com.ast.taskApp.Adapters.TodayFirestoreAdapter;
import com.ast.taskApp.Adapters.TodayOverViewAdapter;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.MerlinsBeard;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class TodayOverview extends AppCompatActivity implements DragListener {

    TextView heading,name,email;
    LinearLayout todayoverview,logout,calendarr,upgrade,overduetasks,home,addnewtask;
    TaskDB taskDB;
    Toolbar toolbar;
    RelativeLayout navigationparent;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    TodayOverViewAdapter adapter;
    ArrayList<Tasks> tasks = new ArrayList<>();
    //ArrayList<Tasks> localtasks = new ArrayList<>();
    Calendar calendar;
    int minutes,hoours,day,month,year;
    Timestamp timestamp;
    Window window;
    String personName,personEmail;
    GoogleSignInClient googleSignInClient;
    Uri personPhoto;
    FirestoreRecyclerOptions<Tasks> options;
    TodayFirestoreAdapter firestoreAdapter;
    Query query;
    MerlinsBeard merlinsBeard;
    FirebaseAuth auth;
    Context context;
    ArrayList<Tasks> tasksNew, todaytsks,tomorrowtsks,upcomingtsks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        window = this.getWindow();
        //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.WHITE);
        setContentView(R.layout.activity_today_overview);

        context = this;
        merlinsBeard = new MerlinsBeard.Builder().build(this);
        auth = FirebaseAuth.getInstance();
        heading = findViewById(R.id.overvw_heading);
        taskDB = new TaskDB(this);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.todayoverrecycler);

        recyclerView.setAdapter(adapter);
        toolbar = findViewById(R.id.toolbar);
        new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withMenuLayout(R.layout.slide_menulayout)
                .addDragListener(this)
                .inject();
        navigationparent = findViewById(R.id.navigationparent);
        toolbar.setNavigationIcon(R.mipmap.menu);

        try {
            if (getIntent().getExtras().get("check").equals("overduetasks") || getIntent().getExtras().get("check").equals("overduetasks")){
                heading.setText("Overduetasks Overview");
            }
            else if (getIntent().getExtras().get("check").equals("tomorrow")){
                heading.setText("Tomorrow Overview");
            }
            else if (getIntent().getExtras().get("check").equals("upcoming")){
                heading.setText("Upcoming Overview");

            }


        }
        catch (Exception e){

        }

        /*db.collection("Tasks").whereEqualTo("userID",taskDB.getString("userID")).orderBy("startTime", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
              tasks = (ArrayList<Tasks>) task.getResult().toObjects(Tasks.class);
              adapter = new TodayOverViewAdapter(TodayOverview.this,tasks);
              recyclerView.setAdapter(adapter);
            }
        });*/

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();



        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);


        /*final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            //String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
        }*/
        personEmail = PreferenceUtils.getEmail(context);
        personName = PreferenceUtils.getName(context);
        personPhoto = Uri.parse(PreferenceUtils.getImage(context));

        tasksNew = new ArrayList<>();
        todaytsks = new ArrayList<>();
        tomorrowtsks = new ArrayList<>();
        upcomingtsks = new ArrayList<>();

        tasks = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(TaskApp.getAuth().getCurrentUser().getUid());
        for (Tasks tasks : tasks){

            if (tasks.getTaskStatus() != 4 && tasks.getTaskStatus() != 3){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(tasks.getStartTime().toDate().getTime() - Timestamp.now().toDate().getTime())) <= 12) {
                    upcomingtsks.add(tasks);
                    }
                else if (Math.abs(TimeUnit.MILLISECONDS.toHours(tasks.getStartTime().toDate().getTime()) - Timestamp.now().toDate().getTime()) > 12
                        && Math.abs(TimeUnit.MILLISECONDS.toHours(tasks.getStartTime().toDate().getTime() - Timestamp.now().toDate().getTime())) < 24) {
                    todaytsks.add(tasks);
                    }
                else if (Math.abs(TimeUnit.MILLISECONDS.toHours(tasks.getStartTime().toDate().getTime() - Timestamp.now().toDate().getTime()))>=24
                        && Math.abs(TimeUnit.MILLISECONDS.toHours(tasks.getStartTime().toDate().getTime() - Timestamp.now().toDate().getTime()))<=48 ){
                    tomorrowtsks.add(tasks);
                    }
                else {
                    tasksNew.add(tasks);
                    }
                }
            else if (tasks.getTaskStatus()==3){
                tasksNew.add(tasks);
                }
            }
        Comparator<Tasks> c = new Comparator<Tasks>() {

            @Override
            public int compare(Tasks a, Tasks b) {
                return Long.compare(a.getStartTime().getSeconds() * 1000, b.getStartTime().getSeconds() * 1000);
            }
        };
        Collections.sort(tasksNew, c);


        tasks = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(auth.getCurrentUser().getUid());
        try {
            if (getIntent().getExtras().get("check").equals("overduetasks") || getIntent().getExtras().get("check").equals("overduetasks")){
                adapter = new TodayOverViewAdapter(TodayOverview.this,tasksNew,getIntent().getExtras().get("check").toString());
            }
            else if (getIntent().getExtras().get("check").equals("tomorrow")){
                adapter = new TodayOverViewAdapter(TodayOverview.this,tomorrowtsks,getIntent().getExtras().get("check").toString());
            }
            else if (getIntent().getExtras().get("check").equals("upcoming")){
                adapter = new TodayOverViewAdapter(TodayOverview.this,upcomingtsks,getIntent().getExtras().get("check").toString());

            }else if (getIntent().getExtras().get("check").equals("today")){
                adapter = new TodayOverViewAdapter(TodayOverview.this,todaytsks,getIntent().getExtras().get("check").toString());

            }


        }
        catch (Exception e){
            Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_LONG);

        }
        //adapter = new TodayOverViewAdapter(TodayOverview.this,tasksNew,getIntent().getExtras().get("check").toString());
        recyclerView.setAdapter(adapter);

        /*if (merlinsBeard.isConnected()){
            getFirestoreData();
        } else {
            if (TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid())!=null){
                Users users = TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid());
                if (TaskApp.getTaskRepo().getAllTasks(users.getUserID()).size()!=0){
                    tasks = (ArrayList<Tasks>)TaskApp.getTaskRepo().getAllTasks(users.getUserID());
                    if (getIntent().getExtras().get("check")==null){
                      adapter = new TodayOverViewAdapter(this,tasks,"today");
                    }else {
                        adapter = new TodayOverViewAdapter(this,tasks,getIntent().getExtras().get("check").toString());
                    }
                    recyclerView.setAdapter(adapter);

                    //adapter = new TodayOverViewAdapter(this,tasks,getIntent().getExtras().get("check").toString());



                }

            }
        }*/








        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        calendarr = findViewById(R.id.calendar);
        CircleImageView circularImageView = findViewById(R.id.profilepic);
        Glide.with(this).asBitmap().load(personPhoto).into(circularImageView);

        name.setText(personName);
        email.setText(personEmail);


        todayoverview = findViewById(R.id.todayoverview);
        logout = findViewById(R.id.logout);
        upgrade = findViewById(R.id.upgradetopro);
        overduetasks = findViewById(R.id.overduetask);
        home = findViewById(R.id.homelayout);
        addnewtask = findViewById(R.id.addnewtask);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayOverview.this,Home.class);
                //intent.putExtra("check","overduetasks");
                startActivity(intent);
            }
        });

        calendarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayOverview.this, CalenderView.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Users")
                        .document(TaskApp.getAuth().getCurrentUser().getUid()).update("isLoggedin",0)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    auth.signOut();
                                    googleSignInClient.signOut();
                                    PreferenceUtils.clearMemory(context);
                                    if (PreferenceUtils.clearMemory(context)) {
                                        Intent intent1 = new Intent(context, Login.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent1);
                                    }
                                }
                                else {

                                    Toast.makeText(context, "Unable to logout!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        overduetasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayOverview.this,TodayOverview.class);
                intent.putExtra("check","overduetasks");
                startActivity(intent);

            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayOverview.this,SubscriptionView.class);
                intent.putExtra("upgrade","sidemenu");
                startActivity(intent);
            }
        });

        addnewtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodayOverview.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });

        todayoverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getExtras().get("check").equals("overduetasks") ||
                        getIntent().getExtras().get("check").equals("tomorrow")||
                        getIntent().getExtras().get("check").equals("upcoming")) {
                    Intent intent = new Intent(TodayOverview.this, TodayOverview.class);
                    intent.putExtra("check", "today");
                    startActivity(intent);
                    finish();
                }
            }
        });

        /*query = FirebaseFirestore.getInstance()
                .collection("Tasks")
                .whereEqualTo("userID",taskDB.getString("userID"))
                .orderBy("startTime", Query.Direction.ASCENDING)
                .limit(50);

        options = new FirestoreRecyclerOptions.Builder<Tasks>()
                .setQuery(query, Tasks.class)
                .setLifecycleOwner(this)
                .build();

        firestoreAdapter = new TodayFirestoreAdapter(options,this,getIntent().getExtras().get("check").toString());
        recyclerView.setAdapter(firestoreAdapter);*/






        /*Tasks taskss = new Tasks();
        taskss.setName("Meeting");
        calendar = Calendar.getInstance();
        calendar.set(2020,1,8,7,44);
        timestamp = new Timestamp(calendar.getTime());
        taskss.setStartTime(timestamp);
        calendar = Calendar.getInstance();
        calendar.set(2020,1,8,7,46);
        timestamp = new Timestamp(calendar.getTime());
        taskss.setEndTime(timestamp);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Family Gathering");
        calendar = Calendar.getInstance();
        calendar.set(2020,1,8,1,44);
        timestamp = new Timestamp(calendar.getTime());
        taskss.setStartTime(timestamp);
        calendar = Calendar.getInstance();
        calendar.set(2020,1,8,3,39);
        timestamp = new Timestamp(calendar.getTime());
        taskss.setEndTime(timestamp);
        tasks.add(taskss);


        adapter = new TodayOverViewAdapter(TodayOverview.this,tasks);
        recyclerView.setAdapter(adapter);*/



    }

    @Override
    public void onDrag(float progress) {
        if (progress > 0) {
            navigationparent.setVisibility(View.VISIBLE);
            getWindow().setStatusBarColor(Color.parseColor("#" + getHexa((int) (progress * 100)) + "416AF3"));
        } else {

            navigationparent.setVisibility(View.INVISIBLE);;
            window.setStatusBarColor(Color.WHITE);
        }
    }

    public static String getHexa(int trans) {
        String hexString = Integer.toHexString(Math.round(255 * trans / 100));
        return (hexString.length() < 2 ? "0" : "") + hexString;
    }

    public void getFirestoreData(){
        db.collection("Tasks").whereEqualTo("userID",auth.getCurrentUser().getUid()).orderBy("startTime", Query.Direction.ASCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                tasks =(ArrayList<Tasks>) task.getResult().toObjects(Tasks.class);
                if (tasks.size()>0){
                    adapter = new TodayOverViewAdapter(TodayOverview.this,tasks,getIntent().getExtras().get("check").toString());
                    recyclerView.setAdapter(adapter);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(TodayOverview.this,Home.class);
        startActivity(intent);
        finish();
    }
}
