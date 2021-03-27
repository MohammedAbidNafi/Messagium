package com.margsapp.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ViewUtils;

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
import com.margsapp.messenger.Model.Group;

import java.util.Objects;

public class group_infoActivity extends AppCompatActivity {

    ImageView group_img;

    Intent intent;
    String groupname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Group Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        group_img = findViewById(R.id.group_img);
        intent = getIntent();
        groupname = intent.getStringExtra("groupname");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Group").child(groupname);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                String imageUrl = group.getImageUrl();
                if(imageUrl.equals("default")){
                    group_img.setImageResource(R.drawable.groupicon);
                }else {
                    Glide.with(getApplicationContext()).load(group.getImageUrl()).into(group_img);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        group_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_img.getDrawable();

            }
        });
    }
}