package com.example.wheet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.provider.ContactsContract;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.acl.Permission;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {
    private int CONTACTS_READ_PERMISSION = 1;
    private LinearLayout contactsTable, contactsNeedTable;
    private Button returnButton;

    ArrayList<String> data = null;
    ArrayList<String> needData = null;


    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contactsTable = findViewById(R.id.ContactsMainTable);
        contactsNeedTable = findViewById(R.id.ContactsNeedTable);
        returnButton = findViewById(R.id.ContactsReturnButton);

        if (ContextCompat.checkSelfPermission(ContactsActivity.this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            data = getContactsData();
            createTable(data, contactsTable);

            needData = getDataEndsWith(data, "a");
            createTable(needData, contactsNeedTable);

        } else {
            requestContactsPermission();
        }


        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                switchActivity(intent);
            }
        });

    }

    private void requestContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS}, CONTACTS_READ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CONTACTS_READ_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("Range")
    private ArrayList<String> getContactsData() {
        ArrayList<String> contacts = new ArrayList<>();

        Cursor cursorAndroidContacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursorAndroidContacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursorAndroidContacts.getCount() > 0) {
            while (cursorAndroidContacts.moveToNext()) {
                String contactName = cursorAndroidContacts.getString(
                        cursorAndroidContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                contacts.add(contactName);
            }
        }

        return contacts;
    }


    @SuppressLint("SetTextI18n")
    private void createTable(ArrayList<String> data, LinearLayout layout) {

        for(String record: data) {
            TextView viewNameText = new TextView(this);
            viewNameText.setText(record);
            setTextViewParams(viewNameText);

            layout.addView(viewNameText);
        }
    }

    private void setTextViewParams(TextView view) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(param);
        view.setTextSize(20);
        //view.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    }

    private ArrayList<String> getDataEndsWith(ArrayList<String> data, String endSymbol) {
        ArrayList<String> sortedData = new ArrayList<>();

        for(String record: data) {
            if (record.endsWith(endSymbol))
                sortedData.add(record);
        }

        return sortedData;
    }

    private void switchActivity(Intent intent) {
        try {
            finish();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Cant switch activity", Toast.LENGTH_LONG).show();
        }
    }
}
