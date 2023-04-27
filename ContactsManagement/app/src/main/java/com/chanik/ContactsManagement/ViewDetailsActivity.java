package com.chanik.ContactsManagement;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


public class ViewDetailsActivity extends Activity{
    DbSqlite DB;
    int contactPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_contact_activity);
//The location of the contact from the list they clicked on
        contactPosition = getIntent().getIntExtra("contact",0);
        Contact contact = ManagerContacts.getInstance(this).getContact(contactPosition);
        final int[] checkedItem = {-1};
        DB = new DbSqlite(ViewDetailsActivity.this);
        final TextView nameTv = findViewById(R.id.contact_name_output);
        TextView phoneTv = findViewById(R.id.contact_phone_output);
        TextView emailTv = findViewById(R.id.contact_email_output);
        TextView addressTv = findViewById(R.id.contact_address_output);
        //ImageView photoIv = findViewById(R.id.contact_photo_output);
        TextView birthdayTv = findViewById(R.id.contact_birthday_output);
        TextView noteTv = findViewById(R.id.contact_note);
        TextView blockedTv = findViewById(R.id.Is_this_contact_blocked_output);
        Button deletContactBtn = findViewById(R.id.Delete_btn);
        //View contact information
        nameTv.setText(contact.getName());
        phoneTv.setText(contact.getPhone());
        //In case no email was entered
        if(contact.getEmail().toString().equals(""))
            emailTv.setText("");
        //In case  email was entered
            else
            emailTv.setText(contact.getEmail());
        //In case no addres was entered
        if(contact.getAddress().toString().equals(""))
            addressTv.setText("");
        //In case addres was entered
        else
            addressTv.setText(contact.getAddress());
        birthdayTv.setText(contact.getBirthdayDate());
        noteTv.setText(contact.getNote());
        blockedTv.setText(contact.getifblock());
//Create a listener to click on a phone number
        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((TextView)view).getText().toString();
                //Displays the phone number on the dialer
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+text));
                startActivity(intent);
            }
        });
        //In case the user has added an email to a contact
        if(emailTv.getText().toString()!="") {
            emailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String text = ((TextView) view).getText().toString();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{text});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Hello " + nameTv.getText().toString());
                    intent.putExtra(Intent.EXTRA_TEXT, "Email body...");
                    intent.setType("text/html");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            });
        }
        if(addressTv.getText().toString()!="") {
            addressTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String text = ((TextView) view).getText().toString();

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse( "waze://?q=" +text));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +"com.waze")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +"com.waze")));
                        }
                    }
                }
            });
        }
        //Listener Click to delete contact
        deletContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AlertDialog builder instance to build the alert dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewDetailsActivity.this);

                // set the custom icon to the alert dialog
                //alertDialog.setIcon(R.drawable.image_logo);

                // title of the alert dialog
                alertDialog.setTitle("Are you sure you want to delete this contact?");

                // list of the items to be displayed to
                // the user in the form of list
                // so that user can select the item from
                final String[] listItems = new String[]{"YES", "NO"};

                // the function setSingleChoiceItems is the function which builds
                // the alert dialog with the single item selection
                alertDialog.setSingleChoiceItems(listItems, checkedItem[0], new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // update the selected item which is selected by the user
                        // so that it should be selected when user opens the dialog next time
                        // and pass the instance to setSingleChoiceItems method
                        checkedItem[0] = which;

                        // now also update the TextView which previews the selected item
                        //  tvSelectedItemPreview.setText("Selected Item is : " + listItems[which]);

                        // when selected an item the dialog should be closed with the dismiss method


                    }
                });
                alertDialog.setNegativeButton("FINISH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(checkedItem[0]==0) {
                            deletContact(contact.getPhone());
                        }
                    }

                });
                // create and build the AlertDialog instance
                // with the AlertDialog builder instance
                AlertDialog customAlertDialog = alertDialog.create();

                // show the alert dialog when the button is clicked
                customAlertDialog.show();
            }
        });


    }
//The function deletes the selected contact
   private void deletContact(String mykey){
        DB.deletedata(mykey);
        ManagerContacts.getInstance(this).removeContact(contactPosition);
        Intent gotoScreenVar = new Intent(ViewDetailsActivity.this, ViewListActivity.class);
        //In order not to keep the back
        gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(gotoScreenVar);
    }
}
