package com.ast.taskApp.Activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.ast.taskApp.AlarmReceiver;
import com.ast.taskApp.BaseClasses.BaseActivity;
import com.ast.taskApp.Home;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.Models.Users;
import com.ast.taskApp.R;
import com.ast.taskApp.TaskApp;
import com.ast.taskApp.TaskDB;
import com.ast.taskApp.Utils.Constants;
import com.ast.taskApp.Utils.PreferenceUtils;
import com.ast.taskApp.Workers.TaskStartWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.novoda.merlin.MerlinsBeard;
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker;
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto;
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewTaskActivity extends BaseActivity implements View.OnClickListener {

    Context context;

    TaskDB taskDB;
    Calendar calendar;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
    TimePickerDialog timePickerDialog;
    Tasks tasks;
    Date startTime;
    Date endTime;
    long startTimeInMillis;
    long endTimeInMillis;
    ArrayList<String> daysList;
    Constraints constraints;

    MaterialSpinner ringtone_selector;
    private EditText task_name, task_place;
    private ImageButton unsplash_btn, camera_btn;
    private ImageButton onstart, onend;
    private Button add_btn;
    private ImageButton back_btn;
    private CheckBox mon, tue, wed, thu, fri, sat, sun;
    private TextView image_name;
    private LinearLayout taskName_layout;
    WorkManager workManager;
    private boolean isStart = true;
    private boolean isEnd = false;
    private String taskTag = "testing";
    private String platform = "Android";
    private int taskStatus = 0;
    private int alarmId;
    private String taskid;
    private String photourl;
    private Uri tuneUri, imageUri;
    MerlinsBeard merlinsBeard;
    OneTimeWorkRequest oneTimeWorkRequest;

    //ProgressDialog dialog;


    TimePickerDialog tpd;
    DatePickerDialog dpd;

    TextView start_time, end_time, start_date, end_date;
    RelativeLayout imageupload, newtag;

    //Timestamp startdate, startime, enddate, endtime;
    private RadioGroup repeat;
    private RadioButton once, daily, weekly, monthly;
    Integer repeatOn;
    List<Users> users = new ArrayList<>();
    String durl;


    List<Uri> ringtoneuris = new ArrayList<>();
    ArrayList<UnsplashPhoto> photos = new ArrayList<>();
    SharedPreferences sharedPreferences;
    Calendar sdate, edate;


    private boolean unsplash, cameraupl, imagechoosen;
    Window window;

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    PendingIntent monintent, tueintent, wedintent, thuintent, friintent, satintent, sunintent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_new_task);
        setUpViews();
        daysSelector();
    }

    private void setUpViews() {
        workManager = WorkManager.getInstance();
        taskDB = new TaskDB(this);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();
        startTime = new Date();
        endTime = new Date();
        tasks = new Tasks();
        daysList = new ArrayList<>();

        //dialog = new ProgressDialog(this);
        merlinsBeard = new MerlinsBeard.Builder().build(this);
        back_btn = findViewById(R.id.back_btn);
        task_name = findViewById(R.id.task_name);
        task_place = findViewById(R.id.task_place);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        ringtone_selector = findViewById(R.id.ringtone_selector);
        unsplash_btn = findViewById(R.id.unsplash_btn);
        camera_btn = findViewById(R.id.camera_btn);
        onstart = findViewById(R.id.onstart);
        onend = findViewById(R.id.onend);
        add_btn = findViewById(R.id.add_btn);
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);
        image_name = findViewById(R.id.image_name);
        repeat = findViewById(R.id.repeatgroup);
        taskName_layout = findViewById(R.id.taskName_layout);

        once = findViewById(R.id.once);
        daily = findViewById(R.id.daily);
        weekly = findViewById(R.id.weekly);
        monthly = findViewById(R.id.monthly);

        back_btn.setOnClickListener(this);
        start_time.setOnClickListener(this);
        end_time.setOnClickListener(this);
        ringtone_selector.setOnClickListener(this);
        unsplash_btn.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        onstart.setOnClickListener(this);
        onend.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        repeat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (once.isChecked()) {
                    once.setElevation(30f);
                    repeatOn = 0;
                } else {
                    once.setElevation(0f);
                }

                if (daily.isChecked()) {
                    daily.setElevation(30f);
                    repeatOn = 1;
                } else {
                    daily.setElevation(0f);
                }
                if (weekly.isChecked()) {
                    weekly.setElevation(30f);
                    repeatOn = 2;
                } else {
                    weekly.setElevation(0f);
                }
                if (monthly.isChecked()) {
                    monthly.setElevation(30f);
                    repeatOn = 3;
                } else {
                    monthly.setElevation(0f);
                }
            }
        });

        ringtone_selector.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {

                PreferenceUtils.setRingtoneUri(ringtoneuris.get(position).toString(), context);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn: {
                Intent intent = new Intent(context, Home.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.start_time: {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);
                        datetime.set(Calendar.SECOND, 0);
                        startTimeInMillis = datetime.getTimeInMillis();

                        startTime.setTime(startTimeInMillis);
                        start_time.setText(dateFormat.format(startTime));
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();
                break;
            }
            case R.id.end_time: {

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        datetime.set(Calendar.MINUTE, selectedMinute);
                        datetime.set(Calendar.SECOND, 0);
                        endTimeInMillis = datetime.getTimeInMillis();

                        endTime.setTime(endTimeInMillis);
                        end_time.setText(dateFormat.format(endTime));
                    }
                }, hour, minute, false);

                timePickerDialog.setTitle("Select End Time");
                timePickerDialog.show();
                break;
            }
            case R.id.ringtone_selector: {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, Constants.RINGTONE_REQUEST);
                break;
            }
            case R.id.unsplash_btn: {
                UnsplashPhotoPicker.INSTANCE.init(
                        getApplication(),
                        "daaa0d70c7e6eb3f47daab9decbf54d97029ed9a3a3bfd80dea0505ad9ad52be",
                        "5aaad8f3e5c91c3004a66fe4e09da4ef1aa180045bd218feac7c16c31b2bb90d", 20);

                startActivityForResult(
                        UnsplashPickerActivity.Companion.getStartingIntent(
                                context, // context
                                false
                        ), Constants.UNSPLASH_REQUEST
                );
                break;
            }
            case R.id.camera_btn: {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewTaskActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    String fileName = "mountains";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, Constants.CAMERA_REQUEST);
                }
                break;
            }
            case R.id.onstart: {
                if (isStart) {
                    onstart.setImageResource(R.mipmap.ringtone_on);
                    isStart = false;
                } else {
                    onstart.setImageResource(R.mipmap.viberation_on);
                    isStart = true;
                }
                break;
            }
            case R.id.onend: {
                if (isEnd) {
                    onend.setImageResource(R.mipmap.ringtone_on);
                    isEnd = false;
                } else {
                    onend.setImageResource(R.mipmap.viberation_on);
                    isEnd = true;
                }
                break;
            }
            case R.id.add_btn: {
                createTask();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Constants.UNSPLASH_REQUEST && data != null) {
                imageUri = null;
                photos = data.getExtras().getParcelableArrayList(UnsplashPickerActivity.EXTRA_PHOTOS);
                photourl = photos.get(0).getUrls().getSmall();
                photos.get(0).getLinks().getDownload();
                image_name.setText(photos.get(0).getId());
            } else if (requestCode == Constants.CAMERA_REQUEST) {
                photourl = null;
                //imageUri = data.getData();
            } else if (requestCode == Constants.RINGTONE_REQUEST && data != null) {
                tuneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (tuneUri != null) {
                    Ringtone ringtone = RingtoneManager.getRingtone(this, tuneUri);
                    String title = ringtone.getTitle(this);
                    ringtone_selector.setText(title);
                } else {
                    ringtone_selector.setText("No Ringtone Available");
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Image selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mp = MimeTypeMap.getSingleton();
        return mp.getExtensionFromMimeType(cr.getType(uri));
    }

    private void createTask() {

        //dialog.setMessage("Creating new task...");
        //dialog.show();

        taskid = TaskApp.getFirestore().collection("Tasks").document().getId();
        String userId = TaskApp.getAuth().getCurrentUser().getUid();
        String taskName = task_name.getText().toString();
        String taskPlace = task_place.getText().toString();
        String ringtone = ringtone_selector.getText().toString();

        alarmId = createID();

        Data.Builder data = new Data.Builder();
        data.putString("TaskID", taskid);
        /*data.putLong("starttime", startTimeInMillis);
        data.putLong("endtime", endTimeInMillis);
        data.putBoolean("vibrationstart", isStart);
        data.putBoolean("vibrationend", isEnd);
        data.putString("ringname", ringtone);
        data.putString("ringtone", tuneUri.toString());*/

        if (photourl != null && imageUri == null) {

            data.putString("imageurl", photourl);
            tasks.setName(taskName);
            tasks.setLocation(taskPlace);
            tasks.setStartTime(new Timestamp(startTime));
            tasks.setStartDate(new Timestamp(startTime));
            tasks.setEndTime(new Timestamp(endTime));
            tasks.setEndDate(new Timestamp(endTime));
            tasks.setTaskImageUrl(photourl);
            tasks.setTag(taskTag);
            tasks.setRepeatOn(repeatOn);
            tasks.setPlatform(platform);
            tasks.setTaskStatus(taskStatus);
            tasks.setVibrateeonStart(isStart);
            tasks.setVibrateonEnd(isEnd);
            tasks.setTuneName(ringtone);
            tasks.setTuneUrl(tuneUri.toString());
            tasks.setWeekdays(daysList);
            tasks.setUserID(userId);
            tasks.setTaskID(taskid);
            tasks.setFromSplash("1");
            tasks.setAlaramID(alarmId);



            oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TaskStartWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data.build())
                    .build();
            workManager.enqueue(oneTimeWorkRequest);
            /*TaskApp.getFirestore().collection("Tasks")
                    .document(taskid)
                    .set(tasks);*/
            makeStatusNotification(false, "Task Created", "Success Your task has been created.", context);
            Toast.makeText(getApplicationContext(), "Your task has been created", Toast.LENGTH_SHORT).show();
            setAlarmStartTime(startTimeInMillis);
            setAlarmEndTime(endTimeInMillis);
            TaskApp.getTaskRepo().insertTasks(tasks);
        } else if (imageUri != null && photourl == null) {

            final StorageReference fileRef = TaskApp.getStorage().child("Tasks").child(taskid).child("Attatchment").child("mountains" + "." + getExtension(imageUri));
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            data.putString("imageurl", uri.toString());
                                            tasks.setName(taskName);
                                            tasks.setLocation(taskPlace);
                                            tasks.setStartTime(new Timestamp(startTime));
                                            tasks.setStartDate(new Timestamp(startTime));
                                            tasks.setEndTime(new Timestamp(endTime));
                                            tasks.setEndDate(new Timestamp(endTime));
                                            tasks.setTaskImageUrl(uri.toString());
                                            tasks.setTag(taskTag);
                                            tasks.setRepeatOn(repeatOn);
                                            tasks.setPlatform(platform);
                                            tasks.setTaskStatus(taskStatus);
                                            tasks.setVibrateeonStart(isStart);
                                            tasks.setVibrateonEnd(isEnd);
                                            tasks.setTuneName(ringtone);
                                            tasks.setTuneUrl(tuneUri.toString());
                                            tasks.setWeekdays(daysList);
                                            tasks.setUserID(userId);
                                            tasks.setTaskID(taskid);
                                            tasks.setFromSplash("0");
                                            tasks.setAlaramID(alarmId);


                                            oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TaskStartWorker.class)
                                                    .setConstraints(constraints)
                                                    .setInputData(data.build())
                                                    .build();
                                            workManager.enqueue(oneTimeWorkRequest);
                                            makeStatusNotification(false, "Task Created", "Success Your task has been created.", context);
                                            Toast.makeText(getApplicationContext(), "Your task has been created", Toast.LENGTH_SHORT).show();
                                            setAlarmStartTime(startTimeInMillis);
                                            setAlarmEndTime(endTimeInMillis);
                                            TaskApp.getTaskRepo().insertTasks(tasks);

                                            /*TaskApp.getFirestore().collection("Tasks")
                                                    .document(taskid)
                                                    .set(tasks)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                makeStatusNotification(false, "Task Created", "Success Your task has been created.", context);
                                                                Toast.makeText(getApplicationContext(), "Your task has been created", Toast.LENGTH_SHORT).show();
                                                                //Intent intent = new Intent(context, Home.class);
                                                                //startActivity(intent);
                                                                setAlarmStartTime(startTimeInMillis);
                                                                //setAlarmEndTime(new Timestamp(endTime));
                                                                TaskApp.getTaskRepo().insertTasks(tasks);
                                                                //dialog.dismiss();
                                                            }
                                                        }
                                                    });*/

                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(context, Home.class);
        startActivity(intent);
    }

    public void setAlarmStartTime(long startTime) {

        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra("taskID", taskid);
        myIntent.putExtra("taskName", task_name.getText().toString());
        myIntent.putExtra("ringtone", tuneUri.toString());

        if (isStart) {
            myIntent.putExtra("vibration", true);
            myIntent.putExtra("ringcheck", false);
        } else {
            myIntent.putExtra("vibration", false);
            myIntent.putExtra("ringcheck", true);
        }
        myIntent.setAction("startTask");




        /*pendingIntent = PendingIntent.getBroadcast(NewTask.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);*/

        if (repeatOn == 0) {
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
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, startTime, pendingIntent);


        } else if (repeatOn == 1) {
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, startTime, AlarmManager.INTERVAL_DAY, pendingIntent);

        } else if (repeatOn == 2) {
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, startTime, DateUtils.WEEK_IN_MILLIS, pendingIntent);

        } else if (repeatOn == 3) {
            Calendar monthlyCalendar = Calendar.getInstance();
            //monthlyCalendar.add(Calendar.MONTH, 1);


            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),getDuration(),pendingIntent);


            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.JANUARY || monthlyCalendar.get(Calendar.MONTH) == Calendar.MARCH || monthlyCalendar.get(Calendar.MONTH) == Calendar.MAY || monthlyCalendar.get(Calendar.MONTH) == Calendar.JULY
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.AUGUST || monthlyCalendar.get(Calendar.MONTH) == Calendar.OCTOBER || monthlyCalendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY * 31, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.APRIL || monthlyCalendar.get(Calendar.MONTH) == Calendar.JUNE || monthlyCalendar.get(Calendar.MONTH) == Calendar.SEPTEMBER
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY * 28, pendingIntent);
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

    public void setAlarmEndTime(long endTime) {

        Intent myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.putExtra("taskID", taskid);
        myIntent.putExtra("taskName", task_name.getText().toString());
        myIntent.putExtra("ringtone", tuneUri.toString());

        if (isEnd) {
            myIntent.putExtra("vibration", true);
            myIntent.putExtra("ringcheck", false);
        } else {
            myIntent.putExtra("vibration", false);
            myIntent.putExtra("ringcheck", true);
        }
        myIntent.setAction("endTask");

        if (repeatOn == 0) {
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

            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC, endTime, pendingIntent);


        } else if (repeatOn == 1) {
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, endTime, AlarmManager.INTERVAL_DAY, pendingIntent);

        } else if (repeatOn == 2) {
            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC, endTime, DateUtils.WEEK_IN_MILLIS, pendingIntent);

        } else if (repeatOn == 3) {
            Calendar monthlyCalendar = Calendar.getInstance();
            //monthlyCalendar.add(Calendar.MONTH, 1);


            pendingIntent = PendingIntent.getBroadcast(context, alarmId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),getDuration(),pendingIntent);


            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.JANUARY || monthlyCalendar.get(Calendar.MONTH) == Calendar.MARCH || monthlyCalendar.get(Calendar.MONTH) == Calendar.MAY || monthlyCalendar.get(Calendar.MONTH) == Calendar.JULY
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.AUGUST || monthlyCalendar.get(Calendar.MONTH) == Calendar.OCTOBER || monthlyCalendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endTime, AlarmManager.INTERVAL_DAY * 31, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.APRIL || monthlyCalendar.get(Calendar.MONTH) == Calendar.JUNE || monthlyCalendar.get(Calendar.MONTH) == Calendar.SEPTEMBER
                    || monthlyCalendar.get(Calendar.MONTH) == Calendar.NOVEMBER) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endTime, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
            }
            if (monthlyCalendar.get(Calendar.MONTH) == Calendar.FEBRUARY) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, endTime, AlarmManager.INTERVAL_DAY * 28, pendingIntent);
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

    private void daysSelector() {

        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mon.isChecked()) {
                    mon.setElevation(30f);
                    if (daysList.contains(mon.getText().toString())) {
                        daysList.remove(mon.getText().toString());
                    } else {
                        daysList.add(mon.getText().toString());
                    }
                } else {
                    daysList.remove(mon.getText().toString());
                    mon.setElevation(0f);
                }
            }
        });

        tue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tue.isChecked()) {
                    tue.setElevation(30f);
                    if (daysList.contains(tue.getText().toString())) {
                        daysList.remove(tue.getText().toString());
                    } else {
                        daysList.add(tue.getText().toString());
                    }
                } else {
                    daysList.remove(tue.getText().toString());
                    tue.setElevation(0f);
                }
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wed.isChecked()) {
                    wed.setElevation(30f);
                    if (daysList.contains(wed.getText().toString())) {
                        daysList.remove(wed.getText().toString());
                    } else {
                        daysList.add(wed.getText().toString());
                    }
                } else {
                    daysList.remove(wed.getText().toString());
                    wed.setElevation(0f);
                }
            }
        });

        thu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (thu.isChecked()) {
                    thu.setElevation(30f);
                    if (daysList.contains(thu.getText().toString())) {
                        daysList.remove(thu.getText().toString());
                    } else {
                        daysList.add(thu.getText().toString());
                    }
                } else {
                    daysList.remove(thu.getText().toString());
                    thu.setElevation(0f);
                }
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fri.isChecked()) {
                    fri.setElevation(30f);
                    if (daysList.contains(fri.getText().toString())) {
                        daysList.remove(fri.getText().toString());
                    } else {
                        daysList.add(fri.getText().toString());
                    }
                } else {
                    daysList.remove(fri.getText().toString());
                    fri.setElevation(0f);
                }
            }
        });

        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sat.isChecked()) {
                    sat.setElevation(30f);
                    if (daysList.contains(sat.getText().toString())) {
                        daysList.remove(sat.getText().toString());
                    } else {
                        daysList.add(sat.getText().toString());
                    }
                } else {
                    daysList.remove(sat.getText().toString());
                    sat.setElevation(0f);
                }
            }
        });

        sun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sun.isChecked()) {
                    sun.setElevation(30f);
                    if (daysList.contains(sun.getText().toString())) {
                        daysList.remove(sun.getText().toString());
                    } else {
                        daysList.add(sun.getText().toString());
                    }
                } else {
                    daysList.remove(sun.getText().toString());
                    sun.setElevation(0f);
                }
            }
        });

    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }

    private void validate(){

        if (TextUtils.isEmpty(task_name.getText().toString())){

            taskName_layout.setBackgroundResource(R.drawable.error_border);
        }
    }
}
