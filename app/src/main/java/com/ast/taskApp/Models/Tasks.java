package com.ast.taskApp.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
@Entity
public class Tasks implements Serializable {

    @PrimaryKey @NonNull
    private String taskID;
    private Timestamp endDate,endTime,startDate,startTime;
    private String location;
    private String name;
    private String platform;
    private String tag;
    private String taskImageUrl;
    private String tuneName;
    private String tuneUrl;
    private String userID;
    private String fromSplash;
    private Integer repeatOn,taskStatus,alaramID;
    private boolean vibrateonEnd,vibrateeonStart;
    private ArrayList<String> weekdays = new ArrayList<>();

    @Ignore
    public Tasks() {}

    public Tasks(@NonNull String taskID, Timestamp endDate,
                 Timestamp endTime, Timestamp startDate,
                 Timestamp startTime, String location,
                 String name, String platform, String tag,
                 String taskImageUrl, String tuneName, String tuneUrl,
                 String userID, String fromSplash, Integer repeatOn,
                 Integer taskStatus, Integer alaramID, boolean vibrateonEnd,
                 boolean vibrateeonStart, ArrayList<String> weekdays) {
        this.taskID = taskID;
        this.endDate = endDate;
        this.endTime = endTime;
        this.startDate = startDate;
        this.startTime = startTime;
        this.location = location;
        this.name = name;
        this.platform = platform;
        this.tag = tag;
        this.taskImageUrl = taskImageUrl;
        this.tuneName = tuneName;
        this.tuneUrl = tuneUrl;
        this.userID = userID;
        this.fromSplash = fromSplash;
        this.repeatOn = repeatOn;
        this.taskStatus = taskStatus;
        this.alaramID = alaramID;
        this.vibrateonEnd = vibrateonEnd;
        this.vibrateeonStart = vibrateeonStart;
        this.weekdays = weekdays;
    }

    @NonNull
    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(@NonNull String taskID) {
        this.taskID = taskID;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTaskImageUrl() {
        return taskImageUrl;
    }

    public void setTaskImageUrl(String taskImageUrl) {
        this.taskImageUrl = taskImageUrl;
    }

    public String getTuneName() {
        return tuneName;
    }

    public void setTuneName(String tuneName) {
        this.tuneName = tuneName;
    }

    public String getTuneUrl() {
        return tuneUrl;
    }

    public void setTuneUrl(String tuneUrl) {
        this.tuneUrl = tuneUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFromSplash() {
        return fromSplash;
    }

    public void setFromSplash(String fromSplash) {
        this.fromSplash = fromSplash;
    }

    public Integer getRepeatOn() {
        return repeatOn;
    }

    public void setRepeatOn(Integer repeatOn) {
        this.repeatOn = repeatOn;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getAlaramID() {
        return alaramID;
    }

    public void setAlaramID(Integer alaramID) {
        this.alaramID = alaramID;
    }

    public boolean isVibrateonEnd() {
        return vibrateonEnd;
    }

    public void setVibrateonEnd(boolean vibrateonEnd) {
        this.vibrateonEnd = vibrateonEnd;
    }

    public boolean isVibrateeonStart() {
        return vibrateeonStart;
    }

    public void setVibrateeonStart(boolean vibrateeonStart) {
        this.vibrateeonStart = vibrateeonStart;
    }

    public ArrayList<String> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<String> weekdays) {
        this.weekdays = weekdays;
    }
}
