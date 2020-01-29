package com.ast.taskApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.ast.taskApp.Utils.PreferenceUtils;
import com.bumptech.glide.request.RequestOptions;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.indicators.PagerIndicator;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends MerlinActivity implements Connectable, Disconnectable, Bindable {

    ImageButton signin;
    GoogleSignInClient googleSignInClient;
    SliderLayout sliderlayout;
    PagerIndicator indicator;
    FirebaseFirestore db;
    String UserID;
    FirebaseAuth auth;
    GoogleSignInAccount googleSignIn;
    ArrayList<Tasks> todaytsks,tomorrowtsks,upcomingtsks;
    List<Tasks> tasks = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TaskDB taskDB;
    MerlinsBeard merlin;
    FirebaseUser firebaseUser;
    boolean newuser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            Window window = this.getWindow();
            //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.WHITE);

        setContentView(R.layout.activity_main);
        merlin = new MerlinsBeard.Builder().build(this);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signin = findViewById(R.id.sign_in);
        sliderlayout = findViewById(R.id.slider);
        indicator = findViewById(R.id.indicator);
        tasks = new ArrayList<>();
        todaytsks = new ArrayList<Tasks>();
        tomorrowtsks = new ArrayList<>();
        upcomingtsks = new ArrayList<>();
        taskDB = new TaskDB(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);

        googleSignIn = GoogleSignIn.getLastSignedInAccount(this);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in:

                    if (merlin.isConnected()){
                        signIn();
                    } else {
                        Toast.makeText(Login.this, "Kingly Connect to Internet First", Toast.LENGTH_SHORT).show();
                    }
                        //dbsave();
                    // ...
                }
            }
        });


        if (hasConnection()){
            HashMap<String,Integer> url_maps = new HashMap<String, Integer>();
            url_maps.put("1", R.mipmap.walk_vector_1);
            url_maps.put("2", R.mipmap.walk_vector_2);


            for(String name : url_maps.keySet()){
                // initialize a SliderLayout
                DefaultSliderView defaultSliderView = new DefaultSliderView(this);
                defaultSliderView
                        //.description(name)
                        .image(url_maps.get(name))
                        .setRequestOption(RequestOptions.fitCenterTransform());
                sliderlayout.addSlider(defaultSliderView);
                sliderlayout.setBackgroundColor(Color.WHITE);
            }



        }
        else {
            HashMap<String, Integer> url_maps = new HashMap<String, Integer>();
            url_maps.put("1", R.mipmap.walk_vector_1);
            url_maps.put("2", R.mipmap.walk_vector_2);


            for (String name : url_maps.keySet()) {
                // initialize a SliderLayout
                DefaultSliderView defaultSliderView = new DefaultSliderView(this);
                defaultSliderView
                        //.description(name)
                        .image(url_maps.get(name))
                        .setRequestOption(RequestOptions.fitCenterTransform());
                sliderlayout.addSlider(defaultSliderView);
                sliderlayout.setBackgroundColor(Color.WHITE);
            }
        }

        sliderlayout.setCustomIndicator(indicator);
        }

    /*private void dbsave() {

        String id = db.collection("Users").document().getId();

        Users users = new Users();
        users.setEmail();

        db.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Tasks<Void> task) {
            if(task.isSuccessful()){
                Toast.makeText(Login.this, "", Toast.LENGTH_SHORT).show();
            }
            else  {
                Toast.makeText(Login.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });
     *//*db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
         @Override
         public void onComplete(@NonNull Tasks<QuerySnapshot> task) {
             if (task.isSuccessful()){

             }
         }
     });*//*



    }*/



    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {

            final GoogleSignInAccount account = task.getResult(ApiException.class);
            googleSignIn = account;
            if (task.isSuccessful()) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

                final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                newuser = task.getResult().getAdditionalUserInfo().isNewUser();

                                firebaseUser = auth.getCurrentUser();
                                if (newuser) {


                                   /* Users users = new Users();
                                    users.setEmail(firebaseUser.getEmail());
                                    users.setFirstname(firebaseUser.getDisplayName());
                                    users.setFullname(account.getFamilyName());
                                    users.setIsLoggedin(1);
                                    users.setPlatform("Android");
                                    try {
                                        users.setProfilepic(account.getPhotoUrl().toString());
                                    } catch (Exception e) {
                                        users.setProfilepic(null);
                                    }
                                    users.setSubscriptionType(0);
                                    users.setUserID(db.collection("Users").document().getId());
                                    UserID = users.getUserID();*/
                                    String[] arr = firebaseUser.getDisplayName().split(" ");
                                    String firstname = arr[0].trim();

                                    HashMap<String, Object> users = new HashMap<>();
                                    users.put("email", firebaseUser.getEmail());
                                    users.put("firstName", firstname);
                                    users.put("fullName", firebaseUser.getDisplayName());
                                    users.put("isLoggedin", 1);
                                    users.put("platform", "Android");
                                    users.put("profilePicture", firebaseUser.getPhotoUrl().toString());
                                    users.put("subscriptionType", 0);
                                    users.put("userID", auth.getCurrentUser().getUid());

                                    db.collection("Users").document(auth.getCurrentUser().getUid()).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {


                                                Users users1 = new Users();
                                                users1.setEmail(firebaseUser.getEmail());
                                                users1.setFirstName(firstname);
                                                users1.setFullName(firebaseUser.getDisplayName());
                                                users1.setIsLoggedin(1);
                                                users1.setPlatform("Android");
                                                try {
                                                    users1.setProfilePicture(firebaseUser.getPhotoUrl().toString());
                                                } catch (Exception e) {
                                                    users1.setProfilePicture(null);
                                                }
                                                users1.setSubscriptionType(0);
                                                users1.setUserID(firebaseUser.getUid());

                                                TaskApp.getUserRepo().insertUser(users1);

                                                Intent intent = new Intent(Login.this, SubscriptionView.class);
                                                startActivity(intent);

                                            }
                                        }
                                    });
                                } else {

                                    db.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            db.collection("Users").document(firebaseUser.getUid()).update("isLoggedin", 1);
                                            Users users = task.getResult().toObject(Users.class);
                                            TaskApp.getUserRepo().updateUser(users);

                                            PreferenceUtils.setUser(users.getEmail(), users.getFirstName(), users.getFullName(), users.getIsLoggedin(), users.getPlatform(), users.getProfilePicture(), users.getSubscriptionType(), users.getUserID(), Login.this);

                                            db.collection("Tasks")
                                                    .whereEqualTo("userID", firebaseUser.getUid())
                                                    .orderBy("startTime", Query.Direction.DESCENDING)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                                                                Tasks tasks = snapshot.toObject(Tasks.class);

                                                                if (TaskApp.getTaskRepo().getAllTasks(firebaseUser.getUid()).size() != 0) {
                                                                    TaskApp.getTaskRepo().updateTask(tasks);
                                                                } else {
                                                                    TaskApp.getTaskRepo().insertTasks(tasks);
                                                                }

                                                            }

                                                            if (users.getSubscriptionType() == 0) {

                                                                Intent intent = new Intent(Login.this, SubscriptionView.class);
                                                                startActivity(intent);
                                                                finish();
                                                                //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();
                                                            } else {

                                                                Intent intent = new Intent(Login.this, Home.class);
                                                                startActivity(intent);
                                                                finish();
                                                                //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });

                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(Login.this, "Connectivity Lost", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
                else {
                    //Toast.makeText(Login.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

        } catch (Exception e ){
            Toast.makeText(this, "No User Found", Toast.LENGTH_SHORT).show();
        }
    }


    /*private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            if (googleSignIn == null){

                final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                googleSignIn = account;
                if (completedTask.isSuccessful()){

                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                            .requestEmail()
                            .build();

                    googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);


                    final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                    auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                *//*Toast.makeText(Login.this, "Signed In", Toast.LENGTH_SHORT).show();
                                Intent intent =  new Intent(Login.this,SubscriptionView.class);
                                startActivity(intent);*//*


                                *//*db.collection("Users").whereEqualTo("email",account.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            if (task.getResult().size()>0) {
                                                List<Users> userfound = task.getResult().toObjects(Users.class);
                                                taskDB.putString("userID", userfound.get(0).getUserID());
                                                if (TaskApp.getUserRepo().getUserById(userfound.get(0).getUserID())==null){
                                                    Users users = new Users();
                                                    users.setSubscriptionType(0);
                                                    users.setUserID(userfound.get(0).getUserID());
                                                    users.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                                    users.setPlatform("Android");
                                                    users.setIsLoggedin(1);
                                                    users.setFullname(googleSignIn.getGivenName() + "" + googleSignIn.getFamilyName());
                                                    users.setFirstname(googleSignIn.getGivenName());
                                                    users.setEmail(googleSignIn.getEmail());

                                                    TaskApp.getUserRepo().insertUser(users);
                                                }
                                                if (userfound.get(0).getSubscriptionType() == 0) {
                                                    Intent intent = new Intent(Login.this, SubscriptionView.class);
                                                    startActivity(intent);
                                                    //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Intent intent = new Intent(Login.this, Home.class);
                                                    startActivity(intent);
                                                    //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();
                                                }
                                                db.collection("Tasks").whereEqualTo("userID", userfound.get(0).getUserID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        tasks = task.getResult().toObjects(Tasks.class);
                                                        if (task.getResult().size()>0) {
                                                            if (TaskApp.getTaskRepo().getAllTasks(taskDB.getString("userID")).size() == 0) {
                                                                TaskApp.getTaskRepo().addAllTasks(tasks);
                                                            }
                                                        }
                                                    }

                                                });
                                                db.collection("Users").document(taskDB.getString("userID")).update("isLoggedin",1);
                                            } else {
                                                Users users = new Users();
                                                users.setEmail(account.getEmail());
                                                users.setFirstname(account.getGivenName());
                                                users.setFullname(account.getFamilyName());
                                                users.setIsLoggedin(1);
                                                users.setPlatform("Android");
                                                try {
                                                    users.setProfilepic(account.getPhotoUrl().toString());
                                                } catch (Exception e) {
                                                    users.setProfilepic(null);
                                                }
                                                users.setSubscriptionType(0);
                                                users.setUserID(db.collection("Users").document().getId());
                                                UserID = users.getUserID();

                                                db.collection("Users").document(UserID).set(users);
                                                if (TaskApp.getUserRepo().getUserById(UserID)==null){
                                                    Users usernotfound = new Users();
                                                    usernotfound.setSubscriptionType(0);
                                                    usernotfound.setUserID(UserID);
                                                    usernotfound.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                                    usernotfound.setPlatform("Android");
                                                    usernotfound.setIsLoggedin(1);
                                                    usernotfound.setFullname(googleSignIn.getGivenName() + "" + googleSignIn.getFamilyName());
                                                    usernotfound.setFirstname(googleSignIn.getGivenName());
                                                    usernotfound.setEmail(googleSignIn.getEmail());

                                                    TaskApp.getUserRepo().insertUser(usernotfound);
                                                }
                                                db.collection("Tasks").whereEqualTo("userID", UserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        tasks =  task.getResult().toObjects(Tasks.class);
                                                        if (task.getResult().size()>0) {
                                                            if (TaskApp.getTaskRepo().getAllTasks(UserID).size() == 0) {
                                                                TaskApp.getTaskRepo().addAllTasks(tasks);
                                                            }
                                                        }
                                                    }

                                                });

                                                Intent intent = new Intent(Login.this,SubscriptionView.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }

                                        else {

                                        }
                                    }
                                });*//*
                            }
                            else {

                                Toast.makeText(Login.this, "Not successfull", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            } else {

                db.collection("Users").whereEqualTo("email",googleSignIn.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().size()>0) {
                                List<Users> userfound = task.getResult().toObjects(Users.class);
                                if (TaskApp.getUserRepo().getUserById(userfound.get(0).getUserID())==null){
                                    Users users = new Users();
                                    users.setSubscriptionType(0);
                                    users.setUserID(userfound.get(0).getUserID());
                                    users.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                    users.setPlatform("Android");
                                    users.setIsLoggedin(1);
                                    users.setFullname(googleSignIn.getGivenName() + "" + googleSignIn.getFamilyName());
                                    users.setFirstname(googleSignIn.getGivenName());
                                    users.setEmail(googleSignIn.getEmail());

                                    TaskApp.getUserRepo().insertUser(users);
                                }
                                if (userfound.get(0).getSubscriptionType() == 0 || userfound.get(0).getSubscriptionType()==null)   {
                                    Intent intent = new Intent(Login.this, SubscriptionView.class);
                                    intent.putExtra("userID", userfound.get(0).getUserID());
                                    startActivity(intent);
                                    taskDB.putString("userID", userfound.get(0).getUserID());

                                    //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();

                                } else {
                                    Intent intent = new Intent(Login.this, Home.class);
                                    intent.putExtra("userID", userfound.get(0).getUserID());
                                    startActivity(intent);
                                    taskDB.putString("userID", userfound.get(0).getUserID());
                                    //Toast.makeText(Login.this, "Subscribed", Toast.LENGTH_SHORT).show();
                                }
                                db.collection("Tasks").whereEqualTo("userID", userfound.get(0).getUserID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        tasks = task.getResult().toObjects(Tasks.class);
                                        if (task.getResult().size()>0) {
                                            if (TaskApp.getTaskRepo().getAllTasks(taskDB.getString("userID")).size() == 0) {
                                                TaskApp.getTaskRepo().addAllTasks(tasks);
                                            }
                                        }
                                    }

                                });

                            } else {
                                Users users = new Users();
                                users.setEmail(googleSignIn.getEmail());
                                users.setFirstname(googleSignIn.getGivenName());
                                users.setFullname(googleSignIn.getFamilyName());
                                users.setIsLoggedin(1);
                                users.setPlatform("Android");
                                try {
                                    users.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                } catch (Exception e) {
                                    users.setProfilepic(null);
                                }
                                users.setSubscriptionType(0);
                                users.setUserID(db.collection("Users").document().getId());
                                UserID = users.getUserID();
                                taskDB.putString("userID", UserID);
                                Intent intent = new Intent(Login.this,SubscriptionView.class);
                                startActivity(intent);

                                if (TaskApp.getUserRepo().getUserById(UserID)==null){
                                    Users usernotfound = new Users();
                                    usernotfound.setSubscriptionType(0);
                                    usernotfound.setUserID(UserID);
                                    usernotfound.setProfilepic(googleSignIn.getPhotoUrl().toString());
                                    usernotfound.setPlatform("Android");
                                    usernotfound.setIsLoggedin(1);
                                    usernotfound.setFullname(googleSignIn.getGivenName() + "" + googleSignIn.getFamilyName());
                                    usernotfound.setFirstname(googleSignIn.getGivenName());
                                    usernotfound.setEmail(googleSignIn.getEmail());

                                    TaskApp.getUserRepo().insertUser(usernotfound);
                                }

                                db.collection("Users").document(UserID).set(users);

                                db.collection("Tasks").whereEqualTo("userID", UserID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        tasks = task.getResult().toObjects(Tasks.class);
                                        if (task.getResult().size()>0) {
                                            if (TaskApp.getTaskRepo().getAllTasks(UserID).size() == 0) {
                                                TaskApp.getTaskRepo().addAllTasks(tasks);
                                            }
                                        }
                                    }

                                });

                                //Toast.makeText(Login.this, "No user found", Toast.LENGTH_SHORT).show();
                            }


                        }
                        else {

                        }
                    }
                });
            }


        } catch (ApiException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    public  boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }


    @Override
    protected Merlin createMerlin() {
        return new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(this);
    }
    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (!networkStatus.isAvailable()) {
            onDisconnect();
        }
        else {
            onConnect();
        }
    }
    @Override
    public void onConnect() {
        //Toast.makeText(this, "Wifi Connected!", Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        //networkStatusDisplayer.displayPositiveMessage(R.string.connected, viewToAttachDisplayerTo);
    }

    @Override
    public void onDisconnect() {
        //Toast.makeText(this, "Wifi Disconnected!", Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerConnectable(this);
        registerDisconnectable(this);
        registerBindable(this);
    }

    public void FirebaseTest(){

    }


}
