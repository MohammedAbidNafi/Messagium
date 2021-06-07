package com.margsapp.messenger.Settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messenger.Main.MainActivity;
import com.margsapp.messenger.Model.User;
import com.margsapp.messenger.R;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.margsapp.messenger.Settings.CustomiseActivity.THEME;

public class edit_profile extends AppCompatActivity {


    private static final String TAG = "edit_profile";
    CircleImageView profile_image;

    TextView username, status, joined_on;

    FirebaseUser firebaseUser;


    CardView Status_card, Account_card, Customize_card, About_card;



    DatabaseReference reference;

    StorageReference storageReference;
    private static final int GALLERY_PICK = 1;

    private ProgressDialog loadingBar;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("theme", 0);
        String Theme = preferences.getString(THEME, "");
        if(Theme.equals("2")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        if(Theme.equals("1")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(Theme.equals("0")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        SlidrInterface slidrInterface = Slidr.attach(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5615682506938042/5865260490", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

        AdView mAdView = findViewById(R.id.adView);

        mAdView.loadAd(adRequest);

         */

        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(edit_profile.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
            overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);
            /*if (mInterstitialAd != null) {
                mInterstitialAd.show(edit_profile.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

             */
        });




        status = findViewById(R.id.status);
        username = findViewById(R.id.username);
        joined_on = findViewById(R.id.joined_on);
        profile_image = findViewById(R.id.profile_image);
        Status_card = findViewById(R.id.status_card);
        Account_card = findViewById(R.id.fragment_chat_settings);
        Customize_card = findViewById(R.id.Customize_card);
        About_card = findViewById(R.id.About_card);


        Status_card.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, EditStatusActivity.class);

            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

            if (mInterstitialAd != null) {
                mInterstitialAd.show(edit_profile.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        });

        Account_card.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, Chat_settings.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

            if (mInterstitialAd != null) {
                mInterstitialAd.show(edit_profile.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }


        });

        Customize_card.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, CustomiseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);


            if (mInterstitialAd != null) {
                mInterstitialAd.show(edit_profile.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

        });

        About_card.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_slide_in_left,R.anim.activity_slider_out_right);

            /*
            if (mInterstitialAd != null) {
                mInterstitialAd.show(edit_profile.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

             */

        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("/ProfileImages/"+firebaseUser.getUid());


        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                assert user != null;
                username.setText(user.getUsername());


                long joineDate = Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getMetadata()).getCreationTimestamp();
                String actual_date = DateFormat.getDateInstance().format(joineDate);
                joined_on.setText(actual_date);

                status.setText(user.getDt());


                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.drawable.user);
                } else {
                    Glide.with((getApplicationContext())).load(user.getImageUrl()).into(profile_image);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadingBar = new ProgressDialog(this);

        profile_image.setOnClickListener(v -> openImage());



    }

    public void onBackPressed(){
        Intent intent = new Intent(edit_profile.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slider_in_right,R.anim.activity_slider_out_left);


        if (mInterstitialAd != null) {
            mInterstitialAd.show(edit_profile.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }


    }



    private void openImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we update your profile picture...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);
                assert result != null;
                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(resultUri));


                StorageTask uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(edit_profile.this, "Image has been stored in our servers", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();


                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.updateChildren(map);

                            loadingBar.dismiss();

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = edit_profile.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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
