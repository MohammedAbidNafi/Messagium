package com.margsapp.messenger.ImageView;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.iosdialog.iOSDialog;
import com.margsapp.iosdialog.iOSDialogListener;
import com.margsapp.messenger.Authentication.VerifyOTP;
import com.margsapp.messenger.Main.MessageActivity;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.margsapp.messenger.groupclass.group_messageActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class chat_image_viewActivity extends AppCompatActivity {

    ImageView imageView;

    TextView usernameView;// timestampView;

    Intent intent;
    String username;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String idtosend = "";

    DatabaseReference reference;

    StorageReference storageReference;


    LinearLayout download, wallpaper,profile, share;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image_view);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        download = findViewById(R.id.download_image);
        wallpaper = findViewById(R.id.set_as_wallpaper);
        profile = findViewById(R.id.profile_image);
        share = findViewById(R.id.share_image);

        intent = getIntent();


        String senderid = intent.getStringExtra("senderid");
        //String timestamp = intent.getStringExtra("timestamp");
        String extraid = intent.getStringExtra("extraid");

        String firebaseuser_ = firebaseUser.getUid();

        String imageuri = intent.getStringExtra("imageuri");

        usernameView = findViewById(R.id.username);

        //timestampView = findViewById(R.id.timestamp);

       // timestampView.setText(timestamp);


        loadingBar = new ProgressDialog(chat_image_viewActivity.this);

        if(firebaseuser_.equals(senderid)){
            idtosend = extraid;

            usernameView.setText("You");
            username = "You";

        }else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(idtosend);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    username = user.getUsername();

                    usernameView.setText(user.getUsername());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
            idtosend = senderid;
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String finalIdtosend = idtosend;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        imageView = findViewById(R.id.chatimage);

        Glide.with(chat_image_viewActivity.this).load(imageuri).into(imageView);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareImageandText(imageuri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(chat_image_viewActivity.this,"Something went wrong! Error Code 0x08040204",Toast.LENGTH_SHORT).show();


                }
            }
        });


        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iOSDialog.Builder
                        .with(chat_image_viewActivity.this)
                        .setTitle(getApplicationContext().getResources().getString(R.string.wallpaper_title))
                        .setMessage(getApplicationContext().getResources().getString(R.string.wallpaper_ask))
                        .setPositiveText(getApplicationContext().getResources().getString(R.string.yes))
                        .setPostiveTextColor(getApplicationContext().getResources().getColor(R.color.red))
                        .setNegativeText(getApplicationContext().getResources().getString(R.string.cancel))
                        .setNegativeTextColor(getApplicationContext().getResources().getColor(R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                try {
                                    setWallpaper(imageuri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do Nothing
                            }
                        })
                        .isCancellable(true)
                        .build()
                        .show();

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(imageuri,username);
                loadingBar.setTitle("Downloading");
                loadingBar.setMessage("Downloading and saving your image in gallery");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOSDialog.Builder
                        .with(chat_image_viewActivity.this)
                        .setTitle(getResources().getString(R.string.profile_picture))
                        .setMessage(getResources().getString(R.string.ask_to_setProfile))
                        .setPositiveText(getResources().getString(R.string.yes))
                        .setPostiveTextColor(getResources().getColor(R.color.red))
                        .setNegativeText(getResources().getString(R.string.cancel))
                        .setNegativeTextColor(getResources().getColor(R.color.company_blue))
                        .onPositiveClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                setProfilePicture(imageuri);
                            }
                        })
                        .onNegativeClicked(new iOSDialogListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                //Do Nothing
                            }
                        })
                        .isCancellable(true)
                        .build()
                        .show();


            }
        });



    }

    private void setProfilePicture(String imageUri) {

        loadingBar.setTitle("Profile Image");
        loadingBar.setMessage("Please wait, while we update your profile picture...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);


        StorageReference filepath = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));


        StorageTask uploadTask = filepath.putFile(Uri.parse(imageUri));
        uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return filepath.getDownloadUrl();
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(chat_image_viewActivity.this, "Profile Picture has updated.", Toast.LENGTH_SHORT).show();

                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    String mUri = downloadUri.toString();


                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageURL", mUri);
                    reference.updateChildren(map);

                } else {
                    Toast.makeText(chat_image_viewActivity.this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();

                }
                loadingBar.dismiss();

            }
        });
    }

    private String getFileExtension(String uri){
        ContentResolver contentResolver = chat_image_viewActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(Uri.parse(uri)));
    }


    private void shareImageandText(String imageurl) throws IOException {

        URL url = new URL(imageurl);
        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), image, "Image Description", null);
        Uri uri = Uri.parse(path);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    private void DownloadImage(String imageurl,String sendername) {

        try {
            String title = randomString(6);
            URL url = new URL(imageurl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            MediaStore.Images.Media.insertImage(getContentResolver(), image, title, sendername + "sent you this image in MessengerByMargs");


        } catch(IOException e) {
            System.out.println(e);
            Toast.makeText(chat_image_viewActivity.this,"Something went wrong! Error Code 0x08040201",Toast.LENGTH_SHORT).show();

        }

        loadingBar.dismiss();

    }

    private void setWallpaper(String imageurl) throws IOException {
        URL url = new URL(imageurl);
        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try{
            manager.setBitmap(image);
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error! 0x08040202", Toast.LENGTH_SHORT).show();
        }
    }

    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static Random RANDOM = new Random();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }



    public void onBackPressed(){

        finish();

    }
}