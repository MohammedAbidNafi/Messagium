package com.margsapp.messenger.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class EditStatusActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    EditText statusEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);

        statusEditText = findViewById(R.id.editText);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.edit_status));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                statusEditText.setText(user.getDt());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void save(String txt_status) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        assert firebaseUser != null;
        String userid = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {




                HashMap<String, Object> hash = new HashMap<>();
                hash.put("DT", txt_status);
                reference.updateChildren(hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Intent intent = new Intent(EditStatusActivity.this, edit_profile.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);
                        finish();
                    }
                });



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuinstatus, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.done) {
            String txt_status = statusEditText.getText().toString();
            save(txt_status);



        }

        return false;
    }




    public void onBackPressed(){
        String txt_status = statusEditText.getText().toString();
        save(txt_status);
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