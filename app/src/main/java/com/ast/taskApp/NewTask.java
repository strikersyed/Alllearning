package com.ast.taskApp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker;
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto;
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTask extends AppCompatActivity implements View.OnClickListener {

    Context context;
    TimePickerDialog tpd;
    DatePickerDialog dpd;
    Calendar calendar;
    EditText task_name, task_place;
    TextView start_time, end_time, start_date, end_date, imageselect;
    RelativeLayout imageupload, newtag;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInAccount googleSignInAccount;
    Button addnow;
    FirebaseFirestore db;
    Timestamp startdate, startime, enddate, endtime;
    private RadioGroup repeat;
    private RadioButton once, daily, weekly, monthly;
    Integer repeaton;
    ArrayList<String> weekdays;
    List<Users> users = new ArrayList<>();
    LinearLayout checkboxlayout, onstartlayout, onendlayout;
    CheckBox mon, tue, wed, thu, fri, sat, sun;
    FirebaseAuth auth;
    String taskid, photourl, durl;
    ImageView onstart, onend, unsplashimage, camera;
    ImageButton back_btn;
    MaterialSpinner ringtoneselect;
    List<Uri> ringtoneuris = new ArrayList<>();
    ArrayList<UnsplashPhoto> photos = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Calendar sdate, edate;
    private boolean isstart, isend, unsplash, cameraupl, imagechoosen;
    Window window;
    Uri tuneuri, imageuri;
    private StorageReference mStorageRef;
    private StorageTask uploadtask;
    Tasks tasks;
    public static CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME = "Plickd Notification";
    public static String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Plickd notification starts";
    public static String CHANNEL_ID = "PLICKD_NOTIFICATION";
    public static int NOTIFICATION_ID = 2;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    TaskDB taskDB;
    PeriodicWorkRequest StartWorkRequest;
    PeriodicWorkRequest EndWorkRequest;
    OneTimeWorkRequest oneTimeStartRequest;
    OneTimeWorkRequest oneTimeEndRequest;
    WorkManager workManager;
    PendingIntent monintent, tueintent, wedintent, thuintent, friintent, satintent, sunintent;
    int AlarmID;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        window = this.getWindow();
        //    window.setBackgroundDrawableResource(R.drawable.splash_bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.WHITE);
        setContentView(R.layout.activity_new_task);

        addnow = findViewById(R.id.Getitnow);

        googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();
        AlarmID = createID();
        //ringtoneselect = (MaterialSpinner) findViewById(R.id.spinner);
        taskDB = new TaskDB(this);
        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<String> tunespaths = new ArrayList<>(loadLocalRingtonesTitle());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ringtoneselect.setItems(tunespaths);
                    }
                });
            }
        });
        ringtoneuris = loadLocalRingtonesUris();*/
        //imageselect = findViewById(R.id.camera);
        workManager = WorkManager.getInstance();
        sdate = Calendar.getInstance();
        sdate.setTimeInMillis(System.currentTimeMillis());
        startime = new Timestamp(sdate.getTime());
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        imagechoosen = false;
        unsplash = false;
        cameraupl = false;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //unsplashimage = findViewById(R.id.unsplashimage);
        //camera = findViewById(R.id.cameraimg);
        onstartlayout = findViewById(R.id.onstartlayout);
        onendlayout = findViewById(R.id.onendlayout);
        isstart = true;
        isend = false;
        onstart = findViewById(R.id.onstart);
        onend = findViewById(R.id.onend);
        back_btn = findViewById(R.id.back_btn);
        auth = FirebaseAuth.getInstance();
        weekdays = new ArrayList<>();
        checkboxlayout = findViewById(R.id.innercehckboxlayout);
        once = findViewById(R.id.once);
        daily = findViewById(R.id.daily);
        weekly = findViewById(R.id.weekly);
        monthly = findViewById(R.id.monthly);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        repeat = findViewById(R.id.repeatgroup);
        db = FirebaseFirestore.getInstance();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //imageupload = findViewById(R.id.imageupld);
        newtag = findViewById(R.id.tasktag);
        task_name = findViewById(R.id.task_name);
        task_place = findViewById(R.id.task_place);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        tasks = new Tasks();
        taskid = db.collection("Tasks").document().getId();
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thu.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);
        photourl = "";
        imageuri = Uri.parse("");

        repeat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (once.isChecked()) {
                    once.setElevation(30f);
                    repeaton = 0;
                } else {
                    once.setElevation(0f);
                }

                if (daily.isChecked()) {
                    daily.setElevation(30f);
                    repeaton = 1;
                } else {
                    daily.setElevation(0f);
                }
                if (weekly.isChecked()) {
                    weekly.setElevation(30f);
                    repeaton = 2;
                } else {
                    weekly.setElevation(0f);
                }
                if (monthly.isChecked()) {
                    monthly.setElevation(30f);
                    repeaton = 3;
                } else {
                    monthly.setElevation(0f);
                }


            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewTask.this, Home.class);
                startActivity(intent);
            }
        });


        start_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                /*calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                sdate = Calendar.getInstance();

                tpd = new TimePickerDialog(NewTask.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int mHour, int mMinute) {

                        *//*Calendar datetime  = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, mHour);
                        datetime.set(Calendar.MINUTE, mMinute);*//*
                        sdate.set(Calendar.HOUR_OF_DAY, mHour);
                        sdate.set(Calendar.MINUTE, mMinute);


                        if (sdate.get(Calendar.AM_PM) == Calendar.AM) {
                            //start_time.setText(mHour+":"+mMinute+" AM");
                            //startime = new Timestamp(sdate.getTime());

                        } else if (sdate.get(Calendar.AM_PM) == Calendar.PM) {
                            //start_time.setText(mHour+":"+mMinute+" PM");
                            //startime = new Timestamp(sdate.getTime());
                        }

                        dpd.setTitle("Select Date");
                        dpd.show();

                    }
                }, hour, minute, false);

                tpd.setTitle("Select Time");
                tpd.show();

                dpd = new DatePickerDialog(NewTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        //start_date.setText(mMonth+"-"+mDay+"-"+mYear);

                        //Calendar datestart = Calendar.getInstance();
                        sdate.set(mYear, mMonth, mDay);
                        String hms = String.format("%02d:%02d",
                                sdate.get(Calendar.HOUR_OF_DAY),
                                sdate.get(Calendar.MINUTE));
                        if (sdate.get(Calendar.AM_PM) == 0) {
                            start_time.setText(sdate.get(Calendar.DAY_OF_MONTH) + "/" + (sdate.get(Calendar.MONTH) + 1) + "/" + sdate.get(Calendar.YEAR) + " " + hms + " AM");
                        } else {
                            start_time.setText(sdate.get(Calendar.DAY_OF_MONTH) + "/" + (sdate.get(Calendar.MONTH) + 1) + "/" + sdate.get(Calendar.YEAR) + " " + hms + " PM");
                        }
                        startime = new Timestamp(sdate.getTime());
                    }
                }, year, month, day);*/

                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);
                        long timeInMillis = datetime.getTimeInMillis();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                        Date date = new Date();
                        date.setTime(timeInMillis);
                        //start_time.setText( selectedHour + ":" + selectedMinute);
                        start_time.setText(dateFormat.format(date));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        });


        ringtoneselect.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewTask.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ringtoneuri", ringtoneuris.get(position).toString());
                editor.apply();
            }
        });

        end_time.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                edate = Calendar.getInstance();

                tpd = new TimePickerDialog(NewTask.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int mHour, int mMinute) {


                        edate.set(Calendar.HOUR_OF_DAY, mHour);
                        edate.set(Calendar.MINUTE, mMinute);

                        if (edate.get(Calendar.AM_PM) == Calendar.AM) {
                            //end_time.setText(mHour+":"+mMinute+" AM");
                            //endtime = new Timestamp(edate.getTime());
                        } else if (edate.get(Calendar.AM_PM) == Calendar.PM) {
                            //end_time.setText(mHour+":"+mMinute+" PM");
                            //endtime = new Timestamp(edate.getTime());
                        }
                        dpd.setTitle("Select Date");
                        dpd.show();
                    }
                }, hour, minute, false);
                tpd.setTitle("Select Time");
                tpd.show();

                dpd = new DatePickerDialog(NewTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        //end_date.setText(mMonth+"-"+mDay+"-"+mYear);
                        edate.set(mYear, mMonth, mDay);
                        String hms = String.format("%02d:%02d",
                                edate.get(Calendar.HOUR_OF_DAY),
                                edate.get(Calendar.MINUTE));
                        if (edate.get(Calendar.AM_PM) == 0) {
                            end_time.setText(edate.get(Calendar.DAY_OF_MONTH) + "/" + (edate.get(Calendar.MONTH) + 1) + "/" + edate.get(Calendar.YEAR) + " " + hms + " AM");
                        } else {
                            end_time.setText(edate.get(Calendar.DAY_OF_MONTH) + "/" + (edate.get(Calendar.MONTH) + 1) + "/" + edate.get(Calendar.YEAR) + " " + hms + " PM");
                        }
                        /*Calendar datestart = Calendar.getInstance();*/

                        endtime = new Timestamp(edate.getTime());
                    }
                }, year, month, day);
            }
        });

        /*start_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                final int day = Calendar.DAY_OF_MONTH;
                final int month = Calendar.MONTH;
                final int year = Calendar.YEAR;


                dpd = new DatePickerDialog(NewTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        start_date.setText(mMonth+"-"+mDay+"-"+mYear);

                        Calendar datestart = Calendar.getInstance();
                        sdate.set(mYear,mMonth,mDay);
                        startdate = new Timestamp(sdate.getTime());
                    }
                },year,month,day);
                dpd.setTitle("Select Date");
                dpd.show();
            }
        });*/

       /* end_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = Calendar.DAY_OF_MONTH;
                int month = Calendar.MONTH;
                int year = Calendar.YEAR;

                dpd = new DatePickerDialog(NewTask.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                        end_date.setText(mMonth+"-"+mDay+"-"+mYear);


                        Calendar datestart = Calendar.getInstance();
                        edate.set(mYear,mMonth,mDay);
                        enddate = new Timestamp(edate.getTime());
                    }
                },year,month,day);
                dpd.setTitle("Select Date");
                dpd.show();
            }
        });*/

        onstartlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isstart == true) {
                    onstart.setImageResource(R.mipmap.ringtone_on);
                    isstart = false;
                } else {
                    onstart.setImageResource(R.mipmap.viberation_on);
                    isstart = true;
                }
            }
        });


        onendlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isend == false) {
                    onend.setImageResource(R.mipmap.viberation_on);
                    isend = true;
                } else {
                    onend.setImageResource(R.mipmap.ringtone_on);
                    isend = false;
                }

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(NewTask.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(NewTask.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewTask.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    String fileName = "mountains";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                    imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, 2);
                    photourl = "";
                }


            }
        });

        unsplashimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UnsplashPhotoPicker.INSTANCE.init(
                        getApplication(),
                        "daaa0d70c7e6eb3f47daab9decbf54d97029ed9a3a3bfd80dea0505ad9ad52be",
                        "5aaad8f3e5c91c3004a66fe4e09da4ef1aa180045bd218feac7c16c31b2bb90d", 20);


                startActivityForResult(
                        UnsplashPickerActivity.Companion.getStartingIntent(
                                NewTask.this, // context
                                false
                        ), 7
                );


            }
        });

        addnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(NewTask.this, "Task has been created", Toast.LENGTH_SHORT).show();

                /*setAlarmStartTime(startime);
                setAlarmEndTime(endtime);*/
                Fileupload("Image");


               /* String chckname = name.getText().toString();
                chckname.replaceAll(" ", "");
                String chckplace = place.getText().toString();
                chckplace.replaceAll(" ", "");

                if (TextUtils.isEmpty(chckname) || chckname.length() == 0) {
                    name.setError("");
                } else if (TextUtils.isEmpty(chckplace) || chckplace.length() == 0) {
                    place.setError("");
                }*/


               /*db.collection("Users").whereEqualTo("email",googleSignInAccount.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()){
                           users = task.getResult().toObjects(Users.class);
                           tasks = new Tasks();
                           *//*if (imageuri == null || imageuri.equals("")){

                               tasks.setName(name.getText().toString());
                               tasks.setLocation(place.getText().toString());
                               tasks.setStartDate(startime);
                               tasks.setEndDate(endtime);
                               tasks.setStartTime(startime);
                               tasks.setEndTime(endtime);
                               tasks.setTaskImageUrl("");

                           } else {*//*
                               Fileupload("Image");


                           *//*}
                           if (photourl == null || photourl.equals("")) {
                               tasks.setTaskImageUrl("");

                           } else {
                               //Toast.makeText(NewTask.this,, Toast.LENGTH_SHORT).show();
                               tasks.setTaskImageUrl(photourl);
                           }
                           tasks.setTag("asdasd");
                           tasks.setRepeatOn(repeaton);
                           tasks.setPlatform("Android");
                           tasks.setTaskID(taskid);
                           tasks.setTaskStatus(0);
                           tasks.setVibrateeonStart(isstart);
                           tasks.setVibrateonEnd(isend);
                           tasks.setTuneName(ringtoneselect.getText().toString());
                           tasks.setTuneUrl(tuneuri.toString());
                           tasks.setWeekdays(weekdays);
                           tasks.setUserID(users.get(0).getUserID());
                           db.collection("Tasks").document(taskid).set(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   Toast.makeText(NewTask.this, "New Task Created Successfully", Toast.LENGTH_SHORT).show();
                                   Intent intent = new Intent(NewTask.this,Home.class);
                                   startActivity(intent);
                               }
                           });*//*
                       }
                   }
               });*/
            }
        });

        ringtoneselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mon:
                if (mon.isChecked()) {
                    mon.setElevation(30f);
                    if (weekdays.contains("Mo")) {
                        weekdays.remove("Mo");
                        weekdays.add("Mo");
                    } else {
                        weekdays.add("Mo");
                    }
                } else {
                    weekdays.remove("Mo");
                    mon.setElevation(0f);
                }
                break;

            case R.id.tue:
                if (tue.isChecked()) {
                    tue.setElevation(30f);
                    if (weekdays.contains("Tu")) {
                        weekdays.remove("Tu");
                        weekdays.add("Tu");
                    } else {
                        weekdays.add("Tu");
                    }
                } else {
                    weekdays.remove("Tu");
                    tue.setElevation(0f);
                }
                break;


            case R.id.wed:
                if (wed.isChecked()) {
                    wed.setElevation(30f);
                    if (weekdays.contains("We")) {
                        weekdays.remove("We");
                        weekdays.add("We");
                    } else {
                        weekdays.add("We");
                    }
                } else {
                    weekdays.remove("We");
                    wed.setElevation(0f);
                }
                break;

            case R.id.thu:
                if (thu.isChecked()) {
                    thu.setElevation(30f);
                    if (weekdays.contains("Th")) {
                        weekdays.remove("Th");
                        weekdays.add("Th");
                    } else {
                        weekdays.add("Th");
                    }
                } else {
                    weekdays.remove("Th");
                    thu.setElevation(0f);
                }
                break;

            case R.id.fri:
                if (fri.isChecked()) {
                    fri.setElevation(30f);
                    if (weekdays.contains("Fr")) {
                        weekdays.remove("Fr");
                        weekdays.add("Fr");
                    } else {
                        weekdays.add("Fr");
                    }
                } else {
                    weekdays.remove("Fr");
                    fri.setElevation(0f);
                }
                break;

            case R.id.sat:
                if (sat.isChecked()) {
                    sat.setElevation(30f);
                    if (weekdays.contains("Sa")) {
                        weekdays.remove("Sa");
                        weekdays.add("Sa");
                    } else {
                        weekdays.add("Sa");
                    }
                } else {
                    weekdays.remove("Sa");
                    sat.setElevation(0f);
                }
                break;

            case R.id.sun:
                if (sun.isChecked()) {
                    sun.setElevation(30f);
                    if (weekdays.contains("Su")) {
                        weekdays.remove("Su");
                        weekdays.add("Su");
                    } else {
                        weekdays.add("Su");
                    }
                } else {
                    weekdays.remove("Su");
                    sun.setElevation(0f);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            photos = data.getExtras().getParcelableArrayList(UnsplashPickerActivity.EXTRA_PHOTOS);
            photourl = photos.get(0).getUrls().getSmall();
            photos.get(0).getLinks().getDownload();
            imageselect.setText(photos.get(0).getId());
            if (photourl == null || photourl.equals("")) {

            } else {
                imageuri = Uri.parse("");
            }

        }

        if (requestCode == 5 && resultCode == RESULT_OK) {
            tuneuri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (tuneuri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, tuneuri);
                String title = ringtone.getTitle(this);
                ringtoneselect.setText(title);
            } else {
                ringtoneselect.setText("No Ringtone Available");
            }
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {


        }
    }


    private String getExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mp = MimeTypeMap.getSingleton();
        return mp.getExtensionFromMimeType(cr.getType(uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Fileupload(final String type) {

        Data.Builder data = new Data.Builder();
        data.putString("TaskID", taskid);

        if (imageuri == null || imageuri.toString() == "") {
            if (task_name.getText().equals("") || task_name.getText() == null) {
                tasks.setName("");
                //data.putString("name","");
            } else {
                tasks.setName(task_name.getText().toString());
                //data.putString("name",name.getText().toString());
            }
            if (task_place.getText().equals("") || task_place.getText() == null) {
                tasks.setLocation("");
            } else {
                tasks.setLocation(task_place.getText().toString());
            }
            tasks.setStartDate(startime);
            data.putLong("endtime", startime.toDate().getTime());
            tasks.setEndDate(endtime);
            tasks.setStartTime(startime);
            tasks.setEndTime(endtime);
            //setAlarmStartTime(startime);
            if (endtime == null) {
            } else {
                //setAlarmEndTime(endtime);
                data.putLong("endtime", endtime.toDate().getTime());
            }
            if (photourl.equals("") || photourl.equals(null)) {
                tasks.setTaskImageUrl("");
                data.putString("imageurl", photourl);
            } else {
                tasks.setTaskImageUrl(photourl);
                data.putString("imageurl", photourl);
            }
            tasks.setTag("asdasd");
            tasks.setRepeatOn(repeaton);
            tasks.setPlatform("Android");
            tasks.setTaskStatus(0);
            tasks.setVibrateeonStart(isstart);
            data.putBoolean("vibrationstart", isstart);
            tasks.setVibrateonEnd(isend);
            data.putBoolean("vibrationend", isend);
            if (ringtoneselect.getText().toString().equals("")) {
                tasks.setTuneName("");
            } else {
                tasks.setTuneName(ringtoneselect.getText().toString());
                data.putString("ringname", ringtoneselect.getText().toString());
            }
            if (tuneuri == null || tuneuri.toString().equals("")) {
                tasks.setTuneUrl("");
            } else {
                tasks.setTuneUrl(tuneuri.toString());
                data.putString("ringtone", tuneuri.toString());
            }

            tasks.setWeekdays(weekdays);
            tasks.setUserID(auth.getCurrentUser().getUid());
            tasks.setTaskID(taskid);
            tasks.setFromSplash("1");
            tasks.setAlaramID(AlarmID);
            db.collection("Tasks").document(taskid).set(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        makeStatusNotification(false, "Task Created", "Success Your task has been created.", NewTask.this);
                        Toast.makeText(NewTask.this, "Your task has been created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NewTask.this, Home.class);
                        startActivity(intent);
                        setstartalarmtime(startime);
                        setendalarmtime(endtime);
                        TaskApp.getTaskRepo().insertTasks(tasks);


                    }
                }
            });


        } else {
            final String rl = getExtension(imageuri);
            final StorageReference Ref = mStorageRef.child("Tasks").child(taskid).child("Attatchment").child("mountains" + "." + rl);
            uploadtask = Ref.putFile(imageuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    durl = uri.toString();
                                    if (task_name.getText().equals("") || task_name.getText() == null) {
                                        tasks.setName("");
                                        data.putString("name", "");
                                    } else {
                                        tasks.setName(task_name.getText().toString());
                                        data.putString("name", task_name.getText().toString());
                                    }
                                    if (task_place.getText().equals("") || task_place.getText() == null) {
                                        tasks.setLocation("");
                                    } else {
                                        tasks.setLocation(task_place.getText().toString());
                                    }
                                    tasks.setStartDate(startime);
                                    tasks.setEndDate(endtime);
                                    tasks.setStartTime(startime);
                                    tasks.setEndTime(endtime);
                                    data.putLong("endtime", startime.toDate().getTime());
                                    if (endtime == null) {

                                    } else {
                                        //setAlarmEndTime(endtime);
                                        data.putLong("endtime", endtime.toDate().getTime());
                                    }

                                    if (durl.equals("") || durl == null) {
                                        tasks.setTaskImageUrl("");
                                        data.putString("imageurl", "");
                                    } else {
                                        tasks.setTaskImageUrl(durl);
                                        data.putString("imageurl", durl);
                                    }
                                    tasks.setTag("asdasd");
                                    tasks.setRepeatOn(repeaton);
                                    tasks.setPlatform("Android");
                                    tasks.setTaskStatus(0);
                                    tasks.setVibrateeonStart(isstart);
                                    data.putBoolean("vibrationstart", isstart);
                                    tasks.setVibrateonEnd(isend);
                                    data.putBoolean("vibrationend", isend);
                                    if (ringtoneselect.getText().toString().equals("")) {
                                        tasks.setTuneName("");
                                    } else {
                                        tasks.setTuneName(ringtoneselect.getText().toString());
                                        data.putString("ringname", ringtoneselect.getText().toString());
                                    }
                                    if (tuneuri.toString().equals("") || tuneuri.toString() == null) {
                                        tasks.setTuneUrl("");
                                    } else {
                                        tasks.setTuneUrl(tuneuri.toString());
                                        data.putString("ringtone", tuneuri.toString());
                                    }

                                    tasks.setWeekdays(weekdays);
                                    tasks.setUserID(auth.getCurrentUser().getUid());
                                    tasks.setTaskID(taskid);
                                    tasks.setFromSplash("0");
                                    tasks.setAlaramID(AlarmID);
                                    db.collection("Tasks").document(taskid).set(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                makeStatusNotification(false, "Task Created", "Success Your task has been created.", NewTask.this);
                                                Toast.makeText(NewTask.this, "Your task has been created", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewTask.this, Home.class);
                                                startActivity(intent);
                                                setstartalarmtime(startime);
                                                setendalarmtime(endtime);
                                                TaskApp.getTaskRepo().insertTasks(tasks);
                                            }
                                        }
                                    });

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //Toast.makeText(NewTask.this,exception.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

        }


    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;

    }



    /*private List<Uri> loadLocalRingtonesUris() {
        List<Uri> alarms = new ArrayList<>();
        try {
            RingtoneManager ringtoneMgr = new RingtoneManager(this);
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE);

            Cursor alarmsCursor = ringtoneMgr.getCursor();
            int alarmsCount = alarmsCursor.getCount();
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close();
                return null;
            }
            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                int currentPosition = alarmsCursor.getPosition();
                alarms.add(ringtoneMgr.getRingtoneUri(currentPosition));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return alarms;
    }*/


    /*private List<String> loadLocalRingtonesTitle() {
        List<String> alarms = new ArrayList<>();
        try {
            RingtoneManager ringtoneMgr = new RingtoneManager(this);
            ringtoneMgr.setType(RingtoneManager.TYPE_RINGTONE);

            Cursor alarmsCursor = ringtoneMgr.getCursor();
            int alarmsCount = alarmsCursor.getCount();
            if (alarmsCount == 0 && !alarmsCursor.moveToFirst()) {
                alarmsCursor.close();
                return null;
            }
            while (!alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
                int currentPosition = alarmsCursor.getPosition();
                alarms.add(ringtoneMgr.getRingtone(currentPosition).getTitle(this));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return alarms;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(NewTask.this, Home.class);
        startActivity(intent);

    }

    public static Notification makeStatusNotification(boolean isForeground, String title, String message, Context context) {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = VERBOSE_NOTIFICATION_CHANNEL_NAME;
            String description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Add the channel
            android.app.NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notificationBuilder.setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setColor(context.getResources().getColor(R.color.blue))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);
        if (isForeground) {
            notificationBuilder.setProgress(0, 0, true)
                    .build();
        } else {
            notificationBuilder.build();
        }
        if (!isForeground) {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build());
        }
        return notificationBuilder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setstartalarmtime(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        /*calendar.set(Calendar.DAY_OF_MONTH,timestamp.toDate().getDate());
        calendar.set(Calendar.HOUR_OF_DAY,timestamp.toDate().getHours());
        calendar.set(Calendar.MINUTE,timestamp.toDate().getMinutes());*/
        calendar.setTimeInMillis(timestamp.toDate().getTime());

        Intent myIntent = new Intent(NewTask.this, AlarmReceiver.class);
        myIntent.putExtra("TaskID", taskid);
        if (isstart == true) {
            myIntent.putExtra("vibration", true);
            myIntent.putExtra("ringcheck", false);
        } else {
            myIntent.putExtra("vibration", false);
            if (ringtoneselect.getText() == "" || ringtoneselect.getText().toString().isEmpty()) {
                myIntent.putExtra("ringcheck", false);
            } else {
                myIntent.putExtra("ringcheck", true);
                myIntent.putExtra("ringtone", tuneuri.toString());
            }
        }

        myIntent.putExtra("name", task_name.getText().toString());
        myIntent.setAction("starttask");


        /*pendingIntent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);*/

        if (repeaton == 0) {
            /*if (mon.isChecked()){
                calendar.set(Calendar.DAY_OF_MONTH,Calendar.MONDAY);
                monintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);;
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), monintent);
            } if (tue.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.TUESDAY);
                tueintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), tueintent);
            } if (wed.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.WEDNESDAY);
                wedintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), wedintent);
            } if (thu.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.THURSDAY);
                thuintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), thuintent);
            } if (fri.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.FRIDAY);
                friintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), friintent);
            } if (sat.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.SATURDAY);
                satintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), satintent);
            } if (sun.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.SUNDAY);
                sunintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), sunintent);
            } else if (weekdays.size()==0) {*/
            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


        } else if (repeaton == 1) {
            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } else if (repeaton == 2) {
            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), DateUtils.WEEK_IN_MILLIS, pendingIntent);

        } else if (repeaton == 3) {
            Calendar monthlyCalendar = Calendar.getInstance();
            //monthlyCalendar.add(Calendar.MONTH, 1);


            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),getDuration(),pendingIntent);


            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.JANUARY || monthlyCalendar.get(Calendar.MONTH) == Calendar.MARCH || monthlyCalendar.get(Calendar.MONTH) == Calendar.MAY || monthlyCalendar.get(Calendar.MONTH) == Calendar.JULY
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.AUGUST || monthlyCalendar.get(Calendar.MONTH) == Calendar.OCTOBER || monthlyCalendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 31, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.APRIL || monthlyCalendar.get(Calendar.MONTH) == Calendar.JUNE || monthlyCalendar.get(Calendar.MONTH) == Calendar.SEPTEMBER
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, pendingIntent);
                //for feburary month)
                //GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
                /*if(cal.isLeapYear(year)){//for leap year feburary month
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 29, alarmIntent);
                }
                else{ //for non leap year feburary month
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, alarmIntent);
                }*/
            }

        }
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_HOUR,pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setendalarmtime(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.toDate().getTime());
        Intent myIntent = new Intent(NewTask.this, AlarmReceiver.class);
        myIntent.putExtra("TaskID", taskid);
        if (isend == true) {
            myIntent.putExtra("vibration", true);
            myIntent.putExtra("ringcheck", false);
        } else {
            myIntent.putExtra("vibration", false);
            if (ringtoneselect.getText() == "" || ringtoneselect.getText().toString().isEmpty()) {
                myIntent.putExtra("ringcheck", false);
            } else {
                myIntent.putExtra("ringcheck", true);
                myIntent.putExtra("ringtone", tuneuri.toString());
            }
        }


        myIntent.putExtra("name", task_name.getText().toString());
        myIntent.setAction("endTask");

        if (repeaton == 0) {
            /*if (mon.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.MONDAY);
                monintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), monintent);
            }if (tue.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.TUESDAY);
                tueintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), tueintent);
            }if (wed.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.WEDNESDAY);
                wedintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), wedintent);
            } if (thu.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.THURSDAY);
                thuintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), thuintent);
            } if (fri.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.FRIDAY);
                friintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), friintent);
            } if (sat.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.SATURDAY);
                satintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), satintent);
            } if (sun.isChecked()) {
                calendar.set(Calendar.DAY_OF_MONTH, Calendar.SUNDAY);
                sunintent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), sunintent);
            }*/

            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


        } else if (repeaton == 1) {
            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } else if (repeaton == 2) {
            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), DateUtils.WEEK_IN_MILLIS, pendingIntent);

        } else if (repeaton == 3) {
            Calendar monthlyCalendar = Calendar.getInstance();
            //monthlyCalendar.add(Calendar.MONTH, 1);


            pendingIntent = PendingIntent.getBroadcast(NewTask.this, AlarmID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),getDuration(),pendingIntent);


            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.JANUARY || monthlyCalendar.get(Calendar.MONTH) == Calendar.MARCH || monthlyCalendar.get(Calendar.MONTH) == Calendar.MAY || monthlyCalendar.get(Calendar.MONTH) == Calendar.JULY
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.AUGUST || monthlyCalendar.get(Calendar.MONTH) == Calendar.OCTOBER || monthlyCalendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 31, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.APRIL || monthlyCalendar.get(Calendar.MONTH) == Calendar.JUNE || monthlyCalendar.get(Calendar.MONTH) == Calendar.SEPTEMBER
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, pendingIntent);
                //for feburary month)
                //GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
                /*if(cal.isLeapYear(year)){//for leap year feburary month
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 29, alarmIntent);
                }
                else{ //for non leap year feburary month
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, alarmIntent);
                }*/
            }

            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
        }


    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }


}
