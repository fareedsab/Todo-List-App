package com.example.todo20;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.todo20.Adapter.TaskAdapter;
import com.example.todo20.Models.AppDatabase;
import com.example.todo20.Models.Task;
import com.example.todo20.Models.Task_Interface;
import com.example.todo20.Models.TodoViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity<OnActivityResulty> extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    DrawerLayout drawer;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    TextView bottomsheetheading, taskview, name;
    NavigationView navigationView;
    ImageView imageView;
    EditText addtitle, adddate, adddescription, addtime;
    Spinner addpriority;
    Button addbutton, addtaskbt;
    List<Task> tasks;
    Bitmap bitmap1 = null;
    NotificationManager  notificationManager;
    BottomSheetDialog bottomSheetDialog;
    View bottomsheetview;
    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View dialogView;
    AlertDialog alertDialogProfile;
    ImageView imagecamera, imagegallery;
    RecyclerView recyclerView;
    String[] items = new String[]{"Low", "Medium", "High"};
    AppDatabase db;
    Task newtask;
    Task_Interface task_interface;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    TaskAdapter adapter;
    TodoViewModel todoViewModel;
    String priority;
    String encodedImage;
    ImageView defaultimage;
    public static boolean workerid;
    WorkManager workManager;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setid();
        notificationManager = (NotificationManager) Applications.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        rootNode = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        db = AppDatabase.getInstance(this);
        task_interface = db.task_interface();
        tasks= task_interface.getalltasks();


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            View headerView = navigationView.getHeaderView(0);
            name = (TextView) headerView.findViewById(R.id.username);

            reference = rootNode.getReference(signInAccount.getId());
            name.setText("WELCOME " + signInAccount.getDisplayName());
            CircleImageView image = headerView.findViewById(R.id.headimage);
            String personImage = Objects.requireNonNull(signInAccount.getPhotoUrl()).toString();
            Glide.with(this).load(personImage).into(image);

//
        }
