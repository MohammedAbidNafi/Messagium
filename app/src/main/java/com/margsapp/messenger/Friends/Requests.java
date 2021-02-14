package com.margsapp.messenger.Friends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.margsapp.messenger.Chat_settings;
import com.margsapp.messenger.R;

public class Requests extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
    }

    public void onBackPressed(){
        startActivity(new Intent(Requests.this, Chat_settings.class));
    }
}