package com.example.todo20.Models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TodoViewModel extends AndroidViewModel{
    public static boolean newstatus = false;
    AppDatabase appDatabase;
    private LiveData<List<Task>> mAllIncompletedtask;
    private LiveData<List<Task>> mAlltask;
    private LiveData<List<Task>> pastdata;
    private LiveData<List<Task>> mAlltodaytask;
    public TodoViewModel(@NonNull Application application) {
        super(application);
        appDatabase= AppDatabase.getInstance(application.getApplicationContext());

        mAllIncompletedtask=appDatabase.task_interface().findalltasksbystatus(newstatus);
        mAlltask=appDatabase.task_interface().findalltasks();
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        Calendar myCalendar=Calendar.getInstance();
        mAlltodaytask=appDatabase.task_interface().findlivetodaytask(dateFormat.format(myCalendar.getTime()));
        pastdata=appDatabase.task_interface().findlivepastask(dateFormat.format(myCalendar.getTime()));


    }
    public  LiveData<List<Task>> getmAlltask()
    {
        return mAllIncompletedtask;
    }
     public LiveData<List<Task>> getAllTodos(){
        return mAlltask;


    }
    public  LiveData<List<Task>> getmAlltodaytasktask()
    {
        return mAlltodaytask;
    }
    public LiveData<List<Task>> getpasttask(){
        return pastdata;
    }
}