package com.chanik.ContactsManagement;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ScreenSplash extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        //Displays the opening screen for a few seconds
                        wait(4000);
                        Intent intent = new Intent
                                (ScreenSplash.this,MainActivity.class);
                        startActivity(intent);
                       // finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }





}
