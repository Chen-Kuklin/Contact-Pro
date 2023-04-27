package com.chanik.ContactsManagement;



import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



public class ManagerContacts  {

    transient private Bitmap photo;
    DbSqlite DB;
    private static ManagerContacts instance;
    private Context context;
    private ArrayList<Contact> contacts = new ArrayList<>();

//Builder of ManagerContacts
    private ManagerContacts(Context context) {
        this.context = context;
        DB = new DbSqlite(context);
        //Send to a method that takes the contact list from sqlite
        get_from_sqlite();


    }
    ///// convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
//The method returns the instance
    public static ManagerContacts getInstance(Context context) {
        if(instance==null)
            instance = new ManagerContacts(context);

        return instance;
    }
//The method returns a contact according to its position in the list
    public Contact getContact(int position) {
        if (position < contacts.size())
            return contacts.get(position);

        return null;
    }
     ////add Contact to the list
    public void add_Contact(Contact contact) {
        contacts.add(contact);

    }
//Delete a contact from the contacts list
    public void removeContact(int position) {
        if(position<contacts.size())
            contacts.remove(position);
    }

    //The method creates a list of contacts that are stored in sqlite
    private void get_from_sqlite() {
        Cursor res = DB.getdata();
        if(res.getCount()==0){
            Toast.makeText(context, "No Entry Exists", Toast.LENGTH_SHORT).show();
            return;
        }
        //As long as there is something in sqlite
        while(res.moveToNext()){
            String phone=res.getString(0);
            String name=res.getString(1);
            String address=res.getString(2);
            String email=res.getString(3);
            String birthdayDate=res.getString(4);
            String timeToCall=res.getString(5);
            String block=res.getString(6);
            byte[] image = res.getBlob(7);
            photo= getImage (image);
//As long as there is something in sqlite
            Contact new_contact = new Contact(name,
                    phone,
                    address,
                    email,
                    birthdayDate,
                    timeToCall,
                    block,
                    photo);
            //Add the object to the list
            contacts.add(new_contact);
        }
    }
    //The method checks if today is the birthday of one or more of the contacts
    public String check_birthday() {
        String currentDate;
        String[] currentDate_splite;
        String[] Date_splite;
        String names = "Today is a birthday for:";
        Cursor res = DB.getdata();
        int flag=0;
        //If there are no contacts yet
        if(res.getCount()==0){
            return null;
        }
        //As long as there is something in sqlite
        while(res.moveToNext()){
            //Contact's birthday
            String birthdayDate=res.getString(4);
            //Name of contact
            String name=res.getString(1);
            //Receiving the current date
            currentDate= new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());
            currentDate_splite = currentDate.split("/");
            Date_splite = birthdayDate.split("/");
            //If the date of the birthday is equal to the current date
            if(currentDate_splite[0].equals(Date_splite[0])&&currentDate_splite[1].equals(Date_splite[1])){
                //If this is the first contact man we found he has a birthday today
                if(flag==0){
                    names+=" ";
                    names+=name;
                    flag=1;
                }
                //If this is not the first contact man we found he has a birthday today
                else {
                    names += "and ";
                    names += name;
                    names += " ";
                }

            }
        }
        //If there is no contact who has a birthday today
       if (flag==0)
           return null;
       //If there is a contact or more who have a birthday today
        return names;
    }


//The method returns a contact list
    public ArrayList<Contact> getContacts() {
        return contacts;
    }
}
