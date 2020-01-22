package com.ast.taskApp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerAdaper extends FragmentPagerAdapter {



    protected PagerAdaper(FragmentManager fm) {

        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment thisfrgmnt;
        if (position==0) {
            thisfrgmnt = Subscription.newInstance();
        } else if (position==1){
            thisfrgmnt = Subscription2.newInstance();
        } else{
            thisfrgmnt = Subscription3.newInstance();
        }
        return thisfrgmnt;

    }




    @Override
    public int getCount() {
        return 3;
    }
}
