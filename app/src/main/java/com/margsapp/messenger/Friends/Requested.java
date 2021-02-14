package com.margsapp.messenger.Friends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.margsapp.messenger.Chat_settings;
import com.margsapp.messenger.R;

public class Requested extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested);
    }

    public void onBackPressed(){
        startActivity(new Intent(Requested.this, Chat_settings.class));
    }
}