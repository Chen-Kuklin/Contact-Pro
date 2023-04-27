package com.chanik.ContactsManagement;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.Calendar;

public class Notification_setting {
    DbSqlite DB;
    Context this_context;

   public  Notification_setting(Context context){
       this_context=context;
       DB = new DbSqlite(this_context);
  }
//Method that Disables Notification Alerts
    public void Notification_off() {
        //Saving the new status
        Boolean flag= DB.updateMode("State","OFF","00");
        //If no mode has been entered yet
      //  AlarmManager alarmManager = (AlarmManager)context.(ALARM_SERVICE);
       AlarmManager alarmManager = (AlarmManager) this_context.getSystemService(ALARM_SERVICE);
//// Create the same Intent
       Intent alarmIntent = new Intent(this_context, AlramRecirver.class);
//// Create PendingIntent with the same Intent & ALARM_ID & with different flag: FLAG_NO_CREATE
       PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this_context, 0,
                alarmIntent, PendingIntent.FLAG_NO_CREATE);
//// If alarmPendingIntent is not null – alarm is SET ON – so we can cancel it
        if (alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
            alarmPendingIntent.cancel();
        }

    }
    //Cool method Notification notifications
    public void Notification_on() {
        //Sending to a method that checks whether the user has already changed the default state of sending the notification
        boolean check=DB.Check_notification_mode_entered();
        //If the default situation has changed
        if(check==true) {
            //Accepting the current situation
            String state=DB.getState("State");
            //If the mode was off but the user selected a specific time to send a notification
            if(state.equals("Off_Selected_Time")){
                //Receiving time to send the notification already selected by the user
                String time= DB.getTime("State");
                //Saving the new status
                 DB.updateMode("State","ON","time");
                //Send to a method that will send a notification at the selected time
                Notification_in_Selected_time(time);
                return;
            }
        }
        //Note that the notification is on
        Boolean flag= DB.updateMode("State","ON","14:00");
        //If no mode has been entered yet
        if(flag==false)
            DB.insertNotificationMode("ON","14:00");
        int hour=14;
        AlarmManager am = (AlarmManager) this_context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this_context,AlramRecirver.class);
        //Send the time when the alert should be sent
        intent.putExtra("hour",hour);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this_context, 0,
                intent, PendingIntent.FLAG_NO_CREATE);
        //In case no alert has been set yet
        if(alarmPendingIntent==null) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY,hour);
            c1.set(Calendar.MINUTE,00);
            long time = c1.getTimeInMillis();
            //Set a daily alert at the requested time
            am.setRepeating(AlarmManager.RTC_WAKEUP, time,  AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
    //The method sets an alert while the user has set
    public void Notification_in_Selected_time(String select_time ) {
        String state=DB.getState("State");
        //If the user selects an alert sending time but the alerts are off
        if(state.equals("OFF")||state.equals("Off_Selected_Time")){
            //Saving the new status
            Boolean flag= DB.updateMode("State","Off_Selected_Time",select_time);
            return;
        }
// off exist Notification
        Notification_off();
        //Saving the new status
        Boolean flag= DB.updateMode("State","ON_Selected_Time",select_time);
        String[] select_time_arr;
        //Dividing the selected time into hours and minutes
        select_time_arr= select_time.split(":");
        String  select_hour=select_time_arr[0];
        //Turning the selected hour time to Int
        int select_hour_int = Integer.parseInt(select_hour.replaceAll("[\\D]", ""));
        AlarmManager am = (AlarmManager) this_context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this_context, AlramRecirver.class);
        intent.putExtra("hour",select_hour_int);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
     //New timeout for sending alere
            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.HOUR_OF_DAY,select_hour_int);
            c1.set(Calendar.MINUTE,00);
            long time1 = c1.getTimeInMillis();
      //  Set a daily alert at the requested time
            am.setRepeating(AlarmManager.RTC_WAKEUP,time1, AlarmManager.INTERVAL_DAY, pendingIntent);

        }
    }

