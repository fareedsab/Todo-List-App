package com.example.todo20;

import static android.content.Context.MODE_APPEND;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.todo20.Models.AppDatabase;
import com.example.todo20.Models.Task;
import com.example.todo20.Models.Task_Interface;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {
    public static boolean isworkeractive = false;
    String channel_id = "notification_channel2";
    List<Task> tasks;
    DatabaseReference reference;
    FirebaseDatabase rootNode;
    NotificationManager notificationManager1;
    Context context;
    SharedPreferences sh;
    String accountid;
    public MyWorker
            (@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        rootNode = FirebaseDatabase.getInstance();

        sh = context.getSharedPreferences("Accountdetails", Context.MODE_PRIVATE);

        this.context=context;

//        fusedLocationProviderClient.requestLocationUpdates(locationRequest,callback ,null);
    } @SuppressLint("MissingPermission")

    public void showNotification(Context co, String title, String message, int id, boolean notitype, String myBitmap1) {
        // Pass the intent to switch to the MainActivity
        byte[] decodedString = Base64.decode(myBitmap1, Base64.DEFAULT);
        Bitmap myBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Intent intent = new Intent(co,SplashActivity.class);
        // Assign channel ID

        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass the intent to PendingIntent to start the
        // next Activity
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(co,
                    0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(co,
                    0, intent, 0);
        }


        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(Applications.getAppContext(),
                channel_id)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(false)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                .setOngoing(notitype)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(myBitmap));


        notificationManager1 = (NotificationManager) Applications.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Check if the Android Version is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH);
            notificationManager1.createNotificationChannel(notificationChannel);
        }
        notificationManager1.notify(id, builder.build());
    }
    AppDatabase db;
    Task_Interface task_interface;

    @Override
    public Result doWork() {

        accountid= getInputData().getString("id");
        reference = rootNode.getReference(accountid);
        isworkeractive=true;
        Log.d("TAG", "starting service from doWork");
        Log.d("TAG", "doWork called for: " + this.getId());
        Calendar c = Calendar.getInstance();
        Date d1=null,d2=null;
        String myFormat = "hh:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        String time = dateFormat.format(c.getTime());
        String myFormat1 = "MM/dd/yy";
        SimpleDateFormat dateFormat1 = new SimpleDateFormat(myFormat1, Locale.US);
        Calendar myCalendar=Calendar.getInstance();
        db=AppDatabase.getInstance(context);
        task_interface=db.task_interface();
        tasks= task_interface.getalltasks();
        String value;
        if(haveNetworkConnection()){
            if(MainActivity.workerid && isworkeractive)
            {
            reference.child("TodoDetails").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("TAG", "onChildAdded: "+snapshot.getKey()+" "+snapshot.getRef());
                   // snapshot.getRef().removeValue();
                    if(!checkdetails(snapshot.getKey()))
                    {
                        Log.d("TAG", "onChildAdded: "+snapshot.getKey());
                        snapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }
        if(tasks.size()!=0)
        {
            for (int i = 0 ;i < tasks.size();i++)
            {
                if(!tasks.get(i).getTask_sync())
                {
                    reference.child("TodoDetails").child(String.valueOf(tasks.get(i).getId())).setValue(tasks.get(i));
                   // task_interface.updatestatus(tasks.get(i).getId(),tasks.get(i).getTask_status(),true);
                  //  tasks.get(i).setTask_sync(true);
                }
            }
        }
        }
        tasks= task_interface.findtodaytask(dateFormat1.format(myCalendar.getTime()));


        if(tasks.size()!=0) {
            for(int i = 0 ;i<tasks.size();i++)
            {
                if((!tasks.get(i).getTask_status())&&(!tasks.get(i).getTasknotify()))
                {
                    Log.d("TAG", "doWork: "+tasks.get(i).getTask_priority());
                    try{
                    d1= dateFormat.parse(time);
                    d2=dateFormat.parse(tasks.get(i).getTask_time());


                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                    long diff = d2.getTime() - d1.getTime();
                    long diffMinutes = diff / (60 * 1000);
                    Log.d("TAG", "doWork: "+diffMinutes);
                    if(tasks.get(i).getTask_priority().equalsIgnoreCase("high"))
                    {
                        task_interface.updatenotify(tasks.get(i).getId(), true);
                        showNotification(context, "TODO", tasks.get(i).getTask_title() + " " + time, tasks.get(i).getId(), true,tasks.get(i).getTaskimage());

                    }
                    if(tasks.get(i).getTask_priority().equalsIgnoreCase("medium"))
                    {

                        if(diffMinutes<=10 && diffMinutes>=0)
                        {
                            showNotification(context,"TODO",tasks.get(i).getTask_title() + " " + time, tasks.get(i).getId(),false,tasks.get(i).getTaskimage());
                            task_interface.updatenotify(tasks.get(i).getId(), true);

                        }
                    }
                    if(tasks.get(i).getTask_priority().equalsIgnoreCase("Low"))
                    {
                        Log.d("TAG", "doWork:ddd");
                        if(diffMinutes<=5 && diffMinutes>=0)
                        {
                            showNotification(context,"TODO",tasks.get(i).getTask_title() + " " + time, tasks.get(i).getId(),false,tasks.get(i).getTaskimage());
                            task_interface.updatenotify(tasks.get(i).getId(), true);

                        }

                    }

                }
            }
        }

       String title = "Message from Activity!";
        Log.d("TAG", "doWork: "+"is running");
//        sendNotification(title, time);

        Data data =new Data.Builder().putString("id",accountid).build();
        final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(10, TimeUnit.SECONDS)
                .addTag("simple_work")
                .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(simpleRequest);
      //  MainActivity.workerid=simpleRequest.getId();
        return Result.success();
    }

    private boolean checkdetails(String key) {
        db=AppDatabase.getInstance(context);
        task_interface=db.task_interface();
        Log.d("TAG", "checkdetails: "+Integer.parseInt(key));
        Log.d("TAG", "checkdetails: "+ task_interface.is_exist(Integer.parseInt(key)));
        return task_interface.is_exist(Integer.parseInt(key));


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;


        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }




}
