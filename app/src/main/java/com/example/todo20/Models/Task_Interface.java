package com.example.todo20.Models;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface Task_Interface {
   @Insert
    void insert(Task tasks);

   @Query("INSERT INTO Task (isdeleted,id,date,description,priority,status,issync,time,title,image,isnotify)" +
           " VALUES (:isdelete,:id,:date,:description,:priority,:status,:issync,:time,:title,:image,:isnotify)")
   void add(boolean isdelete,int id,String date,String description,String priority,boolean status,boolean issync,String time,String title,String image,boolean isnotify);

   @Query("SELECT EXISTS(SELECT * FROM Task WHERE id= :taskid)")
    Boolean is_exist(int taskid);
    @Query("SELECT * FROM Task")
    List<Task> getalltasks();

    @Query("DELETE FROM Task WHERE id = :id")
    void deletebyid(int id);

    @Query("SELECT * FROM Task WHERE status = :status")
    List<Task>findtaskbystatus(Boolean status);
    @Query("UPDATE Task SET status = :status, issync = :isync WHERE id = :task_id " )
    void updatestatus(int task_id,boolean status,boolean isync);

    @Query("UPDATE Task SET title= :task_title , description= :task_description,date = :task_date, priority = :task_priority, isnotify =:notify, issync = :sync,image = :image WHERE id = :task_id")
    void update(String task_title,String task_description,String task_date,String task_priority,int task_id,boolean notify,boolean sync,String image);

    @Query("SELECT * FROM Task WHERE status = :status ")
    LiveData<List<Task>> findalltasksbystatus(boolean status);
 @Query("SELECT * FROM Task")
 LiveData<List<Task>> findalltasks();
 @Query("SELECT * FROM Task WHERE date = :date")
 List<Task> findtodaytask(String date);
 @Query("SELECT * FROM Task WHERE date = :date")
 LiveData<List<Task>> findlivetodaytask(String date);
 @Query("SELECT * FROM Task WHERE date < :date")
 LiveData<List<Task>> findlivepastask(String date);
 @Query("UPDATE Task SET isnotify = :status WHERE id = :task_id " )
 void updatenotify(int task_id,boolean status);
 @Query("UPDATE Task SET isdeleted = :isdelete WHERE id= :task_id")
 void deletetask(boolean isdelete, int task_id);
 @Query("DELETE FROM Task")
 void deleteall();

}
