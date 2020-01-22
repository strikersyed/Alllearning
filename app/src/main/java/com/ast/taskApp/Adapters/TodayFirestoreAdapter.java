package com.ast.taskApp.Adapters;

import android.content.Context;
import android.graphics.Outline;
import android.icu.util.Calendar;
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

import com.bumptech.glide.Glide;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TodayFirestoreAdapter extends FirestoreRecyclerAdapter<Tasks,TodayFirestoreAdapter.ViewHolder> {

    private Context mContext;
    float aFloat = 20f;
    private Calendar start,end;
    String check;
    FirebaseFirestore db;

    public TodayFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Tasks> options, Context mContext, String check) {
        super(options);
        this.mContext = mContext;
        this.check = check;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public TodayFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Tasks> options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tasks model) {

        if (model.getTaskStatus()!=4 && model.getTaskStatus()!=3) {
            if (check.equals("upcoming")){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<=12) {

                    holder.title.setText(model.getName());
                    String hmss = String.format("%02d : %02d",
                            model.getStartTime().toDate().getHours(),
                            model.getStartTime().toDate().getMinutes());
                    holder.descript.setText(model.getStartTime().toDate().getDate() + "/" +
                            (model.getStartTime().toDate().getMonth()+1) + "/20"+model.getStartTime().toDate().getYear()+" "+hmss);
                    start = Calendar.getInstance();
                    end = Calendar.getInstance();
                    start.setTimeInMillis(model.getStartTime().toDate().getTime());
                    end.setTimeInMillis(model.getEndTime().toDate().getTime());

                    long start_milli = start.getTimeInMillis();
                    long end_milli = end.getTimeInMillis();
                    //double total_milli = (end_milli - start_milli);
                    long total_millii = (end_milli - start_milli);
                    long total_milli = (end_milli - start_milli);
                    int factor = (int) (100 / total_milli);
                    Calendar forCounter = Calendar.getInstance();
                    forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                    //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));


                    if (model.getStartTime().toDate().before(Timestamp.now().toDate()) || model.getStartTime().toDate()==Timestamp.now().toDate()) {
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
                                db.collection("Tasks").document(model.getTaskID()).update("taskStatus",3);
                            }
                        }.start();
                    } else {
                        holder.progressBar.setProgress(50);
                        String hms = String.format("%02d : %02d",
                                model.getStartTime().toDate().getHours(),
                                model.getStartTime().toDate().getMinutes());
                        holder.timestatus.setText(hms);
                    }


                    //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
                    Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(model.getTaskImageUrl()).into(holder.todayimage);
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
                    holder.parenttoday.setVisibility(View.GONE);
                    holder.parenttoday.removeAllViews();
                }

            }
            else if (check.equals("tomorrow")){
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))>=24
                        && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<=48) {

                    holder.title.setText(model.getName());
                    String hmss = String.format("%02d : %02d",
                            model.getStartTime().toDate().getHours(),
                            model.getStartTime().toDate().getMinutes());
                    holder.descript.setText(model.getStartTime().toDate().getDate() + "/" +
                            (model.getStartTime().toDate().getMonth()+1) + "/20"+model.getStartTime().toDate().getYear()+" "+hmss);

                    start = Calendar.getInstance();
                    end = Calendar.getInstance();
                    start.setTimeInMillis(model.getStartTime().toDate().getTime());
                    end.setTimeInMillis(model.getEndTime().toDate().getTime());

                    long start_milli = start.getTimeInMillis();
                    long end_milli = end.getTimeInMillis();
                    //double total_milli = (end_milli - start_milli);
                    long total_millii = (end_milli - start_milli);
                    long total_milli = (end_milli - start_milli);
                    int factor = (int) (100 / total_milli);
                    Calendar forCounter = Calendar.getInstance();
                    forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                    //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));


                    if (model.getStartTime().toDate().before(Timestamp.now().toDate()) || model.getStartTime().toDate()==Timestamp.now().toDate()) {
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
                                db.collection("Tasks").document(model.getTaskID()).update("taskStatus",3);
                            }
                        }.start();
                    } else {
                        holder.progressBar.setProgress(50);
                        String hms = String.format("%02d : %02d",
                                model.getStartTime().toDate().getHours(),
                                model.getStartTime().toDate().getMinutes());
                        holder.timestatus.setText(hms);
                    }


                    //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
                    Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(model.getTaskImageUrl()).into(holder.todayimage);
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
                if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))>12
                && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<24) {

                    holder.title.setText(model.getName());
                    String hmss = String.format("%02d : %02d",
                            model.getStartTime().toDate().getHours(),
                            model.getStartTime().toDate().getMinutes());
                    holder.descript.setText(model.getStartTime().toDate().getDate() + "/" +
                            (model.getStartTime().toDate().getMonth()+1) + "/20"+model.getStartTime().toDate().getYear()+" "+hmss);
                    start = Calendar.getInstance();
                    end = Calendar.getInstance();
                    start.setTimeInMillis(model.getStartTime().toDate().getTime());
                    end.setTimeInMillis(model.getEndTime().toDate().getTime());

                    long start_milli = start.getTimeInMillis();
                    long end_milli = end.getTimeInMillis();
                    //double total_milli = (end_milli - start_milli);
                    long total_millii = (end_milli - start_milli);
                    long total_milli = (end_milli - start_milli);
                    int factor = (int) (100 / total_milli);
                    Calendar forCounter = Calendar.getInstance();
                    forCounter.setTimeInMillis(end.getTimeInMillis() - new Date().getTime());
                    //holder.progressBar.setMax((int) (TimeUnit.MINUTES.toMillis(total_milli)/1000));


                    if (model.getStartTime().toDate().before(Timestamp.now().toDate()) || model.getStartTime().toDate()==Timestamp.now().toDate()) {
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
                                db.collection("Tasks").document(model.getTaskID()).update("taskStatus",3);
                            }
                        }.start();
                    } else {
                        holder.progressBar.setProgress(50);
                        String hms = String.format("%02d : %02d",
                                model.getStartTime().toDate().getHours(),
                                model.getStartTime().toDate().getMinutes());
                        holder.timestatus.setText(hms);
                    }


                    //Glide.with(mContext).asBitmap().load(tasks.get(position).getTaskImageUrl()).into(holder.todayimage);
                    Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(model.getTaskImageUrl()).into(holder.todayimage);
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
        else if (model.getTaskStatus()==3) {
            if (check.equals("overduetasks")){
                holder.title.setText(model.getName());
                String hmss = String.format("%02d : %02d",
                        model.getStartTime().toDate().getHours(),
                        model.getStartTime().toDate().getMinutes());
                holder.descript.setText(model.getStartTime().toDate().getDate() + "/" +
                        (model.getStartTime().toDate().getMonth()+1) + "/20"+model.getStartTime().toDate().getYear()+" "+hmss);
                start = Calendar.getInstance();
                end = Calendar.getInstance();
                start.setTimeInMillis(model.getStartTime().toDate().getTime());
                end.setTimeInMillis(model.getEndTime().toDate().getTime());

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
                        model.getStartTime().toDate().getHours(),
                        model.getStartTime().toDate().getMinutes());
                holder.timestatus.setText(hms);

                Glide.with(mContext).asBitmap().sizeMultiplier(0.9f).load(model.getTaskImageUrl()).into(holder.todayimage);

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
            holder.parenttoday.removeAllViews();
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.today_itemlist,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
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
