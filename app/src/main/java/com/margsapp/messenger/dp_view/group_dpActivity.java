package com.margsapp.messenger.dp_view;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.group_infoActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class group_dpActivity extends AppCompatActivity {

    ImageView dpView;

    String imageurl;

    Intent intent;
    String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_dp_view);

        intent = getIntent();
        imageurl = intent.getStringExtra("data");
        groupid = intent.getStringExtra("groupid");
        String groupname = intent.getStringExtra("groupname");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(groupname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(group_dpActivity.this, group_infoActivity.class);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(dpView, "imageTransition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(group_dpActivity.this, pairs);
            intent.putExtra("groupid",groupid);
            startActivity(intent, options.toBundle());

        });


        dpView = findViewById(R.id.dpview);



        if(imageurl.equals("group_default")){
            dpView.setImageResource(R.drawable.groupicon);
        }
        else {
            Glide.with(getApplicationContext()).load(imageurl).into(dpView);
        }



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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(group_dpActivity.this, group_infoActivity.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(dpView, "imageTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(group_dpActivity.this, pairs);
        intent.putExtra("groupid",groupid);
        startActivity(intent, options.toBundle());
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