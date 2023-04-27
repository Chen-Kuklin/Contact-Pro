package com.chanik.ContactsManagement;

//import static androidx.core.app.AppOpsManagerCompat.Api29Impl.getSystemService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlramRecirver extends BroadcastReceiver {
    private int notificationID;
    private NotificationManager notificationManager;
    String names;
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //The time at which the notification should be sent
        int Selected_time = bundle.getInt("hour");
        String[] currentTime_arr;
        //Current time
       String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
       //Dividing the current time into hours and minutes
      currentTime_arr= currentTime.split(":");
        String  current_hour=currentTime_arr[0];
        //Turning the current time to Int
       int current_hour_int = Integer.parseInt(current_hour.replaceAll("[\\D]", ""));
        ManagerContacts manager = ManagerContacts.getInstance(context);
        //Get the names of contacts who have a birthday today
        names=manager.check_birthday();
        //If there are contacts who have a birthday today
        if (names!=null) {
            //If a suitable time to send a birthday alert will be sent
            if(Selected_time==current_hour_int)
                //A call to a method that is subject to notification
            setupNotification(context,names);
        }




    }
   // @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setupNotification(Context context, String names)
    {
        notificationID = 1;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //  Create Notification-Channel. ONLY for Android 8.0 (OREO API level 26) and higher.
        NotificationChannel notificationChannel = new NotificationChannel(
                "HIGH_CHANNEL_ID", // Constant for Channel ID
                "HIGH_CHANNEL_NAME", // Constant for Channel NAME
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);

                Notification notification = new NotificationCompat.Builder(context, "HIGH_CHANNEL_ID")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("A reminder to wish a happy birthday")
                        .setContentText(names)
                        .build();

                // Sending a notification that attacks the user in the status bar
                notificationManager.notify(notificationID, notification);
                notificationID++;
            }


}

