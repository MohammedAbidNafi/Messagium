package com.margsapp.messageium.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.emredavarci.noty.Noty;
import com.google.android.gms.ads.interstitial.InterstitialAd;
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
import com.margsapp.messageium.Main.MainActivity;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.R;
import com.margsapp.messageium.Settings.AboutActivity;
import com.margsapp.messageium.Settings.Chat_settings;
import com.margsapp.messageium.Settings.CustomiseActivity;
import com.margsapp.messageium.Settings.EditStatusActivity;
import com.margsapp.messageium.Settings.edit_profile;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private static final String TAG = "edit_profile";
    CircleImageView profile_image;

    TextView username, status, joined_on;

    FirebaseUser firebaseUser;


    CardView Status_card, Account_card, Customize_card, About_card,Contact_card;


    RelativeLayout mainlayout;

    DatabaseReference reference;

    StorageReference storageReference;
    private static final int GALLERY_PICK = 1;

    private ProgressDialog loadingBar;


    Dialog dialog;
    private static final int RC_PHOTO_PICKER =  105;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        status = view.findViewById(R.id.status);
        username = view.findViewById(R.id.username);
        joined_on = view.findViewById(R.id.joined_on);
        profile_image = view.findViewById(R.id.profile_image);
        Status_card = view.findViewById(R.id.status_card);
        Account_card = view.findViewById(R.id.fragment_chat_settings);
        Customize_card = view.findViewById(R.id.Customize_card);
        About_card = view.findViewById(R.id.About_card);
        Contact_card = view.findViewById(R.id.Contact_card);


        Status_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Status_card.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                } else {
                    Status_card.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });

        Status_card.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditStatusActivity.class);

            startActivity(intent);
        });


        Account_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Account_card.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                } else {
                    Account_card.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });

        Account_card.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Chat_settings.class);
            startActivity(intent);


        });

        Customize_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Customize_card.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                } else {
                    Customize_card.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });

        Customize_card.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CustomiseActivity.class);

            startActivity(intent);


        });

        About_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    About_card.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                } else {
                    About_card.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });
        About_card.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AboutActivity.class);
            startActivity(intent);


        });

        Contact_card.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Contact_card.setCardBackgroundColor(getResources().getColor(R.color.onCardClick));

                } else {
                    Contact_card.setCardBackgroundColor(getResources().getColor(R.color.card_back));

                }
                return false;
            }
        });
        Contact_card.setOnClickListener(v -> {
            sendEmail();
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("/ProfileImages/" + firebaseUser.getUid());


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
                    Glide.with((getContext())).load(user.getImageUrl()).into(profile_image);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadingBar = new ProgressDialog(getContext());

        profile_image.setOnClickListener(v -> onMediaSelect());

        return view;
    }

    @SuppressLint("IntentReset")
    private void sendEmail() {

        String to =  "margsglobal@gmail.com";

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setData(Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        email.putExtra(Intent.EXTRA_SUBJECT, "Messengeium Support Reference ID: " + ReferenceID());
        email.putExtra(Intent.EXTRA_TEXT, "Please describe your issue or Describe the Bug/Glicth you faced or Suggest a feature which will improve this service");

        try {
            startActivity(email);

        }catch (ActivityNotFoundException activityNotFoundException){
            Toast.makeText(getContext(),"An Error occured Error code 0x08050101",Toast.LENGTH_SHORT).show();

        }
    }

    public int ReferenceID(){

        int min = 1000000;
        int max = 9999999;

        return (int)(Math.random()*(max-min+1)+min);


    }



    public void onMediaSelect(){


        AppCompatButton gallery,camera;

        CardView cancel;

        dialog.setContentView(R.layout.add_image_pop_up);




        dialog.setCancelable(false);



        gallery = dialog.findViewById(R.id.gallery);

        camera = dialog.findViewById(R.id.camera);

        cancel = dialog.findViewById(R.id.cancel);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                        RC_PHOTO_PICKER);
                dialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Camera!",Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });







        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(getActivity());

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

                            Noty.init(getContext(),"Photo updated Success",mainlayout, Noty.WarningStyle.ACTION)
                                    .setWarningBoxBgColor("#4BB543")
                                    .setWarningBoxRadius(80,80,80,80)
                                    .setWarningBoxMargins(15,15,15,10)
                                    .setAnimation(Noty.RevealAnim.SLIDE_UP, Noty.DismissAnim.BACK_TO_BOTTOM, 400,400)
                                    .setWarningBoxPosition(Noty.WarningPos.BOTTOM)
                                    .show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            String mUri = downloadUri.toString();


                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.updateChildren(map);

                            loadingBar.dismiss();

                            getActivity().recreate();

                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Profile Picture updation is cancelled", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }


        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
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




}