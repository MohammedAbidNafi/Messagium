package com.margsapp.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.margsapp.messenger.Friends.Blocked;
import com.margsapp.messenger.Friends.Requested;
import com.margsapp.messenger.Friends.Requests;


public class Chat_settings extends AppCompatActivity {

    CardView Accepted, Requested, Requests, Blocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("People");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(Chat_settings.this, edit_profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        Accepted = findViewById(R.id.Accepted);
        Requested = findViewById(R.id.Requested);
        Requests = findViewById(R.id.Requests);
        Blocked = findViewById(R.id.Blocked);

        Accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_settings.this, com.margsapp.messenger.Friends.Accepted.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_settings.this, com.margsapp.messenger.Friends.Requested.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_settings.this, Requests.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Blocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat_settings.this, com.margsapp.messenger.Friends.Blocked.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }
}