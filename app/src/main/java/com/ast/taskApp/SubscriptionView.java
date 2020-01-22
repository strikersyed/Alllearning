package com.ast.taskApp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ast.taskApp.Activities.NewTaskActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscriptionView extends AppCompatActivity {


    ViewPager viewPager;
    PagerAdaper viewPagerAdapter;
    SpringDotsIndicator pagerIndicator;
    ImageButton cancel;
    TaskDB taskDB;
    Toolbar toolbar;
    RelativeLayout navigationparent;
    Window window;
    LinearLayout logout,todayoverview,calendar,overduetasks,addnewtask,upgrade,home;
    TextView name,email;
    GoogleSignInClient googleSignInClient;
    String personName,personEmail;
    Uri personPhoto;
    CircleImageView circularImageView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        window = this.getWindow();
        //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(getResources().getColor(R.color.blue));

        setContentView(R.layout.activity_subscription_view);
        toolbar = findViewById(R.id.toolbar);
        taskDB = new TaskDB(this);
        cancel = findViewById(R.id.cancel);

        pagerIndicator = findViewById(R.id.indicator);
        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new PagerAdaper(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        pagerIndicator.setViewPager(viewPager);
        viewPager.setPageMargin(10);
        viewPager.setClipToPadding(false);


        try {
            if (getIntent().getExtras().get("upgrade").equals("sidemenu")){
                cancel.setVisibility(View.GONE);
                new SlidingRootNavBuilder(SubscriptionView.this)
                        .withToolbarMenuToggle(toolbar)
                        .withMenuOpened(false)
                        .withMenuLayout(R.layout.slide_menulayout)
                        .inject();
                getGoogleData();
                InitializeViews();
                toolbar.setNavigationIcon(R.mipmap.menu_white);
                Glide.with(this).asBitmap().load(personPhoto).into(circularImageView);

                name.setText(personName);
                email.setText(personEmail);

                setListeners();
            }

        } catch (Exception e ){
            cancel.setVisibility(View.VISIBLE);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SubscriptionView.this,Home.class);
                    intent.putExtra("userID", taskDB.getString("userID"));
                    startActivity(intent);
                }
            });
        }



        viewPager.setPageTransformer(true,new CarouselPageTransformer());











    }

    private void setListeners() {
        overduetasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionView.this,TodayOverview.class);
                intent.putExtra("check","overduetasks");
                startActivity(intent);

            }
        });

        addnewtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionView.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionView.this, CalenderView.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskApp.getAuth().signOut();
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(SubscriptionView.this,Login.class);
                        startActivity(intent);
                    }
                });
            }
        });

        todayoverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionView.this,TodayOverview.class);
                intent.putExtra("chech","today");
                startActivity(intent);
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionView.this,Home.class);
                //intent.putExtra("check","overduetasks");
                startActivity(intent);
            }
        });
    }

    private void getGoogleData() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();


        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            //String personGivenName = acct.getGivenName();
            //String personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            //String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
        }
    }

    private void InitializeViews() {

        upgrade = findViewById(R.id.upgradetopro);
        navigationparent = findViewById(R.id.navigationparent);
        home = findViewById(R.id.homelayout);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        calendar = findViewById(R.id.calendar);
        todayoverview = findViewById(R.id.todayoverview);
        logout = findViewById(R.id.logout);
        addnewtask = findViewById(R.id.addnewtask);
        overduetasks = findViewById(R.id.overduetask);
        circularImageView = findViewById(R.id.profilepic);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


}
