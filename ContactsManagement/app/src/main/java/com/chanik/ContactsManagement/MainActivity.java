package com.chanik.ContactsManagement;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    private PhoneReciver PhoneStateReciver;
    private IntentFilter filter;
    Bitmap bitmap;
    DbSqlite DB;

    private NotificationManager notificationManager;
    private Button btnSend;
   // @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.photo_me);
        DB = new DbSqlite(this);
        Notification_setting notifi;
        //Send a birthday reminder notification in case there is a day
        notifi = new Notification_setting(MainActivity.this);
     //Sending to a method that checks whether the user has already changed the default state of sending the notification
        boolean check=DB.Check_notification_mode_entered();
        //If the default situation has not changed
        if(check==false)
            //Sending to a method that defines sending a notification message at the default time of 14:00
            notifi.Notification_on();
//Sending to a method that  requests phone permissions if it does not exist
        AskPermissions();
        //Sending to listen to conversations
        PhoneReciver PhoneStateReciver;
        this.sendBroadcast(new Intent(this, PhoneReciver.class));
//Thread that puts a high-quality background image taken from the web
       new Thread(new Runnable()
        {
            public void run()
            {
                //A call to a method that loads an image from the Internet
               // bitmap = loadImageFromNetwork("https://drive.google.com/uc?id=1_39tz6hfSRn0CHL06a4lTyii-JE4T0Of");
                 bitmap = loadImageFromNetwork("https://drive.google.com/uc?id=1EorBzXW5SintRjXcRLIwVeAOb4_4BkKr");
                runOnUiThread(new Runnable()
                {
                    public void run() {
                       // Put the uploaded image in the background
                        imageView.setImageBitmap(bitmap);;
                    }
                });
            }
        }).start();

        Button addContactBtn = findViewById(R.id.add_contact_btn);
      //Listener at the push of a button to add a new minister
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNewContactActivity.class);
                startActivity(intent);

            }
        });
//Listen to the click of a button to see the contact list
        Button showContactsBtn = findViewById(R.id.show_contacts_btn);
        showContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ViewListActivity.class);
                startActivity(intent);


            }
        });

    }

    ////////////////////////////////////////////////////////////////////////
    //When no permission comes here
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //The method uploads an image from the Internet
    private Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//The method creates a point menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem1 = menu.add("About");
        MenuItem menuItem2 = menu.add("settings");
        MenuItem menuItem3 = menu.add("Exit");
        //When an event clicks on about
        menuItem1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Send to method that prints the app details
                showAboutDialog();
                return true;
            }
        });
// When an event clicks on Exit
        menuItem3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Send to method that exits the app
                showExitDialog();
                return true;
            }
        });
        //When an event clicks on settings
        menuItem2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

                return true;
            }
        });

        return true;

    }
//The method prints the app details
    private void showAboutDialog() {
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setIcon(R.mipmap.ic_launcher);
        aboutDialog.setTitle("About App");
        aboutDialog.setMessage("Application Name: ContactsManagement\n" +
                "Student name:chani kuklin\n" +
                "Date of Submission:19.6.22\n" +
                "Operating system details:Android 11.0 (R) API Level 30");
        aboutDialog.setCancelable(true);

        aboutDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        aboutDialog.show();
    }
//The method exits the app
private void showExitDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setIcon(R.drawable.ic_exit);
        exitDialog.setTitle("Exit App");
        exitDialog.setMessage("Do you really want to exit?");
        exitDialog.setCancelable(false);

        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // destroy & exit this activity
            }
        });

        exitDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        exitDialog.show();
    }
    //The method checks whether the permissions exist
    private boolean hasPermissions(Context context, String... PERMISSIONS) {

        if (context != null && PERMISSIONS != null) {

            for (String permission: PERMISSIONS){

                if (ActivityCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        return true;
    }
    //The method requests phone permissions if it does not exist
    private void AskPermissions() {
        String[] PERMISSIONS;
    PERMISSIONS = new String[] {

        Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS
    };
        if (!hasPermissions(MainActivity.this,PERMISSIONS)) {
        ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS,1);
    }
    }
}





