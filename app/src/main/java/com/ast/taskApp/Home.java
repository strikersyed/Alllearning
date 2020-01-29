package com.ast.taskApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.ast.taskApp.Sections.Late;
import com.ast.taskApp.Sections.Today;
import com.ast.taskApp.Sections.Tomorrow;
import com.ast.taskApp.Sections.Upcoming;
import com.ast.taskApp.Utils.PreferenceUtils;
import com.bumptech.glide.Glide;
import com.ast.taskApp.Activities.NewTaskActivity;
import com.ast.taskApp.Adapters.HomeFirestoreAdapter;
import com.ast.taskApp.Adapters.HomeVewAdapter;
import com.ast.taskApp.Models.Tasks;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.MerlinsBeard;
import com.special.ResideMenu.ResideMenu;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class Home extends AppCompatActivity implements DragListener,Upcoming.OnItemClick, Today.OnItemClick, Tomorrow.OnItemClick, Late.OnItemClick {

    String personName,personEmail;
    Toolbar toolbar;
    ImageButton sidenav,newtask;
    RelativeLayout navigationparent;
    ResideMenu resideMenu;
    RecyclerView recyclerView;
    HomeVewAdapter adapter;
    LinearLayout logout,todayoverview,calendar,overduetasks,addnewtask,upgrade,home;
    Uri personPhoto;
    TextView name,email;
    GoogleSignInClient googleSignInClient;
    FirebaseFirestore db;
    ArrayList<Tasks> tasks,todaytsks,tomorrowtsks,upcomingtsks;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TaskDB taskDB;
    Window window;
    Calendar calendarr;
    Timestamp timestamp;
    FirestoreRecyclerOptions<Tasks> options;
    HomeFirestoreAdapter firestoreAdapter;
    Query query;
    MerlinsBeard merlinsBeard;
    FirebaseAuth auth;
    String ID;
    ArrayList<Tasks> tasksNew;
    Context context;
    SectionedRecyclerViewAdapter viewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        window = this.getWindow();
        //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.WHITE);

        //window.setStatusBarColor(getResources().getColor(R.color.white));

        setContentView(R.layout.activity_home);
        context = this;
        auth = FirebaseAuth.getInstance();
        merlinsBeard = new MerlinsBeard.Builder().build(this);
        toolbar = findViewById(R.id.toolbar);
//        sidenav = findViewById(R.id.sidenavigation);
        taskDB = new TaskDB(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        newtask = findViewById(R.id.newtask_btn);
        recyclerView = findViewById(R.id.recycler);
        viewAdapter = new SectionedRecyclerViewAdapter();

        /*recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));*/

        tasks = new ArrayList<>();
        todaytsks = new ArrayList<>();
        tomorrowtsks = new ArrayList<>();
        upcomingtsks = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        /*final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            //String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
        }*/

        //getFirestoreData();
        tasksNew = new ArrayList<>();
        todaytsks = new ArrayList<>();
        tomorrowtsks = new ArrayList<>();
        upcomingtsks = new ArrayList<>();


        tasks = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(TaskApp.getAuth().getCurrentUser().getUid());
        for (Tasks tasks : tasks){

            if (tasks.getTaskStatus() != 4){
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
        }
        Comparator<Tasks> c = new Comparator<Tasks>() {

            @Override
            public int compare(Tasks a, Tasks b) {
                return Long.compare(a.getStartTime().getSeconds() * 1000, b.getStartTime().getSeconds() * 1000);
            }
        };
        Collections.sort(tasksNew, c);
        //adapter = new HomeVewAdapter(Home.this, tasks, Home.this, Home.this, Home.this);
        viewAdapter = new SectionedRecyclerViewAdapter();
        viewAdapter.addSection(new Upcoming(upcomingtsks,context,this,this,this));
        viewAdapter.addSection(new Today(todaytsks,context,this,this,this));
        viewAdapter.addSection(new Tomorrow(tomorrowtsks,context,this,this,this));
        viewAdapter.addSection(new Late(tasksNew,context,this,this,this));
        recyclerView.setAdapter(viewAdapter);
        //adapter.notifyDataSetChanged();
        /*if (merlinsBeard.isConnected()) {
            adapter = new HomeVewAdapter(this, tasks, this, this, this);
            recyclerView.setAdapter(adapter);



            getFirestoreData();
        }
        else {
            if (TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid())!=null){
                Users users = TaskApp.getUserRepo().getUserById(auth.getCurrentUser().getUid());

                if (TaskApp.getTaskRepo().getAllTasks(users.getUserID()).size() != 0){
                    tasks = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(users.getUserID());

                    adapter = new HomeVewAdapter(this, tasks, this, this, this);
                    recyclerView.setAdapter(adapter);

                }
            }
        }*/




        //SlidingRootNavLayout getSlidingRootNavLayout = new SlidingRootNavLayout(this);

        new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withMenuLayout(R.layout.slide_menulayout)
                .addDragListener(this)
                .inject();

        upgrade = findViewById(R.id.upgradetopro);
        navigationparent = findViewById(R.id.navigationparent);
        toolbar.setNavigationIcon(R.mipmap.menu);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        calendar = findViewById(R.id.calendar);
        /*CircleImageView circularImageView = findViewById(R.id.profilepic);
        Glide.with(this).asBitmap().load(personPhoto).into(circularImageView);

        name.setText(personName);
        email.setText(personEmail);*/
        CircleImageView circularImageView = findViewById(R.id.profilepic);

        Glide.with(this).asBitmap().load(PreferenceUtils.getImage(context)).into(circularImageView);
        name.setText(PreferenceUtils.getName(context));
        email.setText(PreferenceUtils.getEmail(context));


        todayoverview = findViewById(R.id.todayoverview);
        logout = findViewById(R.id.logout);
        addnewtask = findViewById(R.id.addnewtask);
        overduetasks = findViewById(R.id.overduetask);
        home = findViewById(R.id.homelayout);



        overduetasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,TodayOverview.class);
                intent.putExtra("check","overduetasks");
                startActivity(intent);

            }
        });

        addnewtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, NewTaskActivity.class);
                startActivity(intent);
                finish();
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CalenderView.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ID = auth.getCurrentUser().getUid();

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

        todayoverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,TodayOverview.class);
                intent.putExtra("check","today");
                startActivity(intent);
                finish();
            }
        });

        upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,SubscriptionView.class);
                intent.putExtra("upgrade","sidemenu");
                startActivity(intent);
                finish();
            }
        });

        /*resideMenu = new ResideMenu(Home.this);
        resideMenu.setBackgroundColor(getResources().getColor(R.color.theme));
        resideMenu.attachToActivity(Home.this);
        resideMenu.setShadowVisible(false);
        resideMenu.setScaleValue(0.75f);*/


        /*db.collection("Tasks").whereEqualTo("userID",taskDB.getString("userID")).orderBy("startTime", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Toast.makeText(Home.this, "Please Wait Loading your Tasks", Toast.LENGTH_SHORT).show();
                tasks = (ArrayList<Tasks>) task.getResult().toObjects(Tasks.class);

                if (tasks.size()>0){

                    *//* editor.putString("todaytasks",ObjectSerializer.serialize(todaytsks));
                    editor.putString("tomorrowtasks",ObjectSerializer.serialize(tomorrowtsks));
                    editor.putString("upcomingtasks",ObjectSerializer.serialize(upcomingtsks));
                    editor.apply();*//*
                }
            }
        });*/

        /*query = FirebaseFirestore.getInstance()
                .collection("Tasks")
                .whereEqualTo("userID",taskDB.getString("userID"))
                .orderBy("startTime", Query.Direction.ASCENDING)
                .limit(50);

        options = new FirestoreRecyclerOptions.Builder<Tasks>()
                .setQuery(query, Tasks.class)
                .setLifecycleOwner(this)
                .build();

        firestoreAdapter = new HomeFirestoreAdapter(options,this,this,this,this);
        recyclerView.setAdapter(firestoreAdapter);*/








        /*Tasks taskss = new Tasks();
        taskss.setName("Meeting");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,2,11,44);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(0);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Family Gathering");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,5,13,20);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(1);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Meetup");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,10,3,10);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(2);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Dinner");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,12,10,30);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(0);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Picnic");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,15,14,10);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(2);
        tasks.add(taskss);

        taskss = new Tasks();
        taskss.setName("Shopping spree");
        calendarr = Calendar.getInstance();
        calendarr.set(2020,1,20,17,40);
        timestamp = new Timestamp(calendarr.getTime());
        taskss.setStartTime(timestamp);
        taskss.setTaskStatus(1);
        tasks.add(taskss);*/






