package com.margsapp.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.User;

import java.util.Objects;

public class Dp_viewActivity extends AppCompatActivity {

    ImageView dpView;

    String userid;

    Intent intent;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dp_view);


        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dp_viewActivity.this, MessageActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

        dpView = findViewById(R.id.dpview);

        intent = getIntent();
        userid = intent.getStringExtra("userid");


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;

                if(user.getImageUrl().equals("default"))
                {
                    dpView.setImageResource(R.drawable.user);

                }
                else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(dpView);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void onBackPressed(){
        Intent intent = new Intent(Dp_viewActivity.this, MessageActivity.class);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
}