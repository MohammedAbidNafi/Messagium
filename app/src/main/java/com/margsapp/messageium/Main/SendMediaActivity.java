package com.margsapp.messageium.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messageium.R;
import com.margsapp.messageium.utils.AES;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SendMediaActivity extends AppCompatActivity {

    ImageView mediaview;

    ImageView cancel;

    EditText text;

    ImageButton btn_send;

    Uri imageUri;

    ImageView imageView;

    Intent intent;

    StorageReference storageReference;

    String userid;

    private ProgressDialog loadingBar;

    DatabaseReference reference;

    private AES aes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media);


        aes = new AES(this);

        intent = getIntent();


        imageUri = Uri.parse(intent.getStringExtra("imageUri"));

        userid = intent.getStringExtra("userid");

        btn_send = findViewById(R.id.btn_send);

        text = findViewById(R.id.text_send);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loadingBar = new ProgressDialog(this);


        imageView = findViewById(R.id.imageview);

        Glide.with(SendMediaActivity.this).load(imageUri).into(imageView);




        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBar.setTitle("GIF");
                loadingBar.setMessage("Please wait, while we send GIF...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy  HH:mm");
                String timestamp = simpleDateFormat.format(calendar.getTime());

                String message = text.getText().toString();

                String sender = firebaseUser.getUid();
                String receiver = userid;

                String imageUrl = imageUri.toString();


                reference = FirebaseDatabase.getInstance().getReference("Chats");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", sender);
                hashMap.put("receiver", receiver);
                hashMap.put("message","GIF");
                hashMap.put("reply","gif");
                hashMap.put("type","gif");
                hashMap.put("imageurl", imageUrl);
                hashMap.put("isseen", "false");
                hashMap.put("timestamp", timestamp);
                reference.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(!message.equals("")){

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            String sendername = intent.getStringExtra("sendername");
                            String sender = firebaseUser.getUid();
                            String receiver = userid;

                            String encryptedmessage = aes.Encrypt(message, SendMediaActivity.this);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", sender);
                            hashMap.put("receiver", receiver);
                            hashMap.put("message", encryptedmessage);
                            hashMap.put("isseen", "false");
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("reply", "false");
                            hashMap.put("sendername", sendername);

                            reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    finish();
                                    loadingBar.dismiss();
                                }
                            });

                        }else {
                            finish();
                            loadingBar.dismiss();
                        }


                    }

                });


            }

        });
    }
}