package com.ast.taskApp.Database;

import androidx.room.TypeConverter;

import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

class ConverterRoom {



    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static Long fromDate(Timestamp date) {
        if (date==null) {
            return(null);
        }

        return(date.toDate().getTime());


    }

    @TypeConverter
    public static Timestamp toDate(Long millisSinceEpoch) {
        if (millisSinceEpoch==null) {
            return(null);
        }
            Date thisDate = new Date();
            thisDate.setTime(millisSinceEpoch);
        return(new Timestamp(thisDate));
    }



}