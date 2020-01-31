package com.ast.taskApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ast.taskApp.Activities.NewTaskActivity;
import com.unsplash.pickerandroid.photopicker.UnsplashPhotoPicker;
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto;
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity;

import java.util.ArrayList;

public class Gallery extends AppCompatActivity {
    UnsplashPhotoPicker unsplashPhotoPicker;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==7 && resultCode==RESULT_OK){


            ArrayList<UnsplashPhoto> photos = data.getExtras().getParcelableArrayList(UnsplashPickerActivity.EXTRA_PHOTOS);

            //String thumb = photos.get(0).getUrls().getThumb();



            Intent intent = new Intent(Gallery.this, NewTaskActivity.class);
            intent.putExtra("url",photos.get(0).getUrls().getLarge());
            startActivity(intent);



        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        UnsplashPhotoPicker.INSTANCE.init(
                getApplication(),
                "daaa0d70c7e6eb3f47daab9decbf54d97029ed9a3a3bfd80dea0505ad9ad52be",
                "5aaad8f3e5c91c3004a66fe4e09da4ef1aa180045bd218feac7c16c31b2bb90d", 20);



        startActivityForResult(
                UnsplashPickerActivity.Companion.getStartingIntent(
                        this, // context
                        false
                ), 7
        );



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
