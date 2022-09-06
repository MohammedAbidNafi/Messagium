package com.margsapp.messageium.Authentication;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.margsapp.messageium.AppDetails.Terms_ConditionsActivity;
import com.margsapp.messageium.Model.User;
import com.margsapp.messageium.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Phone_setupActivity extends AppCompatActivity {

    private static final String WALLPAPER = "WALLPAPER";
    CircleImageView profile_image;

    AppCompatEditText username, dt;

    FirebaseUser firebaseUser;

    AppCompatButton next;

    DatabaseReference reference;

    private ProgressDialog loadingBar;

    private static final int GALLERY_PICK = 1;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_setup);

        profile_image = findViewById(R.id.DP);

        username = findViewById(R.id.username);
        dt = findViewById(R.id.dt);

        next = findViewById(R.id.next);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;

                username.setText(user.getUsername(), TextView.BufferType.EDITABLE);
                dt.setText(user.getDt(), TextView.BufferType.EDITABLE);
                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.drawable.clicky);
                } else {
                    Glide.with((getApplicationContext())).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages/"+firebaseUser.getUid());


        loadingBar = new ProgressDialog(this);
        profile_image.setOnClickListener(v -> openImage());

        next.setOnClickListener(v -> {

            String txt_username = Objects.requireNonNull(username.getText()).toString();
            String txt_dt = Objects.requireNonNull(dt.getText()).toString();


            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            SharedPreferences sharedPreferences = getSharedPreferences("wallpaper", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(WALLPAPER, "null");
            editor.apply();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", firebaseUser.getUid());
            hashMap.put("username", txt_username);
            hashMap.put("DT", txt_dt);
            hashMap.put("typingto","");
            reference1.updateChildren(hashMap).addOnCompleteListener(task -> startActivity(new Intent(Phone_setupActivity.this, Terms_ConditionsActivity.class)));
        });
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


                StorageTask<UploadTask.TaskSnapshot> uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Phone_setupActivity.this, "Profile Picture has updated.", Toast.LENGTH_SHORT).show();

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
        ContentResolver contentResolver = Phone_setupActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}