//        resideMenu.setScaleX(1.0f);
        /*resideMenu.setScaleY(0.5f);*/



        /*String titles[] = { "Home", "Profile", "Calendar", "Settings" };
        int icon[] = { R.drawable.bars, R.drawable.bars, R.drawable.bars, R.drawable.bars };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(Home.this, icon[i], titles[i]);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT);

        }*/

        /*sidenav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);*//*

            }
        });*/

        newtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, NewTaskActivity.class);
                startActivity(intent);
                finish();
            }
        });





    }

    public void getFirestoreData(){
        db.collection("Tasks").whereEqualTo("userID",auth.getCurrentUser().getUid()).orderBy("startTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //tasks = (ArrayList<Tasks>) queryDocumentSnapshots.toObjects(Tasks.class);
                            //tasks.clear();
                            tasks = (ArrayList<Tasks>) TaskApp.getTaskRepo().getAllTasks(auth.getCurrentUser().getUid());
                            for (int i = 0;i<tasks.size();i++){
                                TaskApp.getTaskRepo().deleteTasks(tasks.get(i).getTaskID());
                            }
                            tasks.clear();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                            Tasks taskss = documentSnapshot.toObject(Tasks.class);
                            //tasks.add(taskss);
                            TaskApp.getTaskRepo().insertTasks(taskss);

                        }


                        /*if (TaskApp.getTaskRepo().getAllTasks(auth.getCurrentUser().getUid()).size()<tasks.size()){
                            TaskApp.getTaskRepo().addAllTasks(tasks);
                        }*/

                        /*if (tasks.size()>0) {
                            //adapter = new HomeVewAdapter(Home.this,tasks,Home.this,Home.this,Home.this);
                            recyclerView.setAdapter(adapter);
                        }*/
                    }
                });
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

    @Override
    public void onitemClick(int position) {

    }

    @Override
    public void onstartClick(int position,String listname,String taskID) {
        //String taskid = options.getSnapshots().get(position).getTaskID();
        int index = 0;
        String taskid = "";
        int cposition = position - 1;
        if (listname.equals("upcoming")){
            for (int l = 0;l <upcomingtsks.size();l++){
                if (upcomingtsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = upcomingtsks.get(l).getTaskID();
                    break;
                }
            }
           tasks.clear();
           tasks.addAll(upcomingtsks);
        } else if (listname.equals("today")){
            for (int l = 0;l <todaytsks.size();l++){
                if (todaytsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = todaytsks.get(l).getTaskID();
                    break;
                }
            }
            tasks.clear();
            tasks.addAll(todaytsks);
        } else if (listname.equals("tomorrow")){
            for (int l = 0;l <tomorrowtsks.size();l++){
                if (tomorrowtsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = tomorrowtsks.get(l).getTaskID();
                    break;
                }
            }
            tasks.clear();
            tasks.addAll(tomorrowtsks);
        } else if (listname.equals("late")){
            for (int l = 0;l <tasksNew.size();l++){
                if (tasksNew.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = tasksNew.get(l).getTaskID();
                    break;
                }
            }
            //taskid = tasksNew.get(position).getTaskID();
            tasks.clear();
            tasks.addAll(tasksNew);
        }


        if (tasks.get(index).getTaskStatus()!=3) {
            if (tasks.get(index).getTaskStatus() == 0 || tasks.get(index).getTaskStatus() == 2) {
                ImageView imageView = recyclerView.findViewById(R.id.start_todaytask);
                imageView.setImageResource(R.mipmap.start);
                db.collection("Tasks").document(taskid).update("taskStatus", 1);
                Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(index).getTaskID());
                if (taskss != null) {
                    taskss.setTaskStatus(1);
                    tasks.get(index).setTaskStatus(1);
                    TaskApp.getTaskRepo().updateTask(taskss);
                } else {
                    TaskApp.getTaskRepo().insertTasks(tasks.get(index));
                }

            } else {
                ImageView imageView = recyclerView.findViewById(R.id.start_todaytask);
                imageView.setImageResource(R.mipmap.pause);
                db.collection("Tasks").document(taskid).update("taskStatus", 2);
                Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(index).getTaskID());
                if (taskss != null) {
                    taskss.setTaskStatus(2);
                    tasks.get(index).setTaskStatus(2);
                    TaskApp.getTaskRepo().updateTask(taskss);
                } else {
                    TaskApp.getTaskRepo().insertTasks(tasks.get(index));
                }
            }
            viewAdapter.notifyItemChanged(position);
        }

    }

    @Override
    public void oncompleteclick(int position,String listname,String taskID) {
        //String taskid = options.getSnapshots().get(position).getTaskID();
        String taskid = "";
        int cposition = position - 1;
        int index = 0;
        if (listname.equals("upcoming")){
            for (int l = 0;l <upcomingtsks.size();l++){
                if (upcomingtsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = upcomingtsks.get(l).getTaskID();
                    break;
                }
            }
            tasks.clear();
            tasks.addAll(upcomingtsks);
        } else if (listname.equals("today")){
            for (int l = 0;l <todaytsks.size();l++){
                if (todaytsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = todaytsks.get(l).getTaskID();
                    break;
                }
            }
            tasks.clear();
            tasks.addAll(todaytsks);
        } else if (listname.equals("tomorrow")){
            for (int l = 0;l <tomorrowtsks.size();l++){
                if (tomorrowtsks.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = tomorrowtsks.get(l).getTaskID();
                    break;
                }
            }
            tasks.clear();
            tasks.addAll(tomorrowtsks);
        } else if (listname.equals("late")){
            for (int l = 0;l <tasksNew.size();l++){
                if (tasksNew.get(l).getTaskID()==taskID){
                    index = l;
                    taskid = tasksNew.get(l).getTaskID();
                    break;
                }
            }
            //taskid = tasksNew.get(position).getTaskID();
            tasks.clear();
            tasks.addAll(tasksNew);
        }
        db.collection("Tasks").document(taskid).update("taskStatus",4);
        Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(index).getTaskID());
        if (taskss!=null){
            taskss.setTaskStatus(4);
            TaskApp.getTaskRepo().updateTask(taskss);
        }
        else {
            TaskApp.getTaskRepo().insertTasks(taskss);
        }
        if (listname.equals("upcoming")){
            upcomingtsks.remove(index);
        } else if (listname.equals("today")){
            todaytsks.remove(index);
        } else if (listname.equals("tomorrow")){
            tomorrowtsks.remove(index);
        } else if (listname.equals("late")){
            tasksNew.remove(index);
        }
        recyclerView.removeViewAt(position);
        //TaskApp.getTaskRepo().deleteTasks(tasks.get(position).getTaskID());
        viewAdapter.notifyItemRemoved(position);
        viewAdapter.notifyDataSetChanged();


    }

    @Override
    public void onlongclick(int position, String listname,String taskID) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
