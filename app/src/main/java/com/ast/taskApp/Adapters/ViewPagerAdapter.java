package com.ast.taskApp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.ast.taskApp.Fragments.TaskCounterFragment;
import com.ast.taskApp.Models.Tasks;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Tasks tasks;
    private ArrayList<Tasks> selectedtasks;


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Tasks> selectedtasks) {
        super(fm, behavior);
        this.selectedtasks = selectedtasks;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        tasks = selectedtasks.get(position);
        fragment = TaskCounterFragment.initializetskcounterfrgment(tasks);
        return fragment;
    }

    @Override
    public int getCount() {
        return selectedtasks.size();
    }
}
