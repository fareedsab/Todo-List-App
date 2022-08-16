package com.example.todo20.Models;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.todo20.OnItemClickListener;
@Entity
public class Task {


    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo (name = "title")
    String task_title;
    @ColumnInfo (name = "description")
    String task_description;

    public Task() {
    }

//    public Task(String task_title, String task_description, String task_date, Boolean task_sync, String task_time, boolean task_status, String task_priority, boolean tasknotify, String taskimage) {
//        this.task_title = task_title;
//        this.task_description = task_description;
//        this.task_date = task_date;
//        this.task_sync = task_sync;
//        this.task_time = task_time;
//        this.task_status = task_status;
//        this.task_priority = task_priority;
//        this.tasknotify = tasknotify;
//        this.taskimage = taskimage;
//    }

    @ColumnInfo (name = "date")
    String task_date;
    @ColumnInfo (name = "issync")
    Boolean task_sync;

    public boolean isDelete_status() {
        return delete_status;
    }

    public void setDelete_status(boolean delete_status) {
        this.delete_status = delete_status;
    }

    @ColumnInfo(name = "isdeleted")
    boolean delete_status;

    public Boolean getTask_sync() {
        return task_sync;
    }

    public void setTask_sync(Boolean task_sync) {
        this.task_sync = task_sync;
    }

    public String getTask_time() {
        return task_time;
    }

    public void setTask_time(String task_time) {
        this.task_time = task_time;
    }

    @ColumnInfo(name = "time")
    String task_time;
    @ColumnInfo (name = "status")
    boolean task_status;
    @ColumnInfo (name = "priority")
    String    task_priority;
    @ColumnInfo(name = "isnotify")
    boolean tasknotify;
    @ColumnInfo(name = "image")
    String taskimage;

    public Task(String task_title, String task_description, String task_date, String task_time, boolean task_status, String task_priority, boolean tasknotify, String taskimage,boolean syncstatus, boolean delete_status ) {
        this.task_title = task_title;
        this.task_description = task_description;
        this.task_date = task_date;
        this.task_time = task_time;
        this.task_status = task_status;
        this.task_priority = task_priority;
        this.tasknotify = tasknotify;
        this.taskimage = taskimage;
        this.task_sync=syncstatus;
        this.delete_status  = delete_status;
    }
    public Task(int id ,String task_title, String task_description, String task_date, String task_time, boolean task_status, String task_priority, boolean tasknotify, String taskimage,boolean syncstatus, boolean delete_status ) {
        this.task_title = task_title;
        this.task_description = task_description;
        this.task_date = task_date;
        this.task_time = task_time;
        this.task_status = task_status;
        this.task_priority = task_priority;
        this.tasknotify = tasknotify;
        this.taskimage = taskimage;
        this.task_sync=syncstatus;
        this.delete_status  = delete_status;
        this.id=id;
    }

    public String getTaskimage() {
        return taskimage;
    }

    public void setTaskimage(String taskimage) {
        this.taskimage = taskimage;
    }

    public boolean getTasknotify() {
        return tasknotify;
    }

    public void setTasknotify(boolean tasknotify) {
        this.tasknotify = tasknotify;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", task_title='" + task_title + '\'' +
                ", task_description='" + task_description + '\'' +
                ", task_date='" + task_date + '\'' +
                ", task_status=" + task_status +
                ", task_priority='" + task_priority + '\'' +
                '}';
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

//    public Bitmap getTask_pic() {
//        return task_pic;
//    }

    //public void setTask_pic(Bitmap task_pic) {
//        this.task_pic = task_pic;
//    }

    public boolean getTask_status() {
        return task_status;
    }

    public void setTask_status(boolean task_status) {
        this.task_status = task_status;
    }

    public String getTask_priority() {
        return task_priority;
    }

    public void setTask_priority(String task_priority) {
        this.task_priority = task_priority;
    }

}
