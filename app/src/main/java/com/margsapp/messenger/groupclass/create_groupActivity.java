package com.margsapp.messenger.groupclass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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
import com.margsapp.messenger.Dp_viewActivity;
import com.margsapp.messenger.MainActivity;
import com.margsapp.messenger.MessageActivity;
import com.margsapp.messenger.Model.Group;
import com.margsapp.messenger.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class create_groupActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;
    CircleImageView group_image;
    AppCompatButton Next;

    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference reference, databaseReference;
    AppCompatEditText group_name;

    boolean image;

    String image_url;

    private Uri imageUri;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(create_groupActivity.this, MainActivity.class));
            }
        });

        group_image = findViewById(R.id.group_img);
        group_name = findViewById(R.id.groupname);
        Next = findViewById(R.id.next);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("GroupImages/"+ Objects.requireNonNull(group_name.getText()).toString());



        loadingBar = new ProgressDialog(this);

        group_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });



        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_groupname = Objects.requireNonNull(group_name.getText()).toString();

                String image_;
                if(image){
                     image_ = image_url;
                }else {
                     image_ = "default";
                }

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
                String timestamp = simpleDateFormat.format(calendar.getTime());
                reference = FirebaseDatabase.getInstance().getReference("Group");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Group group = snapshot.getValue(Group.class);
                        if(snapshot.exists()){
                            if(txt_groupname.equals(group.getGroupName())){
                                Toast.makeText(create_groupActivity.this,"Group Name already exists. Error code 0x08090101",Toast.LENGTH_SHORT).show();
                            }else {
                                if(TextUtils.isEmpty(txt_groupname)){
                                    Toast.makeText(create_groupActivity.this,"Please enter group name. Error code 0x08090102",Toast.LENGTH_SHORT).show();
                                }else {
                                    creategroup(txt_groupname,image_,timestamp);
                                }
                            }
                        }else {
                            if(TextUtils.isEmpty(txt_groupname)){
                                Toast.makeText(create_groupActivity.this,"Please enter group name. Error code 0x08090102",Toast.LENGTH_SHORT).show();
                            }else {
                                creategroup(txt_groupname,image_,timestamp);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });


    }

    private void creategroup(String txt_groupname, String image_, String timestamp) {

        databaseReference = FirebaseDatabase.getInstance().getReference("Grouplist").child(txt_groupname).child("members").child(firebaseUser.getUid());

        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put("id", firebaseUser.getUid());
        hashMap1.put("admin","true");
        databaseReference.setValue(hashMap1);


        reference = FirebaseDatabase.getInstance().getReference("Group").child(txt_groupname);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupName", txt_groupname);
        hashMap.put("imageURL", image_);
        hashMap.put("createdon",timestamp);
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(create_groupActivity.this, AddParticipants.class);
                intent.putExtra("GroupID", txt_groupname);
                startActivity(intent);
            }
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
            imageUri = data.getData();
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


                uploadTask = filepath.putFile(resultUri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(create_groupActivity.this, "Image has been stored in our servers", Toast.LENGTH_SHORT).show();

                            Uri downloadUri = task.getResult();
                            assert downloadUri != null;
                            image_url = downloadUri.toString();
                            image = true;

                            Glide.with((getApplicationContext())).load(image_url).into(group_image);

                            //Update Database
                            /*reference = FirebaseDatabase.getInstance().getReference("Group").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", mUri);
                            reference.updateChildren(map);

                             */

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
        ContentResolver contentResolver = create_groupActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(create_groupActivity.this, MainActivity.class));
    }
}