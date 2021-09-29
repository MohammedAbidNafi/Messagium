package com.margsapp.messenger.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.margsapp.messenger.R;
import com.margsapp.messenger.utils.AES;

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

        btn_send = findViewById(R.id.btn_send);

        text = findViewById(R.id.text_send);


        imageView = findViewById(R.id.imageview);

        imageView.setImageURI(imageUri);

    }
}