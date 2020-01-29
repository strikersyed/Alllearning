package com.ast.taskApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.novoda.merlin.MerlinsBeard;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    FirebaseAuth auth;
    GoogleSignInAccount googleSignIn;
    FirebaseFirestore db;
    List<Tasks> tasks = new ArrayList<>();
    List<Users> userss = new ArrayList<>();
    MerlinsBeard merlinsBeard;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        googleSignIn = GoogleSignIn.getLastSignedInAccount(this);
        merlinsBeard = new MerlinsBeard.Builder().build(this);

        if (auth.getCurrentUser()==null){
            //startsplash();

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    Intent intent = new Intent(SplashActivity.this,Login.class);
                    startActivity(intent);
                    finish();


                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {
            //startsplash();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {

                    Intent intent = new Intent(SplashActivity.this,Home.class);
                    startActivity(intent);
                    finish();

                }
            }, SPLASH_DISPLAY_LENGTH);



            /*db.collection("Users").whereEqualTo("email",auth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        userss = task.getResult().toObjects(Users.class);
                        if (userss.size()>0){
                            db.collection("Tasks").whereEqualTo("userID",userss.get(0).getUserID()).orderBy("startTime", Query.Direction.ASCENDING)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    tasks = task.getResult().toObjects(Tasks.class);

                                    if (tasks.size()>0) {
                                        TaskApp.getTaskRepo().addAllTasks(tasks);
                                        startsplash();
                                    }

                                    startsplash();
                                }
                            });
                        }



                    }
                }
            });*/



        }

            /*if (googleSignIn == null) {
                startsplash();
            } else {
                if (googleSignIn.getIdToken() != null) {
                    final AuthCredential credential = GoogleAuthProvider.getCredential(googleSignIn.getIdToken(), null);
                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startsplash();
                                db.collection("Users").whereEqualTo("email", googleSignIn.getEmail()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                userss = task.getResult().toObjects(Users.class);
                                                if (task.getResult().size() > 0) {
                                                    if (TaskApp.getUserRepo().getUserById(userss.get(0).getUserID()) == null) {
                                                        Users users = new Users();
                                                        users.setSubscriptionType(0);
                                                        users.setUserID(userss.get(0).getUserID());
                                                        users.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                                        users.setPlatform("Android");
                                                        users.setIsLoggedin(1);
                                                        users.setFullname(googleSignIn.getGivenName() + "" + googleSignIn.getFamilyName());
                                                        users.setFirstname(googleSignIn.getGivenName());
                                                        users.setEmail(googleSignIn.getEmail());

                                                        TaskApp.getUserRepo().insertUser(users);

                                                        db.collection("Tasks").whereEqualTo("userID", userss.get(0).getUserID()).orderBy("startTime", Query.Direction.ASCENDING)
                                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (!(TaskApp.getTaskRepo().getAllTasks(users.getUserID()).size() > 0)) {
                                                                        tasks = task.getResult().toObjects(Tasks.class);
                                                                        TaskApp.getTaskRepo().addAllTasks(tasks);


                                                                    }
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        if (!(TaskApp.getTaskRepo().getAllTasks(userss.get(0).getUserID()).size() > 0)) {
                                                            db.collection("Tasks").whereEqualTo("userID", userss.get(0).getUserID()).orderBy("startTime", Query.Direction.ASCENDING)
                                                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (task.getResult().size() > 0) {
                                                                            tasks = task.getResult().toObjects(Tasks.class);
                                                                            TaskApp.getTaskRepo().addAllTasks(tasks);


                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }


                                                } else {

                                                    startsplash();
                                                }
                                            }
                                        });
                            } else {
                                startsplash();
                            }


                        }
                    });
                } else {
                    startsplash();
                }

            }*/


        if(Build.VERSION.SDK_INT >16) {
            Window window = this.getWindow();
            //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.blue));
        }
        setContentView(R.layout.activity_splash);

    }

    public void startsplash(){

    }
}
