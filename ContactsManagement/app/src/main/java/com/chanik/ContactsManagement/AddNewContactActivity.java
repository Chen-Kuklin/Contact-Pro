package com.chanik.ContactsManagement;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;


import android.app.Activity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;

public class AddNewContactActivity extends AppCompatActivity {
    String dateString;
    String[] words;
    String nameB;
    String dateTonotify;
    DbSqlite DB;



    EditText nameEt, phoneEt, emailEt, addressEt,noteEt;
    Bitmap bitmap;
    ImageView imageView;
    TextView dateTv, blockTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = new DbSqlite(this);
        setContentView(R.layout.add_new_contact_activity);
        nameEt = findViewById(R.id.name_input);
       nameEt.setText("");
        phoneEt = findViewById(R.id.phone_input);
        phoneEt.setText("");
        emailEt = findViewById(R.id.email_input);
        emailEt.setText("");
        addressEt = findViewById(R.id.address_input);
        addressEt.setText("");
        noteEt=findViewById(R.id.Note_input);
        imageView = findViewById(R.id.photo_result);
        dateTv = findViewById(R.id.birthdate_output);
        blockTv = findViewById(R.id.block_output);

      //  Listens to a button that adds an image
        Button takePicBtn = findViewById(R.id.pic_btn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                someActivityResultLauncher.launch(cameraIntent);
//
            }
        });
//Listens to a button that adds a birthday date
        Button dateBtn = findViewById(R.id.birthdate_btn);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(AddNewContactActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        dateTv.setText(i2 + "/" + (i1 + 1) + "/" + i);
                        dateTonotify=i2+"-"+(i1 + 1)+"-"+i;
                    }
                }, year, month, day);
                dpd.show();
            }
        });

       // Listens to a button that blocks a contact
        Button blockBtn = findViewById(R.id.block_btn);
        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] checkedItem = {-1};
                // AlertDialog builder instance to build the alert dialog
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(AddNewContactActivity.this);

                // title of the alert dialog
                alertDialog.setTitle("Are you sure you want to block this contact?");

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
                            blockTv.setText("YES");
                        }
                        else
                            blockTv.setText("NO");
                    }

                });
                // create and build the AlertDialog instance
                // with the AlertDialog builder instance
                android.app.AlertDialog customAlertDialog = alertDialog.create();

                // show the alert dialog when the button is clicked
                customAlertDialog.show();
            }
        });
    }
//Save or exit menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
//When the user clicks Save or back from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //When the user clicks from the save menu, a dialog will open asking if he is sure he wants to save
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please confirm")
                        .setMessage("Are you sure you want to save the contact?")
                        //In case the user chooses yes to save
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //The contact man kept only if a cold man and a phone number were added
                                if (!(nameEt.getText().toString().equals(""))&!(phoneEt.getText().toString().equals(""))) {
                                    ManagerContacts manager = ManagerContacts.getInstance(AddNewContactActivity.this);
                                    //Check if a phone number already exists
                                 boolean check =DB.checkKayInDB(phoneEt.getText().toString());
                                 //In case the phone number does not already exist
                                 if(check==false) {
                                     //If the user has not added an image to a contact
                                     //An automatically saved image will be added
                                     if (bitmap == null) {
                                         bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
                                     }
                                     //In case the user has not added whether he wants to block this contact - this contact will not be blocked
                                     if (blockTv.getText().toString() == "") {
                                         blockTv.setText("NO");

                                     }
                                    //Create a new contact object
                                     Contact contact = new Contact(nameEt.getText().toString(),
                                             phoneEt.getText().toString(),
                                             addressEt.getText().toString(),
                                             emailEt.getText().toString(),
                                             dateTv.getText().toString(),
                                             noteEt.getText().toString(),
                                             blockTv.getText().toString(),
                                             bitmap);
                                     //In case the user has added a birthday date to the contact
                                     if (dateTv.getText().toString() != "") {
                                         dateString = dateTv.getText().toString();
                                         //Cutting the birthday date into day and month
                                         words = dateString.split("/");
                                         //The name of his contact person belongs to the date of birth
                                         nameB = nameEt.getText().toString();
                                         //Check if there are calendar permissions for the application to enter the date in the calendar
                                         //If there is a log permission it will go to onRequestPermissionsResult
                                         ActivityCompat.requestPermissions(AddNewContactActivity.this,
                                                 new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                                                 0);
                                     }
                                     //Add the contact to a contact list
                                     manager.add_Contact(contact);
                                     //Save a contact in sqlite
                                     DB.save_Contact_in_sqlite(contact);

                                 }
                                 //In case the user tried to save a phone number that already exists
                                 else{
                                     Toast.makeText(AddNewContactActivity.this, "This number already exists", Toast.LENGTH_SHORT).show();
                                 }
                                }
                                //In case the user tried to save a phone number that already exists
                                else{
                                    Toast.makeText(AddNewContactActivity.this, "It is mandatory to enter a contact person and a telephone number", Toast.LENGTH_SHORT).show();
                                }
                                //A call to a function that clears the screen of the details of the saved contact
                                Clearing_the_details();
                            }
                         //A call to a function that clears the screen of the details of the saved contact
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(AddNewContactActivity.this, "Contact not saved", Toast.LENGTH_SHORT).show();
                    }
                }).show();
                break;
//In case the user selects the back menu
            case R.id.action_back:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }


    //Check if there are permissions on the calendar and enter a date in the calendar
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String day;
            String month;
            String year;
            int j = 0;
            // Permission is granted. Continue the action or workflow
            ContentResolver cr = getContentResolver();
            ContentValues cv = new ContentValues();
            //Day of the birthday date
            day = words[0];
            //month of the birthday date
            month = words[1];
            year=words[2];

            //Turning day and month into int
            int myDay = Integer.parseInt(day.replaceAll("[\\D]", ""));
            int myMonth = Integer.parseInt(month.replaceAll("[\\D]", ""));
            int my_year=Integer.parseInt(year.replaceAll("[\\D]", ""));
            String s = "birthday of  " + nameB;
           //A loop that puts in the diary the date of birth for the  80 years since he was born

            while (j !=80) {
                j++;
                cv.put(CalendarContract.Events.TITLE, s);
                cv.put(CalendarContract.Events.DESCRIPTION, "birthday");
                cv.put(CalendarContract.Events.EVENT_LOCATION, "birthday");
                Calendar start = Calendar.getInstance();
                //Birthday start date
                start.set(my_year, myMonth-1, myDay, 23, 0);
                Long startMillis = start.getTimeInMillis();
                cv.put(CalendarContract.Events.DTSTART, startMillis);
                Calendar end = Calendar.getInstance();
                //Birthday end date
                end.set(my_year, myMonth-1, myDay, 23, 30);
                Long endMillis = end.getTimeInMillis();
                cv.put(CalendarContract.Events.DTEND, endMillis);
                cv.put(CalendarContract.Events.CALENDAR_ID, 3);
                cv.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
                my_year++;
            }

        } else {
            // Explain to the user that the feature is unavailable because
            // the features requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
            Toast.makeText(this, "No log permissions were granted", Toast.LENGTH_SHORT).show();

        }

    }
    //The function clears the saved contact information
    private void Clearing_the_details()  {
        nameEt.setText("");
        phoneEt.setText("");
        addressEt.setText("");
        emailEt.setText("");
        dateTv.setText("");
        noteEt.setText("");
        blockTv.setText("");
        imageView.setImageBitmap(null);
    }
    //When the camera opens
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
//Receipt of the photographed photo
                        bitmap = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
                    }
                }
            });
}


