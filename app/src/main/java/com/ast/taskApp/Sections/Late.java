package com.ast.taskApp.Sections;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.ast.taskApp.Imageslider;
import com.ast.taskApp.Models.Tasks;
import com.ast.taskApp.R;
import com.ast.taskApp.TodayOverview;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class Late extends Section {
    /**
     * Create a Section object based on {@link SectionParameters}.
     *
     * @param sectionParameters section parameters
     */
    ArrayList<Tasks> tasks = new ArrayList<>();
    Context context;
    private StorageReference mStorageRef;
    private OnItemClick onItemClick;
    ArrayList<String> selected = new ArrayList<>();
    ArrayList<Boolean> checklist;

    public Late(ArrayList<Tasks> tasks, Context context,OnItemClick onitemclick,OnItemClick onstartclick,OnItemClick oncompleteclick,ArrayList<Boolean> checklist) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.itemlist_view)
                .headerResourceId(R.layout.item_headerlayout)
                .build());
        this.tasks = tasks;
        this.context = context;
        this.onItemClick = onitemclick;
        this.checklist = checklist;

    }

    public interface OnItemClick {
        void onitemClick (int position);
        void onstartClick (int position,String listname,String taskID);
        void oncompleteclick (int position,String listname, String taskID);
        void onlongclick (int position,String listname, Tasks task);
    }

    @Override
    public int getContentItemsTotal() {
        return tasks.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MyItemViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MyHeaderViewHolder(view);
    }


    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MyHeaderViewHolder viewHolder = (MyHeaderViewHolder) holder;

        if (tasks.size()>0) {
            viewHolder.todayheaderview.setText("Late");

            viewHolder.todayoverbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TodayOverview.class);
                    intent.putExtra("check","overduetasks");
                    context.startActivity(intent);
                }
            });
        }
        else {
            viewHolder.todayheader.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder viewHolder = (MyItemViewHolder) holder;


            viewHolder.todaytitle.setText(tasks.get(position).getName());
        if (tasks.get(position).getTaskStatus() == 0) {
            viewHolder.todaydescript.setText("Initialized");
        } else if (tasks.get(position).getTaskStatus() == 1) {
            viewHolder.todaydescript.setText("Started");
        } else if (tasks.get(position).getTaskStatus() == 2) {
            viewHolder.todaydescript.setText("Paused");
        } else if (tasks.get(position).getTaskStatus() == 3) {
            viewHolder.todaydescript.setText("Pending");
        } else if (tasks.get(position).getTaskStatus() == 4) {
            viewHolder.todaydescript.setText("Finished");
        }
            if (tasks.get(position).getPlatform().equals("Android")) {
                if (!(tasks.get(position).getTaskImageUrl() == null)) {
                    Glide.with(context).load(tasks.get(position).getTaskImageUrl()).into(viewHolder.todayimage);
                } else {
                    Glide.with(context).load(R.drawable.demo3).into(viewHolder.todayimage);
                }
            } else {
                final StorageReference Ref = mStorageRef.child("Tasks").child(tasks.get(position).getTaskID()).child("Attachment").child("mountains.jpg");
                Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Glide.with(context).load(url).into(viewHolder.todayimage);
                    }
                });
            }

        viewHolder.todaydesciptlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Imageslider.class);
                intent.putExtra("check","Late");
                context.startActivity(intent);
            }
        });


        if (checklist.get(3)) {

            if (selected.contains(String.valueOf(viewHolder.getLayoutPosition() - 4))) {
                viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.selected_border);
            } else {
                viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.border);
            }
        }
        else {
            selected.clear();
            checklist.set(3,true);
        }


        viewHolder.todaydesciptlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (checklist.get(3)) {
                    if (selected.contains(String.valueOf(viewHolder.getLayoutPosition()-4))){
                        selected.remove(String.valueOf(viewHolder.getLayoutPosition()-4));
                        //viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.border);


                    }
                    else {
                        selected.add(String.valueOf(viewHolder.getLayoutPosition()-4));
                        //viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.selected_border);
                    }

                    onItemClick.onlongclick(viewHolder.getAdapterPosition(),"late",tasks.get(viewHolder.getLayoutPosition()-4));
                }
                else {
                    checklist.set(3,true);
                }
                return true;
            }
        });




        /*viewHolder.todaydesciptlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (selected.contains(String.valueOf(position))){
                    selected.remove(String.valueOf(position));
                    viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.border);
                }
                else {
                    selected.add(String.valueOf(position));
                    viewHolder.todaydesciptlayout.setBackgroundResource(R.drawable.selected_border);
                }
                onItemClick.onlongclick(position,"late",tasks.get(position).getTaskID());
                return true;
            }
        });*/


    }

    public final class MyItemViewHolder extends RecyclerView.ViewHolder {

        final View rootview;
        final CircleImageView todayimage;
        final TextView todaytitle,todaydescript;
        final RelativeLayout todayheader,parentlayout;
        final LinearLayout todaydesciptlayout;
        final ImageView todayoverbtn,start_tdytask,complt_tdytsk;

        public MyItemViewHolder(@NonNull View itemView) {
            super(itemView);

            rootview = itemView;
            parentlayout = itemView.findViewById(R.id.parenthomelayout);
            //todayheaderview = itemView.findViewById(R.id.textviewtoday);
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
                    onItemClick.onstartClick(getLayoutPosition(),"late",tasks.get(getLayoutPosition() - 4).getTaskID());
                }
            });

            complt_tdytsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.oncompleteclick(getLayoutPosition(),"late",tasks.get(getLayoutPosition() - 4).getTaskID());
                }
            });







        }

    }



    public final class MyHeaderViewHolder extends RecyclerView.ViewHolder{

        TextView todayheaderview;
        final RelativeLayout todayheader;
        ImageView todayoverbtn;

        public MyHeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            todayoverbtn = itemView.findViewById(R.id.todayoverviewbtn);
            todayheaderview = itemView.findViewById(R.id.textviewtoday);
            todayheader = itemView.findViewById(R.id.headertoday);
        }
    }




}
