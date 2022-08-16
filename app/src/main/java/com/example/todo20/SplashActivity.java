package com.example.todo20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        logo=findViewById(R.id.logo);
        text= findViewById(R.id.text);
        Thread thread = new Thread(){
          public void run(){
              try{
                  sleep(4000);

              }catch(Exception e){
                  e.printStackTrace();

                 }finally{
                  Intent intent =  new Intent(SplashActivity.this,LoginActivity.class);
                  startActivity(intent);

            }
          }
        };thread.start();

    }
}