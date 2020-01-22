package com.ast.taskApp;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class CarouselPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scaleFactor = 0.15f;
        float rotationFactor = 1;
        float min_alpha = 0.5f;

        if (position <= -1 ){
            //page.setAlpha(0.5f);
        }

          else if (position < 0) {

            page.setRotationY(rotationFactor * -position);
            float scale = 0.95f + scaleFactor * position;
            page.setScaleX(scale);
            page.setScaleY(scale);
            //page.setAlpha(0.5f);

        } else {

            page.setRotationY(rotationFactor * -position);
            float scale = 1f - scaleFactor * position;
            page.setScaleX(scale);
            page.setScaleY(scale);
            //page.setAlpha(1f);

        }
    }
}
