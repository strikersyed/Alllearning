package com.ast.taskApp.Adapters;

import android.content.Context;
import android.graphics.Outline;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.bumptech.glide.Glide;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.R;
import com.ast.taskApp.TaskApp;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.novoda.merlin.MerlinsBeard;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TodayOverViewAdapter extends RecyclerView.Adapter<TodayOverViewAdapter.ViewHolder> {
    private ArrayList<Tasks> tasks = new ArrayList<>();
    private Context mContext;
    float aFloat = 20f;
    private Calendar start,end;
    String check;
    FirebaseFirestore db;
    MerlinsBeard merlinsBeard;
    private StorageReference mStorageRef;



    public TodayOverViewAdapter(Context mContext, ArrayList<Tasks> tasks,String check) {
        this.mContext = mContext;
        this.tasks = tasks;
        merlinsBeard = new MerlinsBeard.Builder().build(mContext);
        this.check = check;
        db = FirebaseFirestore.getInstance();
    }





    @NonNull
    @Override
    public TodayOverViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_itemlist,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder ;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull final TodayOverViewAdapter.ViewHolder holder, int position) {





        holder.title.setText(tasks.get(position).getName());
        String hmss = String.format("%02d : %02d",
                tasks.get(position).getStartTime().toDate().getHours(),
                tasks.get(position).getStartTime().toDate().getMinutes());
        holder.descript.setText(tasks.get(position).getStartTime().toDate().getDate() + "/" +
                (tasks.get(position).getStartTime().toDate().getMonth()+1) + "/20"+tasks.get(position).getStartTime().toDate().getYear()+" "+hmss);
        start = Calendar.getInstance();
        end = Calendar.getInstance();
        start.setTimeInMillis(tasks.get(position).getStartTime().toDate().getTime());
        end.setTimeInMillis(tasks.get(position).getEndTime().toDate().getTime());

        long start_milli = start.getTimeInMillis();
        long end_milli = end.getTimeInMillis();
        //double total_milli = (end_milli - start_milli);
        long total_millii = (end_milli - start_milli);
        long total_milli = (end_milli - start_milli);
        int factor = (int) (100 / total_milli);
        Calendar forCounter = Calendar.getInstance();
        forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
        //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));
        if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID())!=null) {
            if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID()).getTaskStatus() != tasks.get(position).getTaskStatus()) {
                Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                taskss.setTaskStatus(tasks.get(position).getTaskStatus());
                TaskApp.getTaskRepo().updateTask(taskss);

            }
        }else {
            TaskApp.getTaskRepo().insertTasks(tasks.get(position));
        }

        if (tasks.get(position).getStartTime().toDate().before(Timestamp.now().toDate()) || tasks.get(position).getStartTime().toDate()==Timestamp.now().toDate()) {
            if (holder.countDownTimer != null) {
                holder.countDownTimer.cancel();
            }
            holder.countDownTimer = new CountDownTimer(forCounter.getTimeInMillis(), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
//                    0.001
                    //int count = (int) ((int) (forCounter.getTimeInMillis() - millisUntilFinished) * 0.001);
                    //double progress = (100.0 * count) / forCounter.getTimeInMillis();

                    //holder.progressBar.setProgress((int) millisUntilFinished / 1000);
                    holder.progressBar.setProgress((int) ((Double.parseDouble("" + millisUntilFinished) / total_milli) * 100));
                    String hms = String.format("%02d : %02d",
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))));
                    holder.timestatus.setText(hms);
                    //holder.timestatus.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + ":"
                    //        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                @Override
                public void onFinish() {
                    if (holder.countDownTimer!=null) {
                        holder.countDownTimer.cancel();
                    }
                    Data.Builder data = new Data.Builder();
                    data.putString("TaskID",tasks.get(position).getTaskID());
                    data.putInt("status",3);
                    TaskApp.getWorkManager().enqueue(TaskApp.getTaskUpdateRequest(data.build()));
                    Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                    if (taskss!=null){
                        taskss.setTaskStatus(3);
                        TaskApp.getTaskRepo().updateTask(taskss);
                    }else {
                        TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                    }

                }
            }.start();
        } else {
            holder.progressBar.setProgress(50);
            String hms = String.format("%02d : %02d",
                    tasks.get(position).getStartTime().toDate().getHours(),
                    tasks.get(position).getStartTime().toDate().getMinutes());
            holder.timestatus.setText(hms);
        }


        //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
        if (tasks.get(position).getPlatform().equals("Android")) {
            if (!(tasks.get(position).getTaskImageUrl() == null)) {
                Glide.with(mContext).load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
            } else {
                Glide.with(mContext).load(R.drawable.demo8).into(holder.todayimage);
            }
        } else {
            final StorageReference Ref = mStorageRef.child("Tasks").child(tasks.get(position).getTaskID()).child("Attachment").child("mountains.jpg");
            Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url = uri.toString();
                    Glide.with(mContext).load(url).into(holder.todayimage);
                }
            });
        }
        //Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(R.drawable.demo8).into(holder.todayimage);

        holder.todayimage.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.todayimage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int i = (int) (view.getHeight() + aFloat);
                outline.setRoundRect(0, 0, view.getWidth(), i, 20f);
            }
        });
        holder.todayimage.setClipToOutline(true);

        /*if (tasks.get(position).getTaskStatus()!=4 && tasks.get(position).getTaskStatus()!=3) {
            if (check.equals("upcoming")){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-tasks.get(position).getStartTime().toDate().getTime()))<=12) {


                } else {
                    holder.parenttoday.setVisibility(View.GONE);
                    holder.parenttoday.removeAllViews();
                }

            }
            else if (check.equals("tomorrow")){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-tasks.get(position).getStartTime().toDate().getTime()))>=24
                        && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-tasks.get(position).getStartTime().toDate().getTime()))<=48) {

                    holder.title.setText(tasks.get(position).getName());
                    String hmss = String.format("%02d : %02d",
                            tasks.get(position).getStartTime().toDate().getHours(),
                            tasks.get(position).getStartTime().toDate().getMinutes());
                    holder.descript.setText(tasks.get(position).getStartTime().toDate().getDate() + "/" +
                            (tasks.get(position).getStartTime().toDate().getMonth()+1) + "/20"+tasks.get(position).getStartTime().toDate().getYear()+" "+hmss);

                    start = Calendar.getInstance();
                    end = Calendar.getInstance();
                    start.setTimeInMillis(tasks.get(position).getStartTime().toDate().getTime());
                    end.setTimeInMillis(tasks.get(position).getEndTime().toDate().getTime());

                    long start_milli = start.getTimeInMillis();
                    long end_milli = end.getTimeInMillis();
                    //double total_milli = (end_milli - start_milli);
                    long total_millii = (end_milli - start_milli);
                    long total_milli = (end_milli - start_milli);
                    int factor = (int) (100 / total_milli);
                    Calendar forCounter = Calendar.getInstance();
                    forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                    //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));
                    if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID())!=null) {
                        if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID()).getTaskStatus() != tasks.get(position).getTaskStatus()) {
                            Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                            taskss.setTaskStatus(tasks.get(position).getTaskStatus());
                            TaskApp.getTaskRepo().updateTask(taskss);

                        }
                    }else {
                        TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                    }

                    if (tasks.get(position).getStartTime().toDate().before(Timestamp.now().toDate()) || tasks.get(position).getStartTime().toDate()==Timestamp.now().toDate()) {
                        if (holder.countDownTimer != null) {
                            holder.countDownTimer.cancel();
                        }
                        holder.countDownTimer = new CountDownTimer(forCounter.getTimeInMillis(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
//                    0.001
                                //int count = (int) ((int) (forCounter.getTimeInMillis() - millisUntilFinished) * 0.001);
                                //double progress = (100.0 * count) / forCounter.getTimeInMillis();

                                //holder.progressBar.setProgress((int) millisUntilFinished / 1000);
                                holder.progressBar.setProgress((int) ((Double.parseDouble("" + millisUntilFinished) / total_milli) * 100));
                                String hms = String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))));
                                holder.timestatus.setText(hms);
                                //holder.timestatus.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + ":"
                                //        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                            }

                            @Override
                            public void onFinish() {
                                if (holder.countDownTimer!=null) {
                                    holder.countDownTimer.cancel();
                                }

                                    db.collection("Tasks").document(tasks.get(position).getTaskID()).update("taskStatus", 3);
                                    Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                                    if (taskss!=null){
                                        taskss.setTaskStatus(1);
                                        TaskApp.getTaskRepo().updateTask(taskss);
                                    }else {
                                        TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                                    }

                            }
                        }.start();
                    } else {
                        holder.progressBar.setProgress(50);
                        String hms = String.format("%02d : %02d",
                                tasks.get(position).getStartTime().toDate().getHours(),
                                tasks.get(position).getStartTime().toDate().getMinutes());
                        holder.timestatus.setText(hms);
                    }


                    //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
                    if (tasks.get(position).getPlatform().equals("Android")) {
                        if (!(tasks.get(position).getTaskImageUrl()==null)) {
                            Glide.with(mContext).load(tasks.get(position).getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.demo3).into(holder.todayimage);
                        }
                    } else {
                        final StorageReference Ref = mStorageRef.child("Tasks").child(tasks.get(position).getTaskID()).child("Attachment").child("mountains.jpg");
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Glide.with(mContext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                            }
                        });
                    }
                    //Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(R.drawable.demo8).into(holder.todayimage);

                    holder.todayimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.todayimage.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            int i = (int) (view.getHeight() + aFloat);
                            outline.setRoundRect(0, 0, view.getWidth(), i, 20f);
                        }
                    });
                    holder.todayimage.setClipToOutline(true);
                } else {
                    holder.parenttoday.removeAllViews();
                }

            }
            else if (check.equals("today")){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-tasks.get(position).getStartTime().toDate().getTime()))>12
                        && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-tasks.get(position).getStartTime().toDate().getTime()))<24) {

                    holder.title.setText(tasks.get(position).getName());
                    String hmss = String.format("%02d : %02d",
                            tasks.get(position).getStartTime().toDate().getHours(),
                            tasks.get(position).getStartTime().toDate().getMinutes());
                    holder.descript.setText(tasks.get(position).getStartTime().toDate().getDate() + "/" +
                            (tasks.get(position).getStartTime().toDate().getMonth()+1) + "/20"+tasks.get(position).getStartTime().toDate().getYear()+" "+hmss);
                    start = Calendar.getInstance();
                    end = Calendar.getInstance();
                    start.setTimeInMillis(tasks.get(position).getStartTime().toDate().getTime());
                    end.setTimeInMillis(tasks.get(position).getEndTime().toDate().getTime());

                    long start_milli = start.getTimeInMillis();
                    long end_milli = end.getTimeInMillis();
                    //double total_milli = (end_milli - start_milli);
                    long total_millii = (end_milli - start_milli);
                    long total_milli = (end_milli - start_milli);
                    int factor = (int) (100 / total_milli);
                    Calendar forCounter = Calendar.getInstance();
                    forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                    //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));
                    if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID())!=null) {
                        if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID()).getTaskStatus() != tasks.get(position).getTaskStatus()) {
                            Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                            taskss.setTaskStatus(tasks.get(position).getTaskStatus());
                            TaskApp.getTaskRepo().updateTask(taskss);

                        }
                    }else {
                        TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                    }

                    if (tasks.get(position).getStartTime().toDate().before(Timestamp.now().toDate()) || tasks.get(position).getStartTime().toDate()==Timestamp.now().toDate()) {
                        if (holder.countDownTimer != null) {
                            holder.countDownTimer.cancel();
                        }
                        holder.countDownTimer = new CountDownTimer(forCounter.getTimeInMillis(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
//                    0.001
                                //int count = (int) ((int) (forCounter.getTimeInMillis() - millisUntilFinished) * 0.001);
                                //double progress = (100.0 * count) / forCounter.getTimeInMillis();

                                //holder.progressBar.setProgress((int) millisUntilFinished / 1000);
                                holder.progressBar.setProgress((int) ((Double.parseDouble("" + millisUntilFinished) / total_milli) * 100));
                                String hms = String.format("%02d : %02d",
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))));
                                holder.timestatus.setText(hms);
                                //holder.timestatus.setText(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + ":"
                                //        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                            }

                            @Override
                            public void onFinish() {
                                if (holder.countDownTimer!=null) {
                                    holder.countDownTimer.cancel();
                                }

                                    db.collection("Tasks").document(tasks.get(position).getTaskID()).update("taskStatus", 3);
                                    Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                                    if (taskss!=null){
                                        taskss.setTaskStatus(1);
                                        TaskApp.getTaskRepo().updateTask(taskss);
                                    }else {
                                        TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                                    }

                            }
                        }.start();
                    } else {
                        holder.progressBar.setProgress(50);
                        String hms = String.format("%02d : %02d",
                                tasks.get(position).getStartTime().toDate().getHours(),
                                tasks.get(position).getStartTime().toDate().getMinutes());
                        holder.timestatus.setText(hms);
                    }


                    //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
                    if (tasks.get(position).getPlatform().equals("Android")) {
                        if (!(tasks.get(position).getTaskImageUrl()==null)) {
                            Glide.with(mContext).load(tasks.get(position).getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                        else {
                            Glide.with(mContext).load(R.drawable.demo3).into(holder.todayimage);
                        }
                    } else {
                        final StorageReference Ref = mStorageRef.child("Tasks").child(tasks.get(position).getTaskID()).child("Attachment").child("mountains.jpg");
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Glide.with(mContext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                            }
                        });
                    }
                    //Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(R.drawable.demo8).into(holder.todayimage);

                    holder.todayimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.todayimage.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            int i = (int) (view.getHeight() + aFloat);
                            outline.setRoundRect(0, 0, view.getWidth(), i, 20f);
                        }
                    });
                    holder.todayimage.setClipToOutline(true);
                } else {
                    holder.parenttoday.removeAllViews();
                }
            } else {
                holder.parenttoday.removeAllViews();
            }
        }
        else if (tasks.get(position).getTaskStatus()==3) {
            if (check.equals("overduetasks")){
                holder.title.setText(tasks.get(position).getName());
                String hmss = String.format("%02d : %02d",
                        tasks.get(position).getStartTime().toDate().getHours(),
                        tasks.get(position).getStartTime().toDate().getMinutes());
                holder.descript.setText(tasks.get(position).getStartTime().toDate().getDate() + "/" +
                        (tasks.get(position).getStartTime().toDate().getMonth()+1) + "/20"+tasks.get(position).getStartTime().toDate().getYear()+" "+hmss);
                start = Calendar.getInstance();
                end = Calendar.getInstance();
                start.setTimeInMillis(tasks.get(position).getStartTime().toDate().getTime());
                end.setTimeInMillis(tasks.get(position).getEndTime().toDate().getTime());

                long start_milli = start.getTimeInMillis();
                long end_milli = end.getTimeInMillis();
                //double total_milli = (end_milli - start_milli);
                long total_millii = (end_milli - start_milli);
                long total_milli = (end_milli - start_milli);
                int factor = (int) (100 / total_milli);
                Calendar forCounter = Calendar.getInstance();
                forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());


                holder.progressBar.setProgress(50);
                String hms = String.format("%02d : %02d",
                        tasks.get(position).getStartTime().toDate().getHours(),
                        tasks.get(position).getStartTime().toDate().getMinutes());
                holder.timestatus.setText(hms);

                if (tasks.get(position).getPlatform().equals("Android")) {
                    if (!(tasks.get(position).getTaskImageUrl()==null)) {
                        Glide.with(mContext).load(tasks.get(position).getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                    }
                    else {
                        Glide.with(mContext).load(R.drawable.demo3).into(holder.todayimage);
                    }
                } else {
                    final StorageReference Ref = mStorageRef.child("Tasks").child(tasks.get(position).getTaskID()).child("Attachment").child("mountains.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Glide.with(mContext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                    });
                }

                if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID())!=null) {
                    if (TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID()).getTaskStatus() != tasks.get(position).getTaskStatus()) {
                        Tasks taskss = TaskApp.getTaskRepo().getTaskbyId(tasks.get(position).getTaskID());
                        taskss.setTaskStatus(tasks.get(position).getTaskStatus());
                        TaskApp.getTaskRepo().updateTask(taskss);

                    }
                }else {
                    TaskApp.getTaskRepo().insertTasks(tasks.get(position));
                }

                holder.todayimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.todayimage.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        int i = (int) (view.getHeight() + aFloat);
                        outline.setRoundRect(0, 0, view.getWidth(), i, 20f);
                    }
                });
                holder.todayimage.setClipToOutline(true);

            }
            else {
                holder.parenttoday.removeAllViews();
                holder.parenttoday.removeAllViewsInLayout();
            }

        } else {
            //holder.parenttoday.removeAllViews();
        }*/


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parenttoday;
        TextView title,descript,timestatus;
        ProgressBar progressBar;
        ImageView todayimage;
        CountDownTimer countDownTimer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parenttoday = itemView.findViewById(R.id.parenttodayover);
            timestatus = itemView.findViewById(R.id.timestatus);
            title = itemView.findViewById(R.id.todayovertitle);
            descript = itemView.findViewById(R.id.todayoverdescript);
            progressBar = itemView.findViewById(R.id.timerprogress);
            todayimage = itemView.findViewById(R.id.todaytaskimage);
        }
    }
}
