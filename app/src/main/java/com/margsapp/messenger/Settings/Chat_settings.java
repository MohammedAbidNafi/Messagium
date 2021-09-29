package com.margsapp.messenger.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.margsapp.messenger.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class Chat_settings extends AppCompatActivity {

    CardView Accepted;
    CardView Blocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);

        SlidrInterface slidrInterface = Slidr.attach(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.people));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

            }
        });

        Accepted = findViewById(R.id.Accepted);

        Blocked = findViewById(R.id.Blocked);


        Accepted.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    Accepted.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                }else {
                    Accepted.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });

        Accepted.setOnClickListener(v -> {
            Intent intent = new Intent(Chat_settings.this, com.margsapp.messenger.Friends.Accepted.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
        });

        Blocked.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)  {
                    Blocked.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                }else {
                    Blocked.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });


        Blocked.setOnClickListener(v -> {
            Intent intent = new Intent(Chat_settings.this, com.margsapp.messenger.Friends.Blocked.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
        });


    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.activity_slider_in_right, R.anim.activity_slider_out_left);

    }

    private void status(String status){
        FirebaseUser firebaseUserStatus = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUserStatus != null;
        DatabaseReference statusdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUserStatus.getUid());

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd yy hh:mm aa");
        String timestamp = simpleDateFormat.format(calendar.getTime());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        hashMap.put("lastseen", timestamp);

        statusdatabaseReference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    protected void onDestroy() {
        super.onDestroy();
        status("offline");
    }
}