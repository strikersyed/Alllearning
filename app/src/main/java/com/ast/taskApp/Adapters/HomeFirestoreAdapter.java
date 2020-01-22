package com.ast.taskApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ast.taskApp.Imageslider;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.R;
import com.ast.taskApp.TodayOverview;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFirestoreAdapter extends FirestoreRecyclerAdapter<Tasks,HomeFirestoreAdapter.ViewHolder> {
    Context mcontext;
    private OnItemClick onItemClick,onStartClick,onCompleteClick;
    private String tasktoday = "";
    private String tasktomow = "";
    private String taskupcoming = "";
    int todayindex = 0;
    int tomowindex = 0;
    int upcmindex = 0;
    int todaycnt,tomowcnt,upcmcnt;
    private StorageReference mStorageRef;

    public HomeFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Tasks> options, Context mcontext, OnItemClick onItemClick, OnItemClick onStartClick, OnItemClick onCompleteClick) {
        super(options);
        this.mcontext = mcontext;
        this.onItemClick = onItemClick;
        this.onStartClick = onStartClick;
        this.onCompleteClick = onCompleteClick;
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    public interface OnItemClick {
        void onitemClick (int position);
        void onstartClick (int position);
        void oncompleteclick (int position);
    }
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param
     */

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tasks model) {
        if (model.getTaskStatus() != 4) {
            if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<=12) {

                       /* holder.todayheaderview.setText("Upcoming");
                        holder.todaytitle.setText(tasks.get(position).getName());
                        holder.todaydescript.setText("Work Starts at 10");
                        holder.todayimage.setImageURI(Uri.parse(tasks.get(position).getTaskImageUrl()));
                        holder.todayheader.setVisibility(View.VISIBLE);
                        holder.todaydesciptlayout.setVisibility(View.VISIBLE);*/

                if (taskupcoming.equals("Upcoming") && upcmcnt>=0 && holder.getLayoutPosition() != upcmindex) {
                    holder.todayheader.setVisibility(View.GONE);
                }
                else {
                    taskupcoming = "Upcoming";
                    upcmindex = holder.getLayoutPosition();
                }

                upcmcnt = upcmcnt + 1;
                holder.todayheaderview.setText("Upcoming");
                holder.todaytitle.setText(model.getName());
                if (model.getTaskStatus() == 0) {
                    holder.todaydescript.setText("Initialized");

                } else if (model.getTaskStatus() == 1) {
                    holder.todaydescript.setText("Started");
                } else if (model.getTaskStatus() == 2) {
                    holder.todaydescript.setText("Paused");
                } else if (model.getTaskStatus() == 3) {
                    holder.todaydescript.setText("Pending");
                } else if (model.getTaskStatus() == 4) {
                    holder.todaydescript.setText("Finished");
                }

                holder.todayoverbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>todayindex && holder.getLayoutPosition()<upcmindex) {
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","today");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<(getSnapshots().size()-1)){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","tomorrow");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<todayindex){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });

                if (model.getPlatform().equals("Android")) {
                    Glide.with(mcontext).load(model.getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                } else {
                    final StorageReference Ref = mStorageRef.child("Tasks").child(model.getTaskID()).child("Attachment").child("mountains.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Glide.with(mcontext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                    });
                }

                holder.todaydesciptlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>todayindex && holder.getLayoutPosition()<tomowindex) {
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Today");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<upcmindex){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Tomorrow");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<(getSnapshots().size())){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });
                //holder.upcomheader.setVisibility(View.VISIBLE);

            }
             else if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))>12
            && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<24) {


                    if (tasktoday.equals("Today") && todaycnt>=0 && holder.getLayoutPosition() != todayindex) {
                        holder.todayheader.setVisibility(View.GONE);

                    }
                    else {
                        todayindex = holder.getLayoutPosition();
                        tasktoday = "Today";
                        }

                todaycnt = todaycnt + 1;
                holder.todayheaderview.setText("Today");
                holder.todaytitle.setText(model.getName());
                if (model.getTaskStatus() == 0) {
                    holder.todaydescript.setText("Initialized");

                } else if (model.getTaskStatus() == 1) {
                    holder.todaydescript.setText("Started");
                } else if (model.getTaskStatus() == 2) {
                    holder.todaydescript.setText("Paused");
                } else if (model.getTaskStatus() == 3) {
                    holder.todaydescript.setText("Pending");
                } else if (model.getTaskStatus() == 4) {
                    holder.todaydescript.setText("Finished");
                }
                holder.todaydesciptlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>todayindex && holder.getLayoutPosition()<tomowindex) {
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Today");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<upcmindex){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Tomorrow");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<(getSnapshots().size())){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });

                holder.todayoverbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>=todayindex && holder.getLayoutPosition()<tomowindex) {
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","today");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<(getSnapshots().size()-1)){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","tomorrow");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<todayindex){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });



                if (model.getPlatform().equals("Android")) {
                    Glide.with(mcontext).load(model.getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                } else {
                    final StorageReference Ref = mStorageRef.child("Tasks").child(model.getTaskID()).child("Attachment").child("mountains.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Glide.with(mcontext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                    });
                }
                //holder.todayheader.setVisibility(View.VISIBLE);




            } else if (Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))>=24
            && Math.abs(TimeUnit.MILLISECONDS.toHours(Timestamp.now().toDate().getTime()-model.getStartTime().toDate().getTime()))<=48 ) {

                        /*holder.tomorrowtitle.setText(tasks.get(position).getName());
                        holder.tomorrowdescript.setText("Work Starts at 10");
                        //holder.todayimage.setImageURI(Uri.parse(tasks.get(position).getTaskImageUrl()));
                        holder.todayheader.setVisibility(View.GONE);
                        holder.todaydesciptlayout.setVisibility(View.GONE);*/

                    if (tasktomow.equals("Tomorrow") && tomowcnt >=0 && holder.getLayoutPosition() != tomowindex ) {
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    else {
                        tasktomow = "Tomorrow";
                        tomowindex = holder.getLayoutPosition();
                    }

                tomowcnt = tomowcnt + 1;
                holder.todayheaderview.setText("Tomorrow");
                holder.todaytitle.setText(model.getName());
                if (model.getTaskStatus() == 0) {
                    holder.todaydescript.setText("Initialized");

                } else if (model.getTaskStatus() == 1) {
                    holder.todaydescript.setText("Started");
                } else if (model.getTaskStatus() == 2) {
                    holder.todaydescript.setText("Paused");
                } else if (model.getTaskStatus() == 3) {
                    holder.todaydescript.setText("Pending");
                } else if (model.getTaskStatus() == 4) {
                    holder.todaydescript.setText("Finished");

                }



                if (model.getPlatform().equals("Android")) {
                    Glide.with(mcontext).load(model.getTaskImageUrl()).sizeMultiplier(0.5f).into(holder.todayimage);
                } else {
                    final StorageReference Ref = mStorageRef.child("Tasks").child(model.getTaskID()).child("Attachment").child("mountains.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Glide.with(mcontext).load(url).sizeMultiplier(0.5f).into(holder.todayimage);
                        }
                    });
                }

                holder.todayoverbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>=todayindex && holder.getLayoutPosition()<tomowindex) {
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","today");
                            mcontext.startActivity(intent);
                        }
                        else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<=(getSnapshots().size()-1)){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","tomorrow");
                            mcontext.startActivity(intent);
                        }
                        else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<todayindex){
                            Intent intent = new Intent(mcontext, TodayOverview.class);
                            intent.putExtra("check","upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });

                holder.todaydesciptlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.getLayoutPosition()>todayindex && holder.getLayoutPosition()<tomowindex) {
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Today");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<upcmindex){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Tomorrow");
                            mcontext.startActivity(intent);
                        }else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<(getSnapshots().size())){
                            Intent intent = new Intent(mcontext, Imageslider.class);
                            intent.putExtra("check","Upcoming");
                            mcontext.startActivity(intent);
                        }
                    }
                });
                //holder.tomowheader.setVisibility(View.VISIBLE);

            } else {
                 holder.parentlayout.removeAllViews();
            }


       /* if (position<todaytasks.size()) {
            if (todaytasks.size() > 0) {
                holder.todaytitle.setText(todaytasks.get(position).getName());
                holder.todaydescript.setText("Work starting from 10");
                holder.todayimage.setImageURI(Uri.parse(todaytasks.get(position).getTaskImageUrl()));
                holder.todaydesciptlayout.setVisibility(View.VISIBLE);
                holder.todayheader.setVisibility(View.VISIBLE);
                *//*if (todaytitle==0) {
                    holder.todaydesciptlayout.setVisibility(View.VISIBLE);
                    holder.todayheader.setVisibility(View.VISIBLE);
                    todaytitle = 1;
                } else {
                    holder.todaydesciptlayout.setVisibility(View.VISIBLE);
                    holder.todayheader.setVisibility(View.GONE);
                }*//*
            } else {

            }
        }
        if (position<tomorrowtasks.size()) {
            if (tomorrowtasks.size() > 0) {
                holder.tomorrowtitle.setText(tomorrowtasks.get(position).getName());
                holder.tomorrowdescript.setText("Work starting from 10");
                holder.tomorrowimage.setImageURI(Uri.parse(tomorrowtasks.get(position).getTaskImageUrl()));
                holder.tomowdesciptlayout.setVisibility(View.VISIBLE);
                holder.tomowheader.setVisibility(View.VISIBLE);
               *//* if (tomorrowtitle==0) {
                    holder.tomowdesciptlayout.setVisibility(View.VISIBLE);
                    holder.tomowheader.setVisibility(View.VISIBLE);
                    tomorrowtitle = 1;
                } else {
                    holder.tomowdesciptlayout.setVisibility(View.VISIBLE);
                    holder.tomowheader.setVisibility(View.GONE);
                }*//*
            } else {

            }
        }
        if (position<upcomingtasks.size()) {
            if (upcomingtasks.size() > 0) {
                holder.upcomingtitle.setText(upcomingtasks.get(position).getName());
                holder.upcomingdescript.setText("Work starting from 10");
                holder.upcomingimage.setImageURI(Uri.parse(upcomingtasks.get(position).getTaskImageUrl()));
                holder.upcomingdesciptlayout.setVisibility(View.VISIBLE);
                holder.upcomheader.setVisibility(View.VISIBLE);
                *//*if (upcomingtitle==0) {
                    holder.upcomingdesciptlayout.setVisibility(View.VISIBLE);
                    holder.upcomheader.setVisibility(View.VISIBLE);
                    upcomingtitle = 1;
                } else {
                    holder.upcomingdesciptlayout.setVisibility(View.VISIBLE);
                    holder.upcomheader.setVisibility(View.GONE);
                }*//*

            } else {

            }
        }*/
        } else {
            if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<=todayindex){
                upcmcnt = upcmcnt - 1;
                if (holder.getLayoutPosition()==upcmindex){
                    if (holder.getLayoutPosition()==upcmindex && upcmcnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Upcoming");
                } else {
                    holder.parentlayout.removeAllViews();
                }
            } else if (holder.getLayoutPosition()>=todayindex && holder.getLayoutPosition()<=tomowindex){
                todaycnt = todaycnt - 1;
                if (holder.getLayoutPosition()==todayindex){
                    if (holder.getLayoutPosition()==todayindex && todaycnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Today");
                } else {
                   holder.parentlayout.removeAllViews();
                }
            } else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<=(getSnapshots().size())){
                tomowcnt = tomowcnt - 1;
                if (holder.getLayoutPosition()==tomowindex){
                    if (holder.getLayoutPosition()==tomowindex && tomowcnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Tomorrow");
                } else {
                    holder.parentlayout.removeAllViews();
                }
            } else {
                holder.parentlayout.removeAllViews();
            }



            /*if (holder.getLayoutPosition()>todayindex && holder.getLayoutPosition()<tomowindex){
                todaycnt = todaycnt - 1;
                if (holder.getLayoutPosition()==todayindex){
                    if (holder.getLayoutPosition()==todayindex && todaycnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Upcoming");
                } else {
                    holder.parentlayout.removeAllViews();
                }
            } else if (holder.getLayoutPosition()>=tomowindex && holder.getLayoutPosition()<upcmindex){
                tomowcnt = tomowcnt - 1;
                if (holder.getLayoutPosition()==tomowindex){
                    if (holder.getLayoutPosition()==tomowindex && tomowcnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Tomorrow");
                } else {
                    holder.parentlayout.removeAllViews();
                }
            } else if (holder.getLayoutPosition()>=upcmindex && holder.getLayoutPosition()<(getSnapshots().size())){
                upcmcnt = upcmcnt - 1;
                if (holder.getLayoutPosition()==upcmindex){
                    if (holder.getLayoutPosition()==upcmindex && upcmcnt == 0){
                        holder.todaydesciptlayout.setVisibility(View.GONE);
                        holder.todayheader.setVisibility(View.GONE);
                    }
                    holder.todaydesciptlayout.setVisibility(View.GONE);
                    holder.todayheaderview.setText("Upcoming");
                } else {
                    holder.parentlayout.removeAllViews();
                }
            }*/

            /*if (holder.todaydesciptlayout.getVisibility()==View.VISIBLE){
                holder.todaydesciptlayout.setVisibility(View.GONE);
            } else if (holder.tomowdesciptlayout.getVisibility()==View.VISIBLE){
                holder.tomowdesciptlayout.setVisibility(View.GONE);
            }else if (holder.upcomingdesciptlayout.getVisibility()==View.VISIBLE){
                holder.upcomingdesciptlayout.setVisibility(View.GONE);
            }*/
        }
    }

    @NonNull
    @Override
    public HomeFirestoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.itemlist_view,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView todayimage;
        TextView todaytitle,todaydescript,todayheaderview;
        RelativeLayout todayheader,parentlayout;
        LinearLayout todaydesciptlayout;
        ImageView todayoverbtn,start_tdytask,complt_tdytsk;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            parentlayout = itemView.findViewById(R.id.parenthomelayout);
            todayheaderview = itemView.findViewById(R.id.textviewtoday);
            todaytitle = itemView.findViewById(R.id.tsktodaytitle);
            todaydescript = itemView.findViewById(R.id.tsktodaydescript);
            todaydesciptlayout = itemView.findViewById(R.id.todaydescript);
            todayheader = itemView.findViewById(R.id.headertoday);
            todayimage = itemView.findViewById(R.id.tsktodayimage);
            todayoverbtn = itemView.findViewById(R.id.todayoverviewbtn);
            start_tdytask = itemView.findViewById(R.id.start_todaytask);
            complt_tdytsk = itemView.findViewById(R.id.complete_todaytask);

            start_tdytask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onstartClick(getLayoutPosition());
                }
            });

            complt_tdytsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.oncompleteclick(getLayoutPosition());
                }
            });






            todayoverbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mcontext, TodayOverview.class);
                    mcontext.startActivity(intent);
                }
            });

        }
    }


}
