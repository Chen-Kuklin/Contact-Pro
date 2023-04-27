package com.chanik.ContactsManagement;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class DbSqlite extends SQLiteOpenHelper {

    private String myKey;
//Builder of DbSqlite
    public DbSqlite(Context context) {

        super(context, "Contactdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        //Create a table
        DB.execSQL("create Table ContactList(myKey TEXT primary key, name TEXT,address TEXT, email TEXT,birthdayDate TEXT,note TEXT,block TEXT, KEY_IMAGE BLOB )");
        DB.execSQL("create Table NotificationMode(State TEXT primary key,On_Off  TEXT,Time TEXT )");
    }
    @Override
    //Using an existing table
    public void onUpgrade(SQLiteDatabase DB, int i, int ii) {
        DB.execSQL("drop Table if exists ContactList");
        DB.execSQL("drop Table if exists NotificationMode");

    }
//A method that inserts a new row into a table
    public Boolean insertuserdata(String myKey, String name,String address,String email,String birthdayDate,String note,String block, byte[] image)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("myKey", myKey);
        contentValues.put("name", name);
        contentValues.put("address", address);
        contentValues.put("email", email);
        contentValues.put("birthdayDate", birthdayDate);
        contentValues.put("note",note);
        contentValues.put("block",block);
        contentValues.put("KEY_IMAGE",image);
        long result=DB.insert("ContactList", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    //A method that returns a value of whether or not this contact is blocked by a key
    public String getblock(String myKey)
    {
        this.myKey = myKey;
        boolean check= checkKayInDB(myKey);
        if(check==false)
            return "NO";
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = DB.rawQuery("Select * from ContactList where myKey = ?", new String[]{myKey});
        String value = "";
        res.moveToFirst();
        value=res.getString(6);
        return  value;


    }


  //Method that deletes a contact from the table by key
    public Boolean deletedata (String myKey )
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from ContactList where myKey = ?", new String[]{myKey});
        if (cursor.getCount() > 0) {
            long result = DB.delete("ContactList", "myKey=?", new String[]{myKey});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from ContactList", null);
        return cursor;
    }
    //The method checks if a number exists in the contacts list
    public boolean checkKayInDB(String key){
        Cursor res = getdata();
        while (res.moveToNext()) {
            if (res.getString(0).equals(key)) {
                return true;
            }
        }
        return false;
    }

    ////save Contact in sqlite
    public void save_Contact_in_sqlite(Contact contact) {
        String myKeyTXT = contact.getPhone().toString();
        String name = contact.getName().toString();
        String Address=contact.getAddress();
        String Email = contact.getEmail().toString();
        String birthdayDate = contact.getBirthdayDate().toString();
        String note = contact.getNote().toString();
        String block = contact.getifblock().toString();
        //Sending the image to a method that returns the image in an array of byte so that it can be inserted into sqlite
        byte[] b=getBytes( contact.getPhoto());
        //In case no such key already exists insert into sqlite
        if(checkKayInDB(myKeyTXT)==false) {
             insertuserdata(myKeyTXT, name,Address,Email,birthdayDate,note,block,b);
        }

    }
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    //The method enters the information of notification status and notification sending time
    public Boolean insertNotificationMode( String On_Off,String Time)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("State","State");
        contentValues.put("On_Off",On_Off);
        contentValues.put("Time",Time);

        long result=DB.insert("NotificationMode", null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
    //Check if a situation has been entered already
    public  boolean Check_notification_mode_entered ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from NotificationMode", null);
        if(cursor.getCount()==0)
            return false;
        return true;


    }
//Update the old status in a new mode selected by the user
    public  Boolean updateMode(String myKey, String myValue,String Time) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("On_Off", myValue);
        contentValues.put("Time", Time);
        Cursor cursor = DB.rawQuery("Select * from NotificationMode where State = ?", new String[]{myKey});
        if (cursor.getCount() > 0) {
            long result = DB.update("NotificationMode", contentValues, "State=?", new String[]{myKey});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

//The function returns the status of the existing settings
    public String getState(String myKey)
    {
        this.myKey = myKey;
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = DB.rawQuery("Select * from NotificationMode where State = ?", new String[]{myKey});
        String value = "";
        res.moveToFirst();
        value=res.getString(1);
        return  value;
    }
    //The function returns the selected manna to send a notification
    public String getTime(String myKey)
    {
        this.myKey = myKey;
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor res = DB.rawQuery("Select * from NotificationMode where State = ?", new String[]{myKey});
        String value = "";
        res.moveToFirst();
        value=res.getString(2);
        return  value;
    }
}