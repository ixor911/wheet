package com.example.wheet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView variantText;
    private Button DBButton, contactsButton, geoServiceButton;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        variantText = findViewById(R.id.mainHeaderText);
        DBButton = findViewById(R.id.mainDBButton);
        contactsButton = findViewById(R.id.mainContactsButton);
        geoServiceButton = findViewById(R.id.mainGeoServiceButton);

        DBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DBActivity.class);
                switchActivities(intent);
            }
        });

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                switchActivities(intent);
            }
        });

        geoServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                switchActivities(intent);
            }
        });

    }

    private void switchActivities(Intent intent) {
        startActivity(intent);
    }
}