//
        workerid=true;
      //  dialog.dismiss();
        ;
        workManager = WorkManager.getInstance(this);
        Data data = new Data.Builder().putString("id", LoginActivity.accid).build();
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(data)
                .build();
      //  workerid=startServiceRequest.getId();
        if (!MyWorker.isworkeractive) {

            Log.d("Worker", "onCreate: done ");


            workManager.enqueue(startServiceRequest);
            startServiceRequest.getId();
        }

        setDrawer();


        addtaskview();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                view.setMinDate(System.currentTimeMillis());
                //view.getMinDate(myCalendar.getTimeInMillis());
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);


                updateLabel();
            }
        };
        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                String sendtime;
                myCalendar.set(Calendar.MINUTE, minute);
                Calendar c = Calendar.getInstance();
                if (myCalendar.getTimeInMillis() >= c.getTimeInMillis()) {
                    //it's after current
                    //
                    updateLabel1();
                } else {
                    //it's before current'
                    Toast.makeText(getApplicationContext(), "Invalid Time", Toast.LENGTH_LONG).show();
                }


            }


        };

        bitmap1 = null;
        setroomdata();
        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        String temp = "getmAlltodaytasktask()";
        todoViewModel.getmAlltodaytasktask().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> todoList) {

                Log.d("TAG", "onChanged: " + todoList.toString());
                Log.d("TAG", "onChanged: " + todoList.size());
                tasks = todoList;
                adapter.UpdateList(todoList);
//                dialog.dismiss();
            }
        });
      //


    }


    public void addtaskview() {
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetDialog();

            }
        });
    }

    public void setDrawer() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(MainActivity.this, "HEllo WOrld", Toast.LENGTH_LONG).show();
                try {
                    if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.closeDrawer(Gravity.RIGHT);
                    } else {
                        drawer.openDrawer(Gravity.RIGHT);
                     //   Toast.makeText(MainActivity.this, "Gravity.LEFT", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.d("TAG", "onClick: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void setid() {
        drawer = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        imageView = findViewById(R.id.imageView);
        addbutton = findViewById(R.id.newtaskbutton);
        recyclerView = findViewById(R.id.recycle);
        taskview = findViewById(R.id.taskview);
        name = findViewById(R.id.username);

    }

    private void setBottomSheetDialog() {
        setbottomSheetDialog();
        BitmapDrawable drawable = (BitmapDrawable) defaultimage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        encodedImage = encodeImage(bitmap);



        addtaskbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(priority)) {
                    Toast.makeText(getApplicationContext(), "Select Priority", Toast.LENGTH_LONG).show();
                    return;
                }

                //  addtime.setText(dateFormat.format(myCalendar.getTime()));
                newtask = new Task(addtitle.getText().toString(), adddescription.getText().toString(), adddate.getText().toString(), addtime.getText().toString(), false, priority, false, encodedImage, false, false);
                try {

                    task_interface.insert(newtask);
                    Toast.makeText(MainActivity.this, "successfull", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Not successfull", Toast.LENGTH_LONG).show();
                }

                bottomSheetDialog.dismiss();
            }
        });



        bottomSheetDialog.show();
    }

    private void ChooseProfile(Context context) {
        builder = new AlertDialog.Builder(context);
        inflater = getLayoutInflater();

        dialogView = inflater.inflate(R.layout.dialog_profile_pic, null);
        imagecamera = dialogView.findViewById(R.id.imageViewCamera);
        imagegallery = dialogView.findViewById(R.id.imageViewgallery);
        builder.setCancelable(true);
        dialogView.setBackgroundColor(Color.WHITE);
        builder.setView(dialogView);

        alertDialogProfile = builder.create();

        alertDialogProfile.show();


        imagecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_LONG).show();
                if (chechAndrequestpermission(bottomSheetDialog.getContext())) {
                    TakePicFromCamera();
                    alertDialogProfile.cancel();
                }
            }
        });
        imagegallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Gallery", Toast.LENGTH_LONG).show();
                TakePicFromGallery();
                alertDialogProfile.cancel();

            }
        });


    }

    private boolean chechAndrequestpermission(Context context) {

        if (Build.VERSION.SDK_INT >= 23) {

            int cameraPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Context context) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TakePicFromCamera();
        } else {
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void TakePicFromCamera() {
        Intent takepic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takepic.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takepic, 2);
        }
    }

    private void TakePicFromGallery() {
        Intent ImageFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(ImageFromGallery, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedimage1 = data.getData();
                    final Uri imageUri = data.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedimage1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    encodedImage = encodeImage(selectedImage);
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    bitmap1 = (Bitmap) bundle.get("data");
                    encodedImage = encodeImage(bitmap1);
                    imageView.setImageBitmap(bitmap1);
                }
                break;
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        adddate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void updateLabel1() {
        String myFormat = "hh:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        addtime.setText(dateFormat.format(myCalendar.getTime()));
    }


    public void setroomdata() {
        tasks = task_interface.findtaskbystatus(false);
        adapter = new TaskAdapter(tasks, this, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                setbottomSheetDialog();


                bottomsheetheading.setText("Update Task");
                setBottomSheetDialogvalues(tasks.get(position));
                addtaskbt.setText("Update");
                addtaskbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        task_interface.update(addtitle.getText().toString(),
                                adddescription.getText().toString(),
                                adddate.getText().toString(),
                                addpriority.getSelectedItem().toString(),
                                tasks.get(position).getId(), false, false,encodedImage);
                        Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_LONG).show();
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        //dialog.dismiss();
    }

    private void setbottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
        bottomsheetview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_add_task_layout, findViewById(R.id.bottomsheetcontainer));
        bottomSheetDialog.setContentView(bottomsheetview);
        imageView = bottomSheetDialog.findViewById(R.id.image);
        defaultimage = bottomSheetDialog.findViewById(R.id.image);
        addpriority = bottomSheetDialog.findViewById(R.id.addtaskpriority);
        addtitle = bottomSheetDialog.findViewById(R.id.addTaskTitle);
        adddescription = bottomSheetDialog.findViewById(R.id.addTaskDescription);
        adddate = bottomSheetDialog.findViewById(R.id.addtaskDate);
        addtaskbt = bottomSheetDialog.findViewById(R.id.addTask);
        bottomsheetheading = bottomSheetDialog.findViewById(R.id.heading);
        addtime = bottomSheetDialog.findViewById(R.id.addtaskTime);
        addtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        time, myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();

            }
        });

        adddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();


            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseProfile(bottomSheetDialog.getContext());
             //   Toast.makeText(MainActivity.this, "Nikal", Toast.LENGTH_LONG).show();
            }
        });
        addpriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    priority = addpriority.getSelectedItem().toString();
                } else {
                    priority = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority = "";
            }
        });

        bottomSheetDialog.show();
    }

    private void setBottomSheetDialogvalues(Task task) {
        String[] priorities = getResources().getStringArray(R.array.priorities);
        addpriority.setSelection(Arrays.asList(priorities).indexOf(task.getTask_priority()));
        addtitle.setText(task.getTask_title());
        adddate.setText(task.getTask_date());
        adddescription.setText(task.getTask_description());
        addtime.setText(task.getTask_time());
        byte[] decodedString = Base64.decode(task.getTaskimage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);

    }

    public void timepick(Context context) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


            }
        }, hour, minute, true);//Yes sele24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        // return time;

    }

    public void hello(MenuItem item) {
        taskview.setText("History");

        drawer.closeDrawer(Gravity.RIGHT);
        todoViewModel.getpasttask().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> todoList) {

                Log.d("TAG", "onChanged: " + todoList.toString());
                Log.d("TAG", "onChanged: " + todoList.size());
                tasks = todoList;
                adapter.UpdateList(todoList);
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void home(MenuItem item) {
        taskview.setText("TODAY TASKS");
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        }
        todoViewModel.getmAlltodaytasktask().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> todoList) {

                Log.d("TAG", "onChanged: " + todoList.toString());
                Log.d("TAG", "onChanged: " + todoList.size());
                tasks = todoList;
                adapter.UpdateList(todoList);
            }
        });


    }


    public void Alltask(MenuItem item) {
        taskview.setText("ALL TASKS");

        drawer.closeDrawer(Gravity.RIGHT);
        todoViewModel.getAllTodos().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> todoList) {

                Log.d("TAG", "onChanged: " + todoList.toString());
                Log.d("TAG", "onChanged: " + todoList.size());
                tasks = todoList;
                adapter.UpdateList(todoList);
            }
        });
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public String getBase64Image(String base64String) {
        if (base64String != null) {
            return "data:image/" + "png" + ";base64," + base64String;
        } else {
            return null;
        }
    }


    public void logout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
        task_interface.deleteall();
        workerid=false;
        //workManager.cancelWorkById(workerid);
        workManager.cancelAllWork();
        notificationManager.cancelAll();
        List<Integer> alist = new ArrayList<>();


        MyWorker.isworkeractive=false;

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);


        //   FirebaseAuth.getInstance().getCurrentUser().delete();

    }
    public void synconlinedata()
    {
        DatabaseReference ref = reference.child("TodoDetails");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {  if(snapshot.child("TodoDetails").exists())
                {
                Task t =snapshot.getValue(Task.class);
                task_interface.insert(t);
                Log.d("TAG", "onChildAdded:1  "+t.toString());}
                else
            {
                return ;
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
       // dialog.dismiss();
    }
}