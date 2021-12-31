package com.margsapp.messageium.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.margsapp.messageium.Main.MessageActivity;
import com.margsapp.messageium.R;

import java.util.Objects;

public class personal_dpActivity extends AppCompatActivity {

    ImageView dpView;

    String imageurl;

    String userid;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_personal_dp);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        imageurl = intent.getStringExtra("data");

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(personal_dpActivity.this, MessageActivity.class);
            intent.putExtra("userid",userid);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<View, String>(dpView, "imageTransition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(personal_dpActivity.this, pairs);
            startActivity(intent,options.toBundle());
            finish();
        });

        dpView = findViewById(R.id.dpview);


        if(imageurl.equals("default"))
        {
            dpView.setImageResource(R.drawable.user);

        }
        else {
            Glide.with(getApplicationContext()).load(imageurl).into(dpView);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(personal_dpActivity.this, MessageActivity.class);
        intent.putExtra("userid",userid);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(dpView, "imageTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(personal_dpActivity.this, pairs);
        startActivity(intent,options.toBundle());
        finish();


    }
}