package com.margsapp.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.User;

import java.util.HashMap;
import java.util.Objects;

public class EditStatusActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    EditText statusEditText;

    AppCompatButton saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        statusEditText = findViewById(R.id.editText);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(EditStatusActivity.this, edit_profile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));




        saveButton = findViewById(R.id.savebutton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_status = statusEditText.getText().toString();


                if(txt_status.equals("")){
                    save("Hey there am using Messenger");
                }else
                {
                    save(txt_status);
                }

                Intent intent = new Intent(EditStatusActivity.this, edit_profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });


    }


    private void save(String txt_status) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        assert firebaseUser != null;
        String userid = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                HashMap<String, Object> hash = new HashMap<>();
                hash.put("DT", txt_status);
                reference.updateChildren(hash);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    public void onBackPressed(){
        Intent intent = new Intent(EditStatusActivity.this, edit_profile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }
}