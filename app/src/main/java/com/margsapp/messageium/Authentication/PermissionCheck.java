package com.margsapp.messageium.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emredavarci.noty.Noty;
import com.margsapp.messageium.R;

import java.util.Objects;

public class PermissionCheck extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 101;
    private static final int CONTACTS_PERMISSION_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 103;
    private static final int SMS_PERMISSION_CODE = 104;

    int permission = 0;

    SwitchCompat camera,mircophone,contacts,storage,sms;

    AppCompatButton continue_btn;

    RelativeLayout mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_check);


        camera = findViewById(R.id.camera);
        mircophone = findViewById(R.id.microphone);
        contacts = findViewById(R.id.contacts);
        storage = findViewById(R.id.storage);
        sms = findViewById(R.id.SMS);

        continue_btn = findViewById(R.id.continue_btn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.grant_permission));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mainlayout = findViewById(R.id.mainlayout);



        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE,camera);
        checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE,mircophone);
        checkPermission(Manifest.permission.READ_CONTACTS, CONTACTS_PERMISSION_CODE,contacts);
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE,storage);
        checkPermission(Manifest.permission.READ_SMS, SMS_PERMISSION_CODE,sms);



        camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE,camera);

            }
        });

        mircophone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE,mircophone);
            }
        });

        contacts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkPermission(Manifest.permission.READ_CONTACTS, CONTACTS_PERMISSION_CODE,contacts);
            }
        });

        storage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE,storage);
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE,storage);
            }
        });

        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkPermission(Manifest.permission.READ_SMS, SMS_PERMISSION_CODE,sms);
            }
        });


        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyPermission();
            }
        });


    }

    public void checkPermission(String permission, int requestCode,SwitchCompat switchCompat)
    {
        if (ContextCompat.checkSelfPermission(PermissionCheck.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(PermissionCheck.this, new String[] { permission }, requestCode);
        }
        else {

            switchCompat.setChecked(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkbox(1);
            }
            else {
                uncheckbox(1);
            }
        }

        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkbox(2);

            }
            else {

                uncheckbox(2);
            }
        }

        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                checkbox(3);
            }
            else {

                uncheckbox(3);
            }
        }

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                checkbox(4);
            }
            else {

                uncheckbox(4);
            }
        }

        else if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkbox(5);

            } else {

                uncheckbox(5);
            }
        }
    }

    public void VerifyPermission(){

        if(camera.isChecked() & mircophone.isChecked() & contacts.isChecked() & storage.isChecked() & sms.isChecked()){
            continue_btn.setVisibility(View.VISIBLE);
            startActivity(new Intent(PermissionCheck.this, StartActivity.class));
        }
        else {
            Noty.init(PermissionCheck.this,"Accept all permissions.",mainlayout, Noty.WarningStyle.ACTION)
                    .setWarningBoxBgColor("#F70000")
                    .setWarningBoxRadius(80,80,80,80)
                    .setWarningBoxMargins(15,15,15,10)
                    .setAnimation(Noty.RevealAnim.SLIDE_DOWN, Noty.DismissAnim.BACK_TO_BOTTOM, 500,500)
                    .setWarningBoxPosition(Noty.WarningPos.BOTTOM)
                    .show();
        }
    }

    private void checkbox(int switch_) {

        if(switch_ == 1){
            camera.setChecked(true);

        }

        if(switch_ == 2){
            mircophone.setChecked(true);

        }

        if(switch_ == 3){
            contacts.setChecked(true);

        }

        if(switch_ == 4){
            storage.setChecked(true);

        }

        if(switch_ == 5){
            sms.setChecked(true);

        }

    }

    private void uncheckbox(int switch_) {

        if(switch_ == 1){
            camera.setChecked(false);
        }

        if(switch_ == 2){
            mircophone.setChecked(false);
        }

        if(switch_ == 3){
            contacts.setChecked(false);
        }

        if(switch_ == 4){
            storage.setChecked(false);
        }

        if(switch_ == 5){
            sms.setChecked(false);
        }

    }



}