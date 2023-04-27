package com.chanik.ContactsManagement;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneReciver extends BroadcastReceiver {
    String number;
    DbSqlite DB;
    private String phoneNumber;
    String block;
    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        DB = new DbSqlite(context);
        //Receive phone status
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        try {
            //When a call comes in
            if(state!=null&&state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                //Receive a calling number
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
               //Check if the contact is blocked
               block=DB.getblock( phoneNumber);
                TelecomManager tm;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    tm = (TelecomManager)context.getSystemService(Context.TELECOM_SERVICE);
                    if (tm == null) {
                        throw new NullPointerException("tm == null");
                    }
                    //If this contact is blocked
                        if(block.equals("YES")) {
                            //Call barring
                            tm.endCall();

                        }
                };
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    }
