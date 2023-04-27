package com.chanik.ContactsManagement;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;



public class Contact  implements Serializable{

    private String name;
    private String phone;
    private String address;
    private String email;
    private String birthdayDate;
    private String note;
    private String block;
    transient private Bitmap photo;
//Constructor of an object type Contact
    public Contact(String name, String phone, String address, String email, String birthdayDate, String note, String block, Bitmap photo) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.birthdayDate = birthdayDate;
        this.note = note;
        this.block =block;
        this.photo = photo;
    }

    public Contact() {}
//A method that returns a contact name
    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public String getEmail() {
        return email;
    }
    public String getBirthdayDate() {
        return birthdayDate;
    }
    public String getNote() {
        return note;
    }
    public String getifblock() {
        return block;
    }
    public Bitmap getPhoto() {
        return photo;
    }